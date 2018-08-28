package GameServer;

import Comunication.ChatUtils.TCPChat.GameCommand;
import Comunication.ChatUtils.TCPChat.GamePacket;
import Comunication.JDBCUtils.InternalData.GameInternalData;
import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.JDBCUtils.JDBCHandler;
import RockPaperScissors.Game;

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
    private ArrayList<PairInternalData> Pairs;
    private JDBCHandler DBHandler;
    private String DBHandlerConnectionString;

    public RequestsThread() {
        try {
            SS = new ServerSocket(0, 256, InetAddress.getLocalHost());
            Pairs = new ArrayList<>();
            clientNamesToGameData = new HashMap<>();
            clientNamesToThreads = new HashMap<>();
            clientNamesToGames = new HashMap<>();
            clientNamesToStreams = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Socket newClientSocket = SS.accept();
                ObjectOutputStream toClient = new ObjectOutputStream(newClientSocket.getOutputStream());//OUTPUT FIRST, or else it blocks
                ObjectInputStream fromClient = new ObjectInputStream(newClientSocket.getInputStream());

                try {
                    GamePacket gp = (GamePacket) fromClient.readObject();
                    if (gp.getCommand() == GameCommand.START_GAME) {
                        PlayerInternalData PlayerID = DBHandler.RetrievePlayer(gp.getSender());
                        Integer index = findIndexOfPair(gp.getSender());
                        if (index != null) {//pair already loaded
                            Game PlayersGame = new Game(Pairs.get(index).getPlayerOne().getName(), Pairs.get(index).getPlayerTwo().getName());
                            clientNamesToGameData.put(Pairs.get(index).getPlayerOne().getName(), PlayersGame);
                            clientNamesToGameData.put(Pairs.get(index).getPlayerTwo().getName(), PlayersGame);

                            clientNamesToThreads.put(gp.getSender(), new RequestHandlerThreads(newClientSocket, fromClient, toClient, PlayerID, null));
                            clientNamesToThreads.get(Pairs.get(index).getPlayerOne().getName()).setThisPlayerGame(PlayersGame);
                            clientNamesToThreads.get(Pairs.get(index).getPlayerTwo().getName()).setThisPlayerGame(PlayersGame);

                            clientNamesToStreams.get(Pairs.get(index).getPlayerOne().getName()).writeObject(new GamePacket(GameServerMain.GAMESERVER_NAME, Pairs.get(index).getPlayerOne().getName(), GameCommand.STARTED));
                            clientNamesToStreams.get(Pairs.get(index).getPlayerOne().getName()).writeObject(new GamePacket(GameServerMain.GAMESERVER_NAME, Pairs.get(index).getPlayerOne().getName(), GameCommand.STARTED));
                        } else {
                            try {
                                PairInternalData PID = DBHandler.getPlayerActivePair(gp.getSender());
                                if (PID != null) {
                                    Pairs.add(PID);//Player data already loaded
                                    clientNamesToStreams.put(gp.getSender(), toClient);
                                    clientNamesToThreads.put(gp.getSender(), new RequestHandlerThreads(newClientSocket, fromClient, toClient, PlayerID, null));
                                } else {
                                    System.out.println("GAME REQUEST HANDLER : Unregistered pair tried to start a game");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
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
}
