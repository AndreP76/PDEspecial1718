package GameServer;

import Comunication.ChatUtils.TCPChat.GameCommand;
import Comunication.ChatUtils.TCPChat.GamePacket;
import Comunication.JDBCUtils.InternalData.GameInternalData;
import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.JDBCUtils.JDBCHandler;
import RockPaperScissors.Game;
import RockPaperScissors.GameView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestsThread extends Thread {
    ServerSocket SS;
    HashMap<String, GameInternalData> clientNamesToGames;

    private HashMap<String, ObjectOutputStream> clientNamesToStreams;
    private HashMap<String, RequestHandlerThreads> clientNamesToThreads;
    private HashMap<String, Game> clientNamesToGameData;
    private final ArrayList<PairInternalData> Pairs = new ArrayList<>();//final so we can sync it between threads
    private JDBCHandler DBHandler;
    private String DBHandlerConnectionString;

    public RequestsThread() {
        try {
            SS = new ServerSocket(0, 256, InetAddress.getLocalHost());
            clientNamesToGameData = new HashMap<>();
            clientNamesToThreads = new HashMap<>();
            clientNamesToGames = new HashMap<>();
            clientNamesToStreams = new HashMap<>();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    for (PairInternalData p : Pairs) {
                        DBHandler.DeactivatePair(p.getToken());
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                System.out.println("[RequestsThread][VERBOSE] :: Waiting for new user");
                Socket newClientSocket = SS.accept();
                System.out.println("[RequestsThread][VERBOSE] :: New user arrived. Creating communication streams");
                ObjectOutputStream toClient = new ObjectOutputStream(newClientSocket.getOutputStream());//OUTPUT FIRST, or else it blocks
                ObjectInputStream fromClient = new ObjectInputStream(newClientSocket.getInputStream());

                try {
                    GamePacket gp = (GamePacket) fromClient.readObject();
                    System.out.println("[RequestsThread][VERBOSE] :: Received a new message from " + gp.getSender());
                    if (gp.getCommand() == GameCommand.START_GAME) {
                        synchronized (Pairs) {
                            System.out.println("[RequestsThread][VERBOSE] :: Player " + gp.getSender() + " wants to start a game.\nRetrieving player data from Database");
                            PlayerInternalData PlayerID = DBHandler.RetrievePlayer(gp.getSender());
                            System.out.println("[RequestsThread][VERBOSE] :: Finding player pair");
                            Integer index = findIndexOfPair(gp.getSender());
                            if (index != null) {//pair already loaded
                                System.out.println("[RequestsThread][VERBOSE] :: Player pair already loaded. Finishing pair and starting game");
                                Game PlayersGame = new Game(Pairs.get(index).getPlayerOne().getName(), Pairs.get(index).getPlayerTwo().getName());
                                System.out.println("[RequestsThread][VERBOSE] :: New game created");
                                clientNamesToGameData.put(Pairs.get(index).getPlayerOne().getName(), PlayersGame);
                                clientNamesToGameData.put(Pairs.get(index).getPlayerTwo().getName(), PlayersGame);
                                System.out.println("[RequestsThread][VERBOSE] :: Game data assigned to players");

                                clientNamesToThreads.put(gp.getSender(), new RequestHandlerThreads(newClientSocket, fromClient, toClient, PlayerID, null, this));
                                System.out.println("[RequestsThread][VERBOSE] :: Player thread created! Thread ID : " + clientNamesToThreads.get(gp.getSender()).getName() + ":" + clientNamesToThreads.get(gp.getSender()).getId());
                                clientNamesToThreads.get(Pairs.get(index).getPlayerOne().getName()).setThisPlayerGame(PlayersGame);
                                clientNamesToThreads.get(Pairs.get(index).getPlayerTwo().getName()).setThisPlayerGame(PlayersGame);
                                System.out.println("[RequestsThread][VERBOSE] :: Game data linked to player threads");

                                clientNamesToStreams.put(gp.getSender(), toClient);
                                GameView GV = PlayersGame.generateGameView();
                                System.out.println("[RequestsThread][VERBOSE] :: Game view generated");
                                clientNamesToStreams.get(Pairs.get(index).getPlayerOne().getName()).writeObject(new GamePacket(GameServerMain.GAMESERVER_NAME, Pairs.get(index).getPlayerOne().getName(), GameCommand.STARTED));
                                System.out.println("[RequestsThread][VERBOSE] :: Sent game start packet to player one");
                                clientNamesToStreams.get(Pairs.get(index).getPlayerOne().getName()).writeObject(GV);
                                System.out.println("[RequestsThread][VERBOSE] :: Sent game view to player one");
                                clientNamesToStreams.get(Pairs.get(index).getPlayerTwo().getName()).writeObject(new GamePacket(GameServerMain.GAMESERVER_NAME, Pairs.get(index).getPlayerTwo().getName(), GameCommand.STARTED));
                                System.out.println("[RequestsThread][VERBOSE] :: Sent game start packet to player two");
                                clientNamesToStreams.get(Pairs.get(index).getPlayerTwo().getName()).writeObject(GV);
                                System.out.println("[RequestsThread][VERBOSE] :: Sent game view to player two");

                                clientNamesToThreads.get(Pairs.get(index).getPlayerOne().getName()).start();
                                clientNamesToThreads.get(Pairs.get(index).getPlayerTwo().getName()).start();
                                System.out.println("[RequestsThread][VERBOSE] :: Player thread started");
                            } else {
                                System.out.println("[RequestsThread][VERBOSE] :: Player pair not loaded! Searching from pair in Database");
                                try {
                                    PairInternalData PID = DBHandler.getPlayerActivePair(gp.getSender());
                                    if (PID != null) {
                                        System.out.println("[RequestsThread][VERBOSE] :: Pair data found in database! Loading pair to server");
                                        Pairs.add(PID);//Player data loaded here
                                        System.out.println("[RequestsThread][VERBOSE] :: Pair data loaded. Registering player streams and starting handling thread");
                                        clientNamesToStreams.put(gp.getSender(), toClient);
                                        clientNamesToThreads.put(gp.getSender(), new RequestHandlerThreads(newClientSocket, fromClient, toClient, PlayerID, null, this));
                                        System.out.println("[RequestsThread][VERBOSE] :: Player thread created! Thread ID : " + clientNamesToThreads.get(gp.getSender()).getName() + ":" + clientNamesToThreads.get(gp.getSender()).getId());
                                    } else {
                                        System.out.println("[RequestsThread][WARNING] : Unregistered pair tried to start a game! Ignoring!");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }//end of syncronized
                    }//all other commands should be sent to the handling thread
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getSocketAddress() {//xxx.xxx.xxx.xxx:yyyyy
        return SS.getInetAddress().getHostAddress() + ":" + SS.getLocalPort();
    }

    private Integer findIndexOfPair(String playername) {
        for (int i = 0; i < Pairs.size(); ++i) {
            if (Pairs.get(i).getPlayerOne().getName().equals(playername) || Pairs.get(i).getPlayerTwo().getName().equals(playername) && Pairs.get(i).getActive()) {
                return i;
            }
        }
        return null;
    }

    public void setDBHandlerConnectionString(String DBHandlerAddress, String DBHandlerPort) {
        DBHandler = new JDBCHandler(DBHandlerAddress, DBHandlerPort);
    }

    public void setDBHandler(JDBCHandler DBHandler) {
        this.DBHandler = DBHandler;
    }

    public void playerLeaving(PlayerInternalData leavingPlayer, Game leavingPlayerGame) {
        //find player pair
        //find the players pair(as in, an actual player)
        //send player leaving notification
        //terminate that player's thread
        //write the game to the database
        //deactivate the player pair on the database
        //remove pair from loaded pairs to save some memory

        int pairIndex = findIndexOfPair(leavingPlayer.getName());
        PairInternalData leavingPlayerPair = Pairs.get(pairIndex);

        PlayerInternalData otherPlayer;
        if (leavingPlayerPair.getPlayerOne().getName().equals(leavingPlayer.getName())) {
            otherPlayer = leavingPlayerPair.getPlayerTwo();
        } else if (leavingPlayerPair.getPlayerTwo().getName().equals(leavingPlayer.getName())) {
            otherPlayer = leavingPlayerPair.getPlayerOne();
        } else return;

        try {
            synchronized (clientNamesToStreams) {
                clientNamesToStreams.get(otherPlayer.getName()).writeObject(new GamePacket(GameServerMain.GAMESERVER_NAME, otherPlayer.getName(), GameCommand.PLAYER_LEFT));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (clientNamesToThreads) {
            clientNamesToThreads.get(otherPlayer.getName()).interrupt();
        }

        DBHandler.registerGame(leavingPlayerPair, leavingPlayerGame);
        DBHandler.DeactivatePair(leavingPlayerPair.getToken());

        synchronized (Pairs) {
            for (int i = 0; i < Pairs.size(); ++i) {
                if (Pairs.get(i).getToken().equals(leavingPlayerPair.getToken())) {
                    Pairs.remove(i);
                    break;
                }
            }
        }
    }
}
