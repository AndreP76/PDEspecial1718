package Comunication.RMIHandlers;

import Comunication.RMIInterfaces.RMIHeartbeatInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

public class RMIHeartbeatService extends UnicastRemoteObject implements RMIHeartbeatInterface {
    private LinkedList<IDPair> IDtoHeartbeatCount = null;
    private IDPair oldestPair = null;
    private String DBServerIP;

    public RMIHeartbeatService(String DBServerIP) throws RemoteException {
        IDtoHeartbeatCount = new LinkedList<>();//Maybe quicker, maybe not
        this.DBServerIP = DBServerIP;
    }

    @Override
    public String hearbeatMethod(String ID) {
        IDPair ipd = findID(ID);
        if (ipd != null) {//ID exists
            increaseHeartbeats(ID);
        } else {
            IDtoHeartbeatCount.add(new IDPair(ID, 0));
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
            if (idp.getHeartbeatCount() > oldestPair.getHeartbeatCount()) {
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

    private class IDPair {
        private String ID;
        private int HeartbeatsCount = 0;

        IDPair(String ID, int initialCount) {
            this.ID = ID;
            this.HeartbeatsCount = initialCount;
        }

        IDPair(String ID) {
            this(ID, 0);
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
    }
}
