package Comunication.RMIInterfaces;

import Comunication.ChatUtils.TCPChat.ChatPacket;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIChatClientInterface extends Remote {
    void newMessage(ChatPacket cp) throws RemoteException;

    PlayerInternalData getClientDetails() throws RemoteException;
}
