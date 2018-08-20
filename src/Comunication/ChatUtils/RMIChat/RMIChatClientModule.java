package Comunication.ChatUtils.RMIChat;

import Colections.EventQueue;
import Comunication.ChatUtils.TCPChat.ChatPacket;
import Comunication.JDBCUtils.PlayerInternalData;
import Comunication.RMIInterfaces.RMIChatClientInterface;
import Comunication.RMIInterfaces.RMIChatRoomInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIChatClientModule extends UnicastRemoteObject implements RMIChatClientInterface {
    RMIChatRoomInterface chatServer;
    PlayerInternalData PID;
    EventQueue<ChatPacket> queue;

    protected RMIChatClientModule(String clientName, String serverIP, String serviceName, EventQueue<ChatPacket> ecp) throws RemoteException, MalformedURLException, NotBoundException {
        this(new PlayerInternalData(clientName, 1), serverIP, serviceName, ecp);
    }

    protected RMIChatClientModule(PlayerInternalData pid, String serverIP, String serviceName, EventQueue<ChatPacket> ecp) throws RemoteException, MalformedURLException, NotBoundException {
        chatServer = (RMIChatRoomInterface) Naming.lookup("//" + serverIP + "/" + serviceName);
        PID = pid;
        queue = ecp;
        chatServer.newClient(PID.getName(), this);
    }

    @Override
    public void newMessage(ChatPacket cp) {
        queue.enqueue(cp);
    }

    @Override
    public PlayerInternalData getClientDetails() {
        return PID;
    }

    public void sendMessage(String message) throws RemoteException {
        sendMessage(ChatPacket.GENERAL_STRING, message);
    }

    public void sendMessage(String target, String message) throws RemoteException {
        if (chatServer != null) {
            chatServer.newMessage(this, new ChatPacket(PID.getName(), target, message));
        }
    }
}
