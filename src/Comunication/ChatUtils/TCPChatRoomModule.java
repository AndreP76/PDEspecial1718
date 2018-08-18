package Comunication.ChatUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class TCPChatRoomModule extends Thread {
    PrintStream verboseStream;
    ServerSocket SS;
    ArrayList<ObjectInputStream> fromClientSources;
    ArrayList<ObjectOutputStream> outToClients;
    ArrayList<Socket> clientTCPSockets;
    ArrayList<ChatRoomHandler> clientHandlingThreads;
    HashMap<String, Integer> ClientsToIndexDictionary;
    int currentClientCount = 0;
    private int ChatPort = 21684;
    //the server launches this, and this thread handles everything about the chat thing

    TCPChatRoomModule(PrintStream verboseStream) {
        try {
            this.verboseStream = verboseStream;
            SS = new ServerSocket(ChatPort);

            ClientsToIndexDictionary = new HashMap<>();
            fromClientSources = new ArrayList<>();
            outToClients = new ArrayList<>();
            clientTCPSockets = new ArrayList<>();
            clientHandlingThreads = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    TCPChatRoomModule() {
        this(null);
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                verboseLog("Waiting for new client...");
                Socket S = SS.accept();
                verboseLog("Found new client!");
                ObjectInputStream fromClient = new ObjectInputStream(S.getInputStream());
                ChatPacket cp = (ChatPacket) fromClient.readObject();
                if (ClientsToIndexDictionary.containsKey(cp.getSender())) {//client already exists
                    verboseLog("Client already registred!! WARNING");
                    //replace the streams, maybe a disconnect happened
                    int clientIndex = ClientsToIndexDictionary.get(cp.getSender());
                    ObjectOutputStream toClient = new ObjectOutputStream(S.getOutputStream());
                    fromClientSources.set(clientIndex, fromClient);
                    outToClients.set(clientIndex, toClient);
                    clientTCPSockets.set(clientIndex, S);
                    clientHandlingThreads.get(clientIndex).updateStreams(S, fromClient, this);
                } else {
                    verboseLog("Registering new client");
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

    private void verboseLog(String s) {
        if (verboseStream != null) {
            verboseStream.println(s);
        }
    }

    public int getChatPort() {
        return ChatPort;
    }
}
