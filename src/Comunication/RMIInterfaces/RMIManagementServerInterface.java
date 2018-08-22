package Comunication.RMIInterfaces;

import Comunication.JDBCUtils.InternalData.PlayerInternalData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;

public interface RMIManagementServerInterface extends Remote {
    boolean registerNewClient(PlayerInternalData PID) throws RemoteException;

    boolean registerNewClient(String name, String password, String realName) throws RemoteException;

    boolean login(ClientsCallbackInterface CCI, String name, String password) throws RemoteException;

    boolean logout(ClientsCallbackInterface CCI, String name) throws RemoteException;

    Collection<String> getActivePlayers() throws RemoteException;

    Collection<PlayerInternalData> getActivePlayersStatus() throws RemoteException;

    Collection<PlayerInternalData> getActivePlayersPaired() throws RemoteException, SQLException;

    Collection<PlayerInternalData> getUnpairedActivePlayers() throws RemoteException, SQLException;

    void requestPair(PlayerInternalData player, ClientsCallbackInterface requester) throws RemoteException;

    PlayerInternalData getPlayerData(String name) throws RemoteException;
}
