package Comunication.RMIHandlers;

import Comunication.RMIInterfaces.RMIHeartbeatInterface;

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
    private LinkedList<IDPair> IDtoHeartbeatCount = null;
    private IDPair oldestPair = null;
    private String DBServerIP;

    public RMIHeartbeatService(String DBServerIP) throws RemoteException {
        IDtoHeartbeatCount = new LinkedList<>();//Maybe quicker, maybe not
        try {
            //if (LocateRegistry.getRegistry() == null) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            //}
            Naming.rebind("rmi://localhost/" + RMI_HEARTBEAT_SERVICE_NAME, this);
            this.DBServerIP = DBServerIP;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String hearbeatMethod(String ID, String IPAddress) {
        System.out.println("Heartbeat called!");
        IDPair ipd = findID(ID);
        if (ipd != null) {//ID exists
            increaseHeartbeats(ID);
        } else {
            IDtoHeartbeatCount.add(new IDPair(ID, 0, IPAddress));
        }
        increaseHeartbeats(ID);//SIDE EFFECT : Changes oldestPair
        if (oldestPair.getID().equals(ID)) {
            return DBServerIP;
        } else {
            return null;
        }
    }

    private void increaseHeartbeats(String id) {
        IDPair idp = findID(id);
        if (idp != null) {
            idp.incHeartbeats();
            if (oldestPair == null || idp.getHeartbeatCount() > oldestPair.getHeartbeatCount()) {
                oldestPair = idp;
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

        IDPair(String ID, int initialCount, String IPAddress) {
            this.ID = ID;
            this.HeartbeatsCount = initialCount;
            this.IPAddress = IPAddress;
        }

        IDPair(String ID, String IPAddress) {
            this(ID, 0, IPAddress);
        }

        public String getID() {
            return ID;
        }

        public void incHeartbeats() {
            incHeartbeats(1);
        }

        public void incHeartbeats(int i) {
            this.HeartbeatsCount += i;
        }

        public int getHeartbeatCount() {
            return HeartbeatsCount;
        }

        public String getIPAddress() {
            return IPAddress;
        }
    }
}
