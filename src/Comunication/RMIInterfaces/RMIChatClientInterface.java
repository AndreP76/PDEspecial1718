package Comunication.RMIInterfaces;

import Comunication.ChatUtils.DataPackets.ChatPacket;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIChatClientInterface extends Remote {
    void newMessage(ChatPacket cp) throws RemoteException;
    PlayerInternalData getClientDetails() throws RemoteException;
    void newClient(String newClient) throws RemoteException;

    void clientLeft(String clientName) throws RemoteException;
}
