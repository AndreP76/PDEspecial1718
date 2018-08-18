package Comunication.ChatUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class TCPChatRoomModule extends Thread {
    private int ChatPort;
    ServerSocket SS;
    ArrayList<ObjectInputStream> fromClientSources;
    ArrayList<ObjectOutputStream> outToClients;
    ArrayList<Socket> clientTCPSockets;
    ArrayList<ChatRoomHandler> clientHandlingThreads;
    HashMap<String, Integer> ClientsToIndexDictionary;
    int currentClientCount = 0;

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
                ObjectInputStream fromClient = new ObjectInputStream(S.getInputStream());
                ChatPacket cp = (ChatPacket) fromClient.readObject();
                if (ClientsToIndexDictionary.containsKey(cp.getSender())) {//client already exists
                    //replace the streams, maybe a disconnect happened
                    int clientIndex = ClientsToIndexDictionary.get(cp.getSender());
                    ObjectOutputStream toClient = new ObjectOutputStream(S.getOutputStream());
                    fromClientSources.set(clientIndex, fromClient);
                    outToClients.set(clientIndex, toClient);
                    clientTCPSockets.set(clientIndex, S);
                    clientHandlingThreads.get(clientIndex).updateStreams(S, fromClient, this);
                } else {
                    fromClientSources.add(fromClient);
                    ObjectOutputStream toClient = new ObjectOutputStream(S.getOutputStream());
                    outToClients.add(toClient);
                    clientTCPSockets.add(S);
                    ChatRoomHandler handlingThread = new ChatRoomHandler(S, cp.getSender(), fromClient, this);
                    clientHandlingThreads.add(handlingThread);
                    ClientsToIndexDictionary.put(cp.getSender(), currentClientCount);
                    handlingThread.start();
                    currentClientCount++;
                }
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
