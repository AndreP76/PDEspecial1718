package Comunication.RMIInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIManagementServerInterface extends Remote {
    String hearbeatMethod(String ID) throws RemoteException;
}
