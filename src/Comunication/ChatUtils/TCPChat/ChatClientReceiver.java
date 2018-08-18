package Comunication.ChatUtils.TCPChat;

import Colections.EventQueue;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ChatClientReceiver extends Thread {
    //this thread reads data from the stream and writes it to the queue
    EventQueue<ChatPacket> newMessages;
    ObjectInputStream fromServer;

    ChatClientReceiver(EventQueue<ChatPacket> newMessages, ObjectInputStream fromServer) {
        this.newMessages = newMessages;
        this.fromServer = fromServer;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {//TODO : Maybe isInterrupted isn't the right method here
            try {
                ChatPacket cp = (ChatPacket) fromServer.readObject();
                newMessages.enqueue(cp);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
