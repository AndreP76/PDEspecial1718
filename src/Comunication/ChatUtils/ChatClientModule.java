package Comunication.ChatUtils;

import Colections.EventQueue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ChatClientModule {
    private InetAddress ServerIP;
    private int ServerPort = 21684;//the ChatPort
    private String clientName;

    private ChatClientReceiver receiverThread;
    private Socket S;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    ChatClientModule(String clientName, String serverIPString, EventQueue<ChatPacket> newMessages) throws IOException {
        ServerIP = InetAddress.getByName(serverIPString);
        this.clientName = clientName;
        S = new Socket();

        S.connect(new InetSocketAddress(ServerIP, ServerPort));
        toServer = new ObjectOutputStream(S.getOutputStream());
        toServer.writeObject(new ChatPacket(this.clientName, ChatPacket.GAME_SERVER_ID, ChatPacket.HELLO_STRING));

        fromServer = new ObjectInputStream(S.getInputStream());

        receiverThread = new ChatClientReceiver(newMessages, fromServer);
        receiverThread.start();
    }

    public void SendMessage(String message) throws IOException {
        SendMessage(ChatPacket.GENERAL_STRING, message);
    }

    public void SendMessage(String to, String message) throws IOException {
        toServer.writeObject(new ChatPacket(clientName, to, message));
    }
}
