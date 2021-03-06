package Comunication.RMIHandlers;

import Comunication.RMIInterfaces.RMIHeartbeatInterface;
import Utils.Logger;
import Utils.StringUtils;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

public class RMIHeartbeatService extends UnicastRemoteObject implements RMIHeartbeatInterface, Serializable {
    public static final String RMI_HEARTBEAT_SERVICE_NAME = "RMIHBS";
    private final LinkedList<IDPair> IDtoHeartbeatCount = new LinkedList<>();//Maybe quicker, maybe not
    private IDPair oldestPair = null;

    private String DBServerIP;
    private String DBServerPort;
    private String DBServerUser;
    private String DBServerPassword;

    private RMIWatchdogThread watchdog;

    public RMIHeartbeatService(String DBServerIP, String DBServerPort, String DBServerUser, String DBServerPassword) throws RemoteException {
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            Naming.rebind("rmi://localhost/" + RMI_HEARTBEAT_SERVICE_NAME, this);

            this.DBServerIP = DBServerIP;
            this.DBServerPort = DBServerPort;
            this.DBServerUser = DBServerUser;
            this.DBServerPassword = DBServerPassword;

            watchdog = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] hearbeatMethod(String ID, String IPAddress) {
        Logger.logVerbose("Heartbeat service", "Heartbeat called!");
        IDPair ipd = findID(ID);
        if (ipd == null) {//ID does not exist
            IDtoHeartbeatCount.add(new IDPair(ID, 0, IPAddress));
        }
        increaseHeartbeats(ID);//SIDE EFFECT : Changes oldestPair
        if (watchdog == null) {
            watchdog = new RMIWatchdogThread();
            watchdog.start();
        }
        Logger.logDebug("Heartbeat service", "Oldest game server is " + oldestPair.getID() + "@" + oldestPair.getIPAddress());
        if (oldestPair.getID().equals(ID)) {
            return new String[]{DBServerIP, DBServerPort, DBServerUser, DBServerPassword};
        } else {
            return null;
        }
    }

    private void increaseHeartbeats(String id) {
        synchronized (IDtoHeartbeatCount) {
            IDPair idp = findID(id);
            if (idp != null) {
                idp.incHeartbeats();
                if (oldestPair == null || idp.getHeartbeatCount() > oldestPair.getHeartbeatCount()) {
                    oldestPair = idp;
                }
            }
        }
    }

    private IDPair findID(String id) {
        for (IDPair idp : IDtoHeartbeatCount) {
            if (idp.getID().equals(id)) {
                return idp;
            }
        }
        return null;
    }


    private boolean IDExists(String id) {
        return findID(id) != null;
    }

    public String getGameServerIP() {
        return oldestPair.getIPAddress();
    }

    private class IDPair implements Serializable {
        private String ID;
        private int HeartbeatsCount = 0;
        private String IPAddress;
        private String heartbeatSignature;

        IDPair(String ID, int initialCount, String IPAddress) {
            this.ID = ID;
            this.HeartbeatsCount = initialCount;
            this.IPAddress = IPAddress;
            heartbeatSignature = StringUtils.RandomAlfa(64);
        }

        IDPair(String ID, String IPAddress) {
            this(ID, 0, IPAddress);
        }

        private String getID() {
            return ID;
        }

        private void incHeartbeats() {
            incHeartbeats(1);
        }

        private void incHeartbeats(int i) {
            this.HeartbeatsCount += i;
            heartbeatSignature = StringUtils.RandomAlfa(64);
        }

        private int getHeartbeatCount() {
            return HeartbeatsCount;
        }

        private String getIPAddress() {
            return IPAddress;
        }

        private void setHeartbeats(int heartbeats) {
            this.HeartbeatsCount = heartbeats;
        }
    }

    private class RMIWatchdogThread extends Thread {
        private String lastOldestPairSignature = null;
        private int maxFailures = 3;
        private int currentConsecutiveFailures = 0;
        private int cumulativeFailures = 0;//statistics and stuff

        @Override
        public void run() {
            lastOldestPairSignature = oldestPair.heartbeatSignature;
            while (!isInterrupted()) {
                try {
                    Thread.sleep(RMIHeartbeatHandler.HEARTBEAT_INTERVAL_MILIS);
                    Logger.logVerbose("Heartbeat Watchdog", "Starting heartbeat check");
                    synchronized (IDtoHeartbeatCount) {
                        if (oldestPair != null) {
                            Logger.logDebug("Heartbeat Watchdog", "Stored signature : " + lastOldestPairSignature + "\n                              Actual signature : " + oldestPair.heartbeatSignature);
                            if (lastOldestPairSignature.equals(oldestPair.heartbeatSignature)) {//no heartbeats for 3 seconds
                                Logger.logVerbose("Heartbeat Watchdog", "Ho heartbeat detected...");
                                currentConsecutiveFailures++;
                                cumulativeFailures++;
                                if (currentConsecutiveFailures >= maxFailures) {//drop current oldest pair and start looking for another.
                                    Logger.logVerbose("Heartbeat Watchdog", "Game server is dead. Searching for new server...");
                                    IDPair ipd = findID(oldestPair.ID);
                                    if (ipd != null) {
                                        ipd.setHeartbeats(-1);
                                        oldestPair = null;
                                    }
                                }
                            } else {//there was an heartbeat
                                lastOldestPairSignature = oldestPair.heartbeatSignature;
                                Logger.logVerbose("Heartbeat Watchdog", "Heartbeat detected!");
                                currentConsecutiveFailures = 0;
                            }
                        } else Logger.logVerbose("Heartbeat Watchdog", "No game server present");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Logger.logInfo("Heartbeat Watchdog", "Current failures [" + currentConsecutiveFailures + " out of " + maxFailures + "] (" + cumulativeFailures + " total failures)");
            }
        }
    }
}
