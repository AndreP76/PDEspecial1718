package GameServer;

import Comunication.JDBCUtils.InternalData.GameInternalData;
import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.RMIHandlers.RMIHeartbeatHandler;

import java.io.*;
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
                        deleteSavedGames();
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

    public static String[] findAvailableSavedGames() {
        File savesFolder = new File(SAVEDGAMES_FOLDER);
        File[] savedGames = savesFolder.listFiles();
        if (savedGames != null) {
            String[] savedGamesTokens = new String[savedGames.length];
            for (int i = 0; i < savedGames.length; ++i) {
                try {
                    String canonicalPath = savedGames[i].getCanonicalPath();
                    savedGamesTokens[i] = canonicalPath.substring(canonicalPath.lastIndexOf("/") + 1, canonicalPath.lastIndexOf("."));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return savedGamesTokens;
        } else return null;
    }

    public static GameInternalData loadGame(String game) {
        File gameFile = new File(SAVEDGAMES_FOLDER + "/" + game + ".bin");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(gameFile))) {
            return (GameInternalData) ois.readObject();
        } catch (IOException e) {
            System.out.println("[Game Server][ERROR] :: Failed to load game [" + game + "]");
        } catch (ClassNotFoundException e) {
            System.out.println("[Game Server][ERROR] :: Failed to load game [" + game + "]\nCould not interpret class!");
        }
        return null;
    }

    static void saveGame(GameInternalData thisPlayerGameData) {
        File newFile = new File(GameServerMain.SAVEDGAMES_FOLDER);
        if (!newFile.exists()) {
            if (!newFile.mkdirs()) {
                return;
            }
        }
        String newFilePath = GameServerMain.SAVEDGAMES_FOLDER + "/" + thisPlayerGameData.getGameToken() + ".bin";
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(newFilePath));
            out.writeObject(thisPlayerGameData);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSavedGames() {
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
    }
}
