package Comunication.RMIInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIHeartbeatInterface extends Remote {
    String hearbeatMethod(String ID) throws RemoteException;
}
