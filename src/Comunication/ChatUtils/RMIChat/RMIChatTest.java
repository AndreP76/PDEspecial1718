package Comunication.ChatUtils.RMIChat;

import Colections.EventCollectionInterface;
import Colections.EventCollectionListenerInterface;
import Colections.EventQueue;
import Comunication.ChatUtils.TCPChat.ChatPacket;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class RMIChatTest {
    public static void main(String[] args) {
        if (args[0].equals("CLI")) {
            StartClient();
        } else {
            StartServer();
        }
    }

    private static void StartServer() {
        try {
            RMIChatRoomModule RMICRM = new RMIChatRoomModule("RMICR", "localhost");
            Scanner sIN = new Scanner(System.in);
            System.out.println("Write \"exit\" to end program");
            while (!sIN.nextLine().equals("exit")) ;//This is well built, Linter is picky
            System.out.println("Ending program");
        } catch (RemoteException e) {
            System.out.println("Remote exception starting RMI Chat Server");
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL for service");
        }
    }

    private static void StartClient() {
        EventQueue<ChatPacket> messagesQueue = new EventQueue<>();
        messagesQueue.addListener(new EventCollectionListenerInterface() {
            @Override
            public void onNewElement(EventCollectionInterface evi) {
                try {
                    ChatPacket cp = messagesQueue.dequeue();
                    System.out.println("Received message from " + cp.getSender() + " : " + cp.getMessageContents());
                } catch (InterruptedException e) {
                    System.out.println("Failed dequeuing message!");
                }
            }
        });
        String name;
        Scanner sIN = new Scanner(System.in);
        System.out.println("Name : ");
        name = sIN.nextLine();
        try {
            RMIChatClientModule RMICCM = new RMIChatClientModule(name, "localhost", "RMICR", messagesQueue);
            System.out.print("New message (will be sent to general) : ");
            RMICCM.sendMessage(sIN.nextLine());
            System.out.println("Message sent");
        } catch (RemoteException e) {
            System.out.println("Exception occurred in ChatServer!");
        } catch (MalformedURLException e) {
            System.out.println("Invalid IP or service name!");
        } catch (NotBoundException e) {
            System.out.println("NotBoundException, whatever that is...");
        }
    }
}
