package GameClient.Forms;

import Comunication.ChatUtils.TCPChat.GameCommand;
import Comunication.ChatUtils.TCPChat.GamePacket;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import GameServer.GameServerMain;
import RockPaperScissors.GameView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class GameHandler extends Thread {
    private Consumer<String> onWinnerDecided;
    private Consumer<PlayerInternalData> onPlayerQuit;
    private Consumer<Void> onGameStopped;
    private Consumer<GameView> onGameUpdated;
    private Consumer<GameView> onGameStarted;

    private Socket toServerSocket;
    private ObjectInputStream fromServerData;
    private ObjectOutputStream toServerData;
    private PlayerInternalData thisPlayerID;

    GameHandler(String gameServerSocketAddress) {
        try {
            String IPAddress = gameServerSocketAddress.substring(0, gameServerSocketAddress.indexOf(":"));
            toServerSocket = new Socket(IPAddress, Integer.parseInt(gameServerSocketAddress.substring(gameServerSocketAddress.indexOf(":") + 1)));
            toServerData = new ObjectOutputStream(toServerSocket.getOutputStream());//OUTPUT FIRST
            fromServerData = new ObjectInputStream(toServerSocket.getInputStream());//INPUT AFTER OUTPUT
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                GameCommand GP = (GameCommand) fromServerData.readObject();
                if (GP == GameCommand.UPDATED) {
                    GameView gv = (GameView) fromServerData.readObject();
                    onGameUpdated.accept(gv);
                } else if (GP == GameCommand.STARTED) {
                    GameView gid = (GameView) fromServerData.readObject();
                    onGameStarted.accept(gid);
                } else if (GP == GameCommand.STOPPED) {//stopped as in paused, maybe ?
                    onGameStopped.accept(null);
                } else if (GP == GameCommand.WINNER_DECIDED) {
                    String winnerID = (String) fromServerData.readObject();
                    onWinnerDecided.accept(winnerID);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void setOnGameStopped(Consumer<Void> onGameStopped) {
        this.onGameStopped = onGameStopped;
    }

    public void setOnGameStarted(Consumer<GameView> onGameStarted) {
        this.onGameStarted = onGameStarted;
    }

    public void setOnGameUpdated(Consumer<GameView> onGameUpdated) {
        this.onGameUpdated = onGameUpdated;
    }

    public void setOnWinnerDecided(Consumer<String> onWinnerDecided) {
        this.onWinnerDecided = onWinnerDecided;
    }

    public void setOnPlayerQuit(Consumer<PlayerInternalData> onPlayerQuit) {
        this.onPlayerQuit = onPlayerQuit;
    }

    public void sendGameStart() {
        GamePacket gp = new GamePacket(thisPlayerID.getName(), GameServerMain.GAMESERVER_NAME, GameCommand.START_GAME);
        try {
            toServerData.writeObject(gp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
