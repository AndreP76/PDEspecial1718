package Comunication.ChatUtils.RMIChat;

import Comunication.ChatUtils.TCPChat.ChatPacket;
import Comunication.JDBCUtils.PlayerInternalData;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIChatClientInterface extends Remote {
    void newMessage(ChatPacket cp) throws RemoteException;

    PlayerInternalData getClientDetails() throws RemoteException;
}
