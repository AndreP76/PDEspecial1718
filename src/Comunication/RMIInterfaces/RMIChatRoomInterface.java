package Comunication.RMIInterfaces;

import Comunication.ChatUtils.TCPChat.ChatPacket;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIChatRoomInterface extends Remote {
    void newClient(String clientName, RMIChatClientInterface clientChat) throws RemoteException;

    void newMessage(ChatPacket message) throws RemoteException;

    ArrayList<String> getClients() throws RemoteException;
}
