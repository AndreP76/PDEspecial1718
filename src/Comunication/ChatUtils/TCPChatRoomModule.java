package Comunication.ChatUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Dictionary;

public class TCPChatRoomModule extends Thread {
    private int ChatPort;
    private ServerSocket SS;
    private ArrayList<ObjectInputStream> fromClientSources;
    private ArrayList<ObjectOutputStream> outToClients;
    private ArrayList<Socket> clientTCPSockets;
    private Dictionary<String, Integer> ClientsToIndexDictionary;

    TCPChatRoomModule() {
        try {
            SS = new ServerSocket(0);
            ChatPort = SS.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                Socket S = SS.accept();
                ObjectOutputStream toClientStream = new ObjectOutputStream(S.getOutputStream());
                ObjectInputStream fromClient = new ObjectInputStream(S.getInputStream());
                ChatPacket cp = (ChatPacket) fromClient.readObject();
                //TODO : check if client is in list. If not, add
                if (cp.getTarget().equals(ChatPacket.GENERAL_STRING)) {//send to everyone
                    //TODO : send to everyone
                    //TODO : create a special HELLO message
                }//TODO : find specific target and send message
                //read message
                //find targets
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    protected void RemoveChildren(int childrenCount) {

    }
}
