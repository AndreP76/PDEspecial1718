package Comunication.RMIInterfaces;

import Comunication.JDBCUtils.PlayerInternalData;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientsCallbackInterface extends Remote {//clients must implement this, because reasons

    PlayerInternalData getClientInfo() throws RemoteException;

    void onDuplicateLogin();

    void onSQLError();

    void onDuplicateLogout();

    void onInvalidPairRequest();

    boolean onPairRequested(PlayerInternalData clientInfo, RMIManagementServerInterface managementServerMain);

    void onPairRequestRejected();

    void onPairRequestAccepted();
}
