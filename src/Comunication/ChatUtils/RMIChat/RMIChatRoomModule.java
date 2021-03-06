package Comunication.ChatUtils.RMIChat;

import Comunication.ChatUtils.DataPackets.ChatPacket;
import Comunication.RMIInterfaces.RMIChatClientInterface;
import Comunication.RMIInterfaces.RMIChatRoomInterface;
import Utils.Logger;

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

    public RMIChatRoomModule(String serviceName, String serviceIP) throws MalformedURLException, RemoteException {
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (RemoteException e) {
            Logger.logInfo("ChatServer", "RMI  Registry found");
        }

        Naming.rebind("rmi://" + serviceIP + "/" + serviceName, this);
        clientsToInterface = new HashMap<>();
    }

    @Override
    public void newClient(String clientName, RMIChatClientInterface clientChat) throws RemoteException {
        Logger.logInfo("ChatServer", "Client " + clientName + " has registred in the chat");
        if (!clientsToInterface.containsKey(clientName)) {
            clientsToInterface.put(clientName, clientChat);
            for (RMIChatClientInterface c : clientsToInterface.values()) {
                if (c != null && !c.getClientDetails().getName().equals(clientName)) {
                    c.newClient(clientName);
                }
            }
        } else {//reply to client saying they are already registered
            clientChat.newMessage(new ChatPacket(ChatPacket.CHAT_SERVER_ID, clientName, ChatPacket.DUPLICATE_CLIENT));
        }
    }

    @Override
    public void removeClient(String clientName) throws RemoteException {
        Logger.logInfo("ChatServer", "Client " + clientName + " has left the chat");
        if (clientsToInterface.containsKey(clientName)) {
            clientsToInterface.remove(clientName);
            for (RMIChatClientInterface c : clientsToInterface.values()) {
                c.clientLeft(clientName);
            }
        }
    }

    @Override
    public void newMessage(ChatPacket message) throws RemoteException {
        Logger.logInfo("ChatServer", "New message arrived");
        Logger.logDebug("ChatServer", "Message from " + message.getSender() + " : " + message.getMessageContents());
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
        Logger.logVerbose("ChatServer", "Client list requested");
        ArrayList<String> keyset = new ArrayList<>(clientsToInterface.keySet());
        keyset.add(ChatPacket.GENERAL_STRING);
        return keyset;
    }
}
