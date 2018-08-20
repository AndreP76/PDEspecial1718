package Comunication.RMIInterfaces;

import Comunication.JDBCUtils.PlayerInternalData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface RMIManagementServerInterface extends Remote {
    boolean registerNewClient(PlayerInternalData PID) throws RemoteException;

    boolean registerNewClient(String name, String password, String realName) throws RemoteException;

    boolean login(ClientsCallbackInterface CCI, String name, String password) throws RemoteException;

    boolean logout(ClientsCallbackInterface CCI, String name) throws RemoteException;

    Collection<PlayerInternalData> getActivePlayers() throws RemoteException;

    Collection<PlayerInternalData> getActivePlayersStatus() throws RemoteException;

    Collection<PlayerInternalData> getActivePlayersPaired() throws RemoteException;

    Collection<PlayerInternalData> getUnpairedActivePlayers() throws RemoteException;

    void requestPair(PlayerInternalData player, ClientsCallbackInterface requester) throws RemoteException;
}
