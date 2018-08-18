package Comunication.ChatUtils;

import Colections.EventCollectionInterface;
import Colections.EventQueue;

import java.io.IOException;
import java.util.Scanner;

public class ChatTestModule {
    private static final String CLIENT_MODE = "CLI";
    private static final String SERVER_MODE = "SER";

    public static void main(String args[]) {
        if (args.length == 1) {//server mode
            if (args[0].equals(SERVER_MODE)) {
                StartServer();
            }
        } else if (args.length == 2) {//client mode
            if (args[0].equals(CLIENT_MODE)) {
                StartClient(args[1]);
            }
        } else usage();
    }

    private static void StartClient(String arg) {
        try {
            EventQueue<ChatPacket> messages = new EventQueue<>();
            messages.addListener(new TranslatorModuleTest());
            Scanner sIN = new Scanner(System.in);
            System.out.print("Name : ");
            String name = sIN.nextLine();
            ChatClientModule CCM = new ChatClientModule(name, arg, messages);
            while (true) {
                System.out.print("New Message : ");
                String message = sIN.nextLine();
                System.out.println("To whom (enter means general) : ");
                String target = sIN.nextLine();
                if (target.isEmpty()) {
                    target = ChatPacket.GENERAL_STRING;
                }

                try {
                    CCM.SendMessage(target, message);
                } catch (IOException ioex) {
                    System.out.println("Could not send message!!");
                }
            }
        } catch (IOException e) {
            System.out.println("Could not connect to the chat server");
        }
    }

    private static void StartServer() {
        TCPChatRoomModule chatModule = new TCPChatRoomModule(System.out);
        chatModule.start();

        System.out.println("Chat port : " + chatModule.getChatPort());
        System.out.println("Chat module started... Type \"exit\" to stop");
        Scanner sIN = new Scanner(System.in);
        while (!sIN.nextLine().equals("exit")) ;
        System.out.println("Chat module shutting down");
        chatModule.interrupt();
    }

    private static void usage() {
        System.out.println("Arguments : " + SERVER_MODE + " | " + CLIENT_MODE + "<chat server IP>");
    }

    private static class TranslatorModuleTest implements Colections.EventCollectionListenerInterface {
        @Override
        public void onNewElement(EventCollectionInterface evi) {
            EventQueue evq = (EventQueue<ChatPacket>) evi;
            try {
                System.out.println("Received : " + ((ChatPacket) evq.dequeue()).getMessageContents());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
