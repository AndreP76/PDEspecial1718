package GameServer;

import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.RMIHandlers.RMIHeartbeatHandler;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
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
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        System.out.println("[Game Server][INFO] :: Shutdown hook One called");
                        File f = new File(SAVEDGAMES_FOLDER);
                        File[] filesInFolder = f.listFiles();
                        if (filesInFolder != null) {
                            for (File file : filesInFolder) {
                                if (!file.isDirectory()) {
                                    file.delete();
                                    System.out.println("[Game Server][VERBOSE] :: Deleting " + file.getAbsolutePath());
                                }
                            }
                        }
                        System.out.println("[Game Server][INFO] :: Shutdown hook Two called");
                        for (PairInternalData p : rt.getPairs()) {
                            System.out.println("[Game Server][VERBOSE] :: Deactivating " + p.getToken());
                            try {
                                rt.getDBHandler().DeactivatePair(p.getToken());
                            } catch (SQLException e) {
                                System.out.println("[Game Server][ERROR] :: Failed to deactivate pair [" + p.getToken() + "]");
                            }
                        }
                    }
                });
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
