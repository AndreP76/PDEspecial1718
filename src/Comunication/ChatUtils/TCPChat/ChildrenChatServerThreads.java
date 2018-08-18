package Comunication.ChatUtils.TCPChat;

public class ChildrenChatServerThreads extends Thread {
    private TCPChatRoomModule fatherThread;
    //this thread will:
    //be woken when a message is received
    //process message as needed
    //relay message to necessary clients using fatherThread method
    //sleep until next Run is called
}
