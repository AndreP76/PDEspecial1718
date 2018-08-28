package GameServer;

import Comunication.ChatUtils.TCPChat.GameCommand;
import Comunication.ChatUtils.TCPChat.GamePacket;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import RockPaperScissors.Game;
import RockPaperScissors.GameChoice;
import RockPaperScissors.GameView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class RequestHandlerThreads extends Thread implements Observer {
    private ObjectOutputStream toClientStream;
    private ObjectInputStream fromClientStream;
    private Socket clientSocket;
    private PlayerInternalData thisPlayer;
    private Game thisPlayerGame;
    //have game here, add thread as observer, and on update send new view ? Sounds good


    public RequestHandlerThreads(Socket newClientSocket, ObjectInputStream fromClient, ObjectOutputStream toClient, PlayerInternalData playerID, Game thisPlayerGame) {
        toClientStream = toClient;
        fromClientStream = fromClient;
        clientSocket = newClientSocket;
        thisPlayer = playerID;
        this.thisPlayerGame = thisPlayerGame;
        if (thisPlayerGame != null) {
            thisPlayerGame.addObserver(this);
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                GamePacket gp = (GamePacket) fromClientStream.readObject();
                if (gp.getCommand() == GameCommand.MAKE_PLAY) {
                    GameChoice GC = (GameChoice) fromClientStream.readObject();
                    if (!thisPlayerGame.hasPlayerChoosen(gp.getSender())) {
                        thisPlayerGame.Play(gp.getSender(), GC);
                    }
                }/*else if(gp.getCommand() == GameCommand.PLAYER_LOGOUT){
                    //TODO : finish the logout
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        GameView GV = thisPlayerGame.generateGameView();
        GamePacket GP = new GamePacket(GameServerMain.GAMESERVER_NAME, thisPlayer.getName(), GameCommand.UPDATED);
        try {
            toClientStream.writeObject(GP);
            toClientStream.writeObject(GV);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setThisPlayerGame(Game thisPlayerGame) {
        if (this.thisPlayerGame != null) {
            this.thisPlayerGame.deleteObserver(this);
        }
        this.thisPlayerGame = thisPlayerGame;
        if (this.thisPlayerGame != null) {
            this.thisPlayerGame.addObserver(this);
        }
    }
}
