package Comunication.ChatUtils.TCPChat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

public class ChatRoomHandler extends Thread {
    Socket rawSocket;
    ObjectInputStream fromClient;
    TCPChatRoomModule tcpMasterService;
    String clientName;

    public ChatRoomHandler(Socket s, String clientName, ObjectInputStream fromClient, TCPChatRoomModule tcpDictionary) {
        rawSocket = s;
        this.fromClient = fromClient;
        tcpMasterService = tcpDictionary;
        this.clientName = clientName;
    }

    public synchronized void updateStreams(Socket s, ObjectInputStream fromClient, TCPChatRoomModule tcpDictionary) {
        rawSocket = s;
        this.fromClient = fromClient;
        tcpMasterService = tcpDictionary;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                ChatPacket cp = (ChatPacket) fromClient.readObject();
                if (clientName.equals(cp.getSender())) {//the right thread got the right stuff
                    if (cp.getTarget().equals(ChatPacket.GENERAL_STRING)) {
                        Set<Map.Entry<String, Integer>> pairs = tcpMasterService.ClientsToIndexDictionary.entrySet();
                        for (Map.Entry<String, Integer> p : pairs) {
                            if (!p.getKey().equals(cp.getSender())) {//do not duplicate messages
                                int clientIndex = p.getValue();
                                tcpMasterService.outToClients.get(clientIndex).writeObject(cp);
                            }
                        }
                    } else if (tcpMasterService.ClientsToIndexDictionary.containsKey(cp.getTarget())) {//known/active client
                        int clientIndex = tcpMasterService.ClientsToIndexDictionary.get(cp.getTarget());
                        tcpMasterService.outToClients.get(clientIndex).writeObject(cp);
                    } else {//unknown client
                        tcpMasterService.outToClients.get(tcpMasterService.ClientsToIndexDictionary.get(cp.getSender())).writeObject(new ChatPacket(ChatPacket.CHAT_SERVER_ID, cp.getSender(), "USER " + cp.getTarget() + " is unreachable at the moment. Please try again later"));
                        //reply with an error message to the sender
                    }
                } else {//crossed comunications
                    //LOG THIS
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
