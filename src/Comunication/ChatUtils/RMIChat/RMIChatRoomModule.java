package Comunication.ChatUtils.RMIChat;

import Comunication.ChatUtils.TCPChat.ChatPacket;
import Comunication.RMIInterfaces.RMIChatClientInterface;
import Comunication.RMIInterfaces.RMIChatRoomInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RMIChatRoomModule extends UnicastRemoteObject implements RMIChatRoomInterface {
    private static final long serialVersionUID = 5464874L;
    private HashMap<String, RMIChatClientInterface> clientsToInterface;

    public RMIChatRoomModule(String serviceName, String serviceIP) throws RemoteException, MalformedURLException {
        LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        Naming.rebind("//" + serviceIP + "/" + serviceName, this);
        clientsToInterface = new HashMap<>();
    }

    @Override
    public void newClient(String clientName, RMIChatClientInterface clientChat) throws RemoteException {
        System.out.println("DEBUG :: " + clientName + " has registred!");
        if (!clientsToInterface.containsKey(clientName)) {
            clientsToInterface.put(clientName, clientChat);
            for (RMIChatClientInterface c : clientsToInterface.values()) {
                if (c != null && !c.getClientDetails().getName().equals(clientName)) {
                    System.out.println("DEBUG :: " + c.getClientDetails().getName() + " newClient event called!");
                    c.newClient(clientName);
                }
            }
        } else {//reply to client saying they are already registered
            clientChat.newMessage(new ChatPacket(ChatPacket.CHAT_SERVER_ID, clientName, ChatPacket.DUPLICATE_CLIENT));
        }
    }

    @Override
    public void newMessage(ChatPacket message) throws RemoteException {
        System.out.println("DEBUG :: New message arrived [" + message.getSender() + " :: " + message.getMessageContents() + "]");
        if (message.getTarget().equals(ChatPacket.GENERAL_STRING)) {//send to everyone
            Set<Map.Entry<String, RMIChatClientInterface>> RMIC = clientsToInterface.entrySet();
            for (Map.Entry<String, RMIChatClientInterface> R : RMIC) {
                if (!message.getSender().equals(R.getKey())) {//avoid repeating messages
                    R.getValue().newMessage(message);
                }
            }
        } else if (clientsToInterface.containsKey(message.getTarget())) {//send to a particular someone
            clientsToInterface.get(message.getTarget()).newMessage(message);
        }
    }

    @Override
    public ArrayList<String> getClients() {
        System.out.println("DEBUG :: Client list request");
        return new ArrayList<>(clientsToInterface.keySet());
    }
}
