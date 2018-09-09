package GameServer;

import Comunication.ChatUtils.DataPackets.GameCommand;
import Comunication.ChatUtils.DataPackets.GamePacket;
import Comunication.JDBCUtils.InternalData.GameInternalData;
import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.JDBCUtils.JDBCHandler;
import RockPaperScissors.Game;
import RockPaperScissors.GameView;
import Utils.Logger;

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
    private final HashMap<String, GameInternalData> clientNamesToGames = new HashMap<>();
    private final HashMap<String, ObjectOutputStream> clientNamesToStreams = new HashMap<>();
    private final HashMap<String, RequestHandlerThreads> clientNamesToThreads = new HashMap<>();
    private final HashMap<String, Game> clientNamesToGameData = new HashMap<>();
    private ServerSocket SS;
    private final ArrayList<PairInternalData> Pairs = new ArrayList<>();//final so we can sync it between threads
    private JDBCHandler DBHandler;
    private String DBHandlerConnectionString;

    public RequestsThread() {
        try {
            SS = new ServerSocket(0, 256, InetAddress.getLocalHost());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PairInternalData> getPairs() {
        return Pairs;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Logger.logVerbose("RequestsThread", "Waiting for new user");
                Socket newClientSocket = SS.accept();
                Logger.logVerbose("RequestsThread", "New user arrived. Creating communication streams");
                ObjectOutputStream toClient = new ObjectOutputStream(newClientSocket.getOutputStream());//OUTPUT FIRST, or else it blocks
                ObjectInputStream fromClient = new ObjectInputStream(newClientSocket.getInputStream());

                try {
                    GamePacket gp = (GamePacket) fromClient.readObject();
                    Logger.logVerbose("RequestsThread", "Received a new message from " + gp.getSender());
                    if (gp.getCommand() == GameCommand.START_GAME) {
                        synchronized (Pairs) {
                            Logger.logVerbose("RequestsThread", "Player " + gp.getSender() + " wants to start a game.\nRetrieving player data from Database");
                            PlayerInternalData PlayerID = DBHandler.RetrievePlayer(gp.getSender());
                            Logger.logVerbose("RequestsThread", "Finding player pair");
                            Integer index = findIndexOfPair(gp.getSender());
                            if (index != null) {//pair already loaded
                                Logger.logVerbose("RequestsThread", "Player pair already loaded. Finishing pair and starting game");
                                PairInternalData startingPairID = Pairs.get(index);
                                //list available games on the save folder
                                //retrieve data for said games
                                //retrieve players for those games
                                //if there is a game where both players equal these two players
                                //game = loadSerializedGame, fuck the pair
                                //else game = new Game
                                Game PlayersGame = null;
                                GameInternalData PlayersGameData = null;

                                String[] availableGameTokens = GameServerMain.findAvailableSavedGames();
                                if (availableGameTokens != null) {
                                    for (String game : availableGameTokens) {
                                        GameInternalData GID = DBHandler.retrieveGame(game);
                                        String gamePlayerOne = GID.getPlayingPair().getPlayerOne().getName();
                                        String gamePlayerTwo = GID.getPlayingPair().getPlayerTwo().getName();
                                        String pairPlayerOne = startingPairID.getPlayerOne().getName();
                                        String pairPlayerTwo = startingPairID.getPlayerTwo().getName();

                                        if ((gamePlayerOne.equals(pairPlayerOne) && gamePlayerTwo.equals(pairPlayerTwo)) || (gamePlayerOne.equals(pairPlayerTwo) && gamePlayerTwo.equals(pairPlayerOne))) {
                                            //this is a game played by these two
                                            PlayersGameData = GameServerMain.loadGame(game);
                                            if (PlayersGameData == null || PlayersGameData.getG() == null) {
                                                PlayersGameData = null;//null so the game gets recreated and nobody has to know we tried to load a game
                                            } else {
                                                PlayersGameData.setPlayingPair(startingPairID);
                                                PlayersGame = PlayersGameData.getG();
                                                Logger.logVerbose("RequestsThread", "Old game loaded");
                                            }
                                        }
                                    }
                                }
                                if (PlayersGame == null) {
                                    PlayersGame = new Game(startingPairID.getPlayerOne().getName(), startingPairID.getPlayerTwo().getName());
                                    PlayersGameData = new GameInternalData(PlayersGame, startingPairID);
                                    DBHandler.registerGame(PlayersGameData);
                                    Logger.logVerbose("RequestThread", "New game created");
                                }

                                clientNamesToGameData.put(startingPairID.getPlayerOne().getName(), PlayersGame);
                                clientNamesToGameData.put(startingPairID.getPlayerTwo().getName(), PlayersGame);
                                Logger.logVerbose("RequestThread", "Game data assigned to players");

                                clientNamesToThreads.put(gp.getSender(), new RequestHandlerThreads(newClientSocket, fromClient, toClient, PlayerID, null, this));
                                Logger.logVerbose("RequestsThread", "Player thread created! Thread ID : " + clientNamesToThreads.get(gp.getSender()).getName() + ":" + clientNamesToThreads.get(gp.getSender()).getId());
                                clientNamesToThreads.get(startingPairID.getPlayerOne().getName()).setThisPlayerGameData(PlayersGameData);
                                clientNamesToThreads.get(startingPairID.getPlayerTwo().getName()).setThisPlayerGameData(PlayersGameData);
                                Logger.logVerbose("RequestsThread", "Game data linked to player threads");

                                clientNamesToStreams.put(gp.getSender(), toClient);
                                GameView GV = PlayersGame.generateGameView();
                                Logger.logVerbose("RequestsThread", "Game view generated");
                                clientNamesToStreams.get(startingPairID.getPlayerOne().getName()).writeObject(new GamePacket(GameServerMain.GAMESERVER_NAME, Pairs.get(index).getPlayerOne().getName(), GameCommand.STARTED));
                                Logger.logVerbose("RequestsThread", "Sent game start packet to player one");
                                clientNamesToStreams.get(startingPairID.getPlayerOne().getName()).writeObject(GV);
                                Logger.logVerbose("RequestsThread", "Sent game view to player one");
                                clientNamesToStreams.get(startingPairID.getPlayerTwo().getName()).writeObject(new GamePacket(GameServerMain.GAMESERVER_NAME, Pairs.get(index).getPlayerTwo().getName(), GameCommand.STARTED));
                                Logger.logVerbose("RequestsThread", "Sent game start packet to player two");
                                clientNamesToStreams.get(startingPairID.getPlayerTwo().getName()).writeObject(GV);
                                Logger.logVerbose("RequestsThread", "Sent game view to player two");

                                clientNamesToThreads.get(startingPairID.getPlayerOne().getName()).start();
                                clientNamesToThreads.get(startingPairID.getPlayerTwo().getName()).start();
                                Logger.logVerbose("RequestsThread", "Player threads started");
                            } else {
                                Logger.logVerbose("RequestsThread", "Player pair not loaded! Searching pair in Database");
                                try {
                                    PairInternalData PID = DBHandler.getPlayerActivePair(gp.getSender());
                                    if (PID != null) {
                                        Logger.logVerbose("RequestsThread", "Pair data found in database! Loading pair to server");
                                        Pairs.add(PID);//Player data loaded here
                                        Logger.logVerbose("RequestsThread", "Pair data loaded. Registering player streams and starting handling thread");
                                        clientNamesToStreams.put(gp.getSender(), toClient);
                                        clientNamesToThreads.put(gp.getSender(), new RequestHandlerThreads(newClientSocket, fromClient, toClient, PlayerID, null, this));
                                        Logger.logVerbose("RequestsThread", "Player thread created! Thread ID : " + clientNamesToThreads.get(gp.getSender()).getName() + ":" + clientNamesToThreads.get(gp.getSender()).getId());
                                    } else {
                                        Logger.logError("RequestsThread", "Unregistered pair tried to start a game! Ignoring!");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }//end of syncronized
                    }//all other commands should be sent to the handling thread
                } catch (ClassNotFoundException e) {
                    Logger.logException(e);
                }
            } catch (IOException e) {
                Logger.logException(e);
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

    public void playerLeaving(PlayerInternalData leavingPlayer, GameInternalData leavingPlayerGame) {
        //find player pair
        //find the players pair(as in, an actual player)
        //send player leaving notification
        //terminate that player's thread
        //write the game to the database
        //deactivate the player pair on the database
        //remove pair from loaded pairs to save some memory

        Logger.logVerbose("RequestsThread", "playerLeaving method called");
        Integer pairIndex = findIndexOfPair(leavingPlayer.getName());//What is this ?
        if (pairIndex != null) {
            PairInternalData leavingPlayerPair = Pairs.get(pairIndex);

            PlayerInternalData otherPlayer;
            Logger.logVerbose("RequestsThread", "Finding leaving player pair");
            if (leavingPlayerPair.getPlayerOne().getName().equals(leavingPlayer.getName())) {
                otherPlayer = leavingPlayerPair.getPlayerTwo();
            } else if (leavingPlayerPair.getPlayerTwo().getName().equals(leavingPlayer.getName())) {
                otherPlayer = leavingPlayerPair.getPlayerOne();
            } else return;

            try {
                synchronized (clientNamesToStreams) {
                    Logger.logVerbose("RequestsThread", "Sending player leaving information to " + otherPlayer.getName());
                    clientNamesToStreams.get(otherPlayer.getName()).writeObject(new GamePacket(GameServerMain.GAMESERVER_NAME, otherPlayer.getName(), GameCommand.PLAYER_LEFT));
                    clientNamesToStreams.get(otherPlayer.getName()).writeObject(leavingPlayer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            synchronized (clientNamesToThreads) {
                Logger.logVerbose("RequestsThread", "Interrupting other player thread");
                clientNamesToThreads.get(otherPlayer.getName()).interrupt();
            }

            Logger.logVerbose("RequestsThread", "Logging game and deactivating pair");
            DBHandler.updateGame(leavingPlayerGame);
            try {
                DBHandler.DeactivatePair(leavingPlayerPair.getToken());
            } catch (SQLException e) {
                Logger.logVerbose("RequestsThread", "Failed to deactivate pair!");
            }

            synchronized (Pairs) {
                System.out.println("[RequestsThread][VERBOSE] :: Removing pair from memory");
                Logger.logVerbose("RequestsThread", "Removing pair from memory");
                for (int i = 0; i < Pairs.size(); ++i) {
                    if (Pairs.get(i).getToken().equals(leavingPlayerPair.getToken())) {
                        Logger.logVerbose("RequestsThread", "Pair removed");
                        Pairs.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public void IncreasePlayerRoundsWon(String name) {
        DBHandler.IncreasePlayerRoundsWon(name, 1);
    }

    public void IncreasePlayerRoundsLost(String name) {
        DBHandler.IncreasePlayerRoundsLost(name, 1);
    }

    public JDBCHandler getDBHandler() {
        return DBHandler;
    }
}
