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
    private Consumer<ChatPacket> onNewMessageListerner;
    private Consumer<String> onNewClientListerner;

    public RMIChatClientModule(String clientName, String serverIP, String serviceName, Consumer<ChatPacket> onNewMessageListerner, Consumer<String> onNewClientListerner) throws RemoteException, MalformedURLException, NotBoundException {
        this(new PlayerInternalData(clientName, 1), serverIP, serviceName, onNewMessageListerner, onNewClientListerner);
    }

    public RMIChatClientModule(PlayerInternalData pid, String serverIP, String serviceName, Consumer<ChatPacket> onNewMessageListerner, Consumer<String> onNewClientListerner) throws RemoteException, MalformedURLException, NotBoundException {
        chatServer = (RMIChatRoomInterface) Naming.lookup("//" + serverIP + "/" + serviceName);
        PID = pid;
        chatServer.newClient(PID.getName(), this);
        this.onNewClientListerner = onNewClientListerner;
        this.onNewMessageListerner = onNewMessageListerner;
    }

    public RMIChatClientModule(RMIChatRoomInterface chatServer, PlayerInternalData pid, Consumer<ChatPacket> onNewMessageListerner, Consumer<String> onNewClientListerner) throws RemoteException {
        this.chatServer = chatServer;
        PID = pid;
        this.chatServer.newClient(PID.getName(), this);
        this.onNewClientListerner = onNewClientListerner;
        this.onNewMessageListerner = onNewMessageListerner;
    }

    public RMIChatClientModule(RMIChatRoomInterface chatServer, PlayerInternalData pid) throws RemoteException {
        this(chatServer, pid, null, null);
    }

    @Override
    public void newMessage(ChatPacket cp) {
        if (onNewMessageListerner != null) {
            System.out.println("message listener not null");
            onNewMessageListerner.accept(cp);
        } else {
            System.out.println("message listener null");
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
        if (onNewClientListerner != null) {
            System.out.println("client listener not null");
            onNewClientListerner.accept(newClient);
        } else {
            System.out.println("client listener null");
        }
    }

    public ArrayList<String> getClients() throws RemoteException {
        return chatServer.getClients();
    }

    public void setOnNewClientListerner(Consumer<String> onNewClientListerner) {//LULZ typo
        System.out.println("setting client listener");
        this.onNewClientListerner = onNewClientListerner;
    }

    public void setOnNewMessageListerner(Consumer<ChatPacket> onNewMessageListerner) {
        System.out.println("setting message listener");
        this.onNewMessageListerner = onNewMessageListerner;
    }
}
