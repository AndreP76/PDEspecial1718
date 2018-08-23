package Comunication.RMIInterfaces;

import Comunication.JDBCUtils.InternalData.PlayerInternalData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface RMIManagementServerInterface extends Remote {
    boolean registerNewClient(PlayerInternalData PID) throws RemoteException;

    boolean registerNewClient(String name, String password, String realName) throws RemoteException;

    boolean login(ClientsCallbackInterface CCI, String name, String password) throws RemoteException;

    boolean logout(String name) throws RemoteException;

    ArrayList<String> getActivePlayers() throws RemoteException;

    ArrayList<PlayerInternalData> getActivePlayersStatus() throws RemoteException;

    ArrayList<PlayerInternalData> getActivePlayersPaired() throws RemoteException, SQLException;

    ArrayList<PlayerInternalData> getUnpairedActivePlayers() throws RemoteException, SQLException;

    void requestPair(PlayerInternalData player, ClientsCallbackInterface requester) throws RemoteException;

    PlayerInternalData getPlayerData(String name) throws RemoteException;

    ArrayList<PlayerInternalData> getActivePlayersData() throws RemoteException;
}
