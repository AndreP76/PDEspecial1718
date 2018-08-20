package Comunication.RMIInterfaces;

import Comunication.ChatUtils.TCPChat.ChatPacket;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIChatRoomInterface extends Remote {
    void newClient(String clientName, RMIChatClientInterface clientChat) throws RemoteException;

    void newMessage(RMIChatClientInterface senderChat, ChatPacket message) throws RemoteException;
}
