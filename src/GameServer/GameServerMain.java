package GameServer;

import Comunication.RMIHandlers.RMIHeartbeatHandler;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GameServerMain {
    public static final String DATAPATH = "./Data/";
    public static final String SAVEDGAMES_FOLDER = DATAPATH + "SavedGames";
    public static final String GAMESERVER_NAME = "GAMESERVER";
    public static InetAddress ManagementServerIP;

    public static RMIHeartbeatHandler HeartbeatThread;

    public static void main(String args[]) {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        if (args.length > 0) {
            try {
                ManagementServerIP = Inet4Address.getByName(args[0]);
                RequestsThread rt = new RequestsThread();
                HeartbeatThread = new RMIHeartbeatHandler(args[0], rt.getSocketAddress());
                HeartbeatThread.start();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rt.setDBHandler(HeartbeatThread.getDBHandler());
                rt.start();
                Scanner sIN = new Scanner(System.in);
                while (true) {
                    System.out.print("Command : ");
                    String command = sIN.nextLine();
                    if (command.equals("exit")) {
                        HeartbeatThread.interrupt();
                        System.exit(0);
                        return;
                    }
                }
            } catch (UnknownHostException e) {
                System.out.println("Cannot find management server. Shutting down");
            }
        } else usage();
    }

    private static void usage() {
        System.out.println("usage : program.jar <managementServerIP>");
    }
}
