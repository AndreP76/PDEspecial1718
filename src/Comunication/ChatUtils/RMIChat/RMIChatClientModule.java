package Comunication.ChatUtils.RMIChat;

import Comunication.ChatUtils.TCPChat.ChatPacket;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.RMIChatClientInterface;
import Comunication.RMIInterfaces.RMIChatRoomInterface;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.function.Consumer;

public class RMIChatClientModule extends UnicastRemoteObject implements RMIChatClientInterface, Serializable {
    private static final long serialVersionUID = 65268857L;
    private RMIChatRoomInterface chatServer;
    private PlayerInternalData PID;
    private Consumer<ChatPacket> onNewMessageListener;
    private Consumer<String> onNewClientListener;
    private Consumer<String> onClientLeftListener;

    public RMIChatClientModule(String clientName, String serverIP, String serviceName, Consumer<ChatPacket> onNewMessageListener, Consumer<String> onNewClientListener) throws RemoteException, MalformedURLException, NotBoundException {
        this(new PlayerInternalData(clientName, 1), serverIP, serviceName, onNewMessageListener, onNewClientListener);
    }

    public RMIChatClientModule(PlayerInternalData pid, String serverIP, String serviceName, Consumer<ChatPacket> onNewMessageListener, Consumer<String> onNewClientListener) throws RemoteException, MalformedURLException, NotBoundException {
        chatServer = (RMIChatRoomInterface) Naming.lookup("rmi://" + serverIP + "/" + serviceName);
        PID = pid;
        chatServer.newClient(PID.getName(), this);
        this.onNewClientListener = onNewClientListener;
        this.onNewMessageListener = onNewMessageListener;
    }

    public RMIChatClientModule(RMIChatRoomInterface chatServer, PlayerInternalData pid, Consumer<ChatPacket> onNewMessageListener, Consumer<String> onNewClientListener) throws RemoteException {
        this.chatServer = chatServer;
        PID = pid;
        this.chatServer.newClient(PID.getName(), this);
        this.onNewClientListener = onNewClientListener;
        this.onNewMessageListener = onNewMessageListener;
    }

    public RMIChatClientModule(RMIChatRoomInterface chatServer, PlayerInternalData pid) throws RemoteException {
        this(chatServer, pid, null, null);
    }

    @Override
    public void newMessage(ChatPacket cp) {
        if (onNewMessageListener != null) {
            System.out.println("[CHAT CLIENT] :: message listener not null");
            onNewMessageListener.accept(cp);
        } else {
            System.out.println("[CHAT CLIENT] :: message listener null");
        }
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
            chatServer.newMessage(new ChatPacket(PID.getName(), target, message));
        }
    }

    public void newClient(String newClient) {
        if (onNewClientListener != null) {
            System.out.println("[CHAT CLIENT] :: client listener not null");
            onNewClientListener.accept(newClient);
        } else {
            System.out.println("[CHAT CLIENT] :: client listener null");
        }
    }

    @Override
    public void clientLeft(String clientName) {
        if (onClientLeftListener != null) {
            System.out.println("[CHAT CLIENT] :: client left listener not null");
            onClientLeftListener.accept(clientName);
        } else {
            System.out.println("[CHAT CLIENT] :: client left listener null");
        }
    }

    public ArrayList<String> getClients() throws RemoteException {
        return chatServer.getClients();
    }

    public void setOnNewClientListener(Consumer<String> onNewClientListener) {//LULZ typo
        System.out.println("[CHAT CLIENT] :: setting client listener");
        this.onNewClientListener = onNewClientListener;
    }

    public void setOnNewMessageListener(Consumer<ChatPacket> onNewMessageListener) {
        System.out.println("[CHAT CLIENT] :: setting message listener");
        this.onNewMessageListener = onNewMessageListener;
    }

    public void setOnClientLeftListener(Consumer<String> onClientLeftListener) {
        System.out.println("[CHAT CLIENT] :: setting client left listener");
        this.onClientLeftListener = onClientLeftListener;
    }

    public void leave() {
        try {
            chatServer.removeClient(PID.getName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
