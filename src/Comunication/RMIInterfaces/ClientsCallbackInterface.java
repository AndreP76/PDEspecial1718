package Comunication.RMIInterfaces;

import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientsCallbackInterface extends Remote {//clients must implement this, because reasons

    PlayerInternalData getClientInfo() throws RemoteException;

    void onDuplicateLogin() throws RemoteException;

    void onSQLError() throws RemoteException;

    void onDuplicateLogout() throws RemoteException;

    void onInvalidPairRequest() throws RemoteException;

    boolean onPairRequested(PlayerInternalData clientInfo, RMIManagementServerInterface managementServerMain) throws RemoteException;

    void onPairRequestRejected() throws RemoteException;

    void onPairRequestAccepted(PairInternalData PID) throws RemoteException;

    void newPlayerJoined(PlayerInternalData PID) throws RemoteException;

    void playerLeft(PlayerInternalData PID) throws RemoteException;

    void playerUpdate(PlayerInternalData PID) throws RemoteException;
}
