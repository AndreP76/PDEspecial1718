package GameServer;

import Comunication.ChatUtils.TCPChat.GameCommand;
import Comunication.ChatUtils.TCPChat.GamePacket;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import GameClient.Forms.GameForm;
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
    private RequestsThread masterThread;
    //have game here, add thread as observer, and on update send new view ? Sounds good


    public RequestHandlerThreads(Socket newClientSocket, ObjectInputStream fromClient, ObjectOutputStream toClient, PlayerInternalData playerID, Game thisPlayerGame, RequestsThread masterThread) {
        toClientStream = toClient;
        fromClientStream = fromClient;
        clientSocket = newClientSocket;
        thisPlayer = playerID;
        this.thisPlayerGame = thisPlayerGame;
        if (thisPlayerGame != null) {
            thisPlayerGame.addObserver(this);
        }
        this.masterThread = masterThread;
    }

    @Override
    public void run() {
        System.out.println("[RequestHandlerThread " + this.getName() + ":" + this.getId() + " ][VERBOSE] :: Handler thread started");
        while (!isInterrupted()) {
            try {
                GamePacket gp = (GamePacket) fromClientStream.readObject();
                System.out.println("[RequestHandlerThread " + this.getName() + ":" + this.getId() + " ][VERBOSE] :: Received a client message");
                if (gp.getCommand() == GameCommand.MAKE_PLAY) {
                    System.out.println("[RequestHandlerThread " + this.getName() + ":" + this.getId() + " ][VERBOSE] :: Message was a move");
                    GameChoice GC = (GameChoice) fromClientStream.readObject();
                    if (!thisPlayerGame.hasPlayerChoosen(gp.getSender())) {
                        thisPlayerGame.Play(gp.getSender(), GC);
                    }
                } else if (gp.getCommand() == GameCommand.PLAYER_LEAVING) {//Oh my, how rude
                    masterThread.playerLeaving(thisPlayer, thisPlayerGame);
                    return;
                }
            } catch (IOException e) {
                System.out.println("[RequestHandlerThread " + this.getName() + ":" + this.getId() + " ][ERROR] :: IOException occurred! Thread terminating");
                masterThread.playerLeaving(thisPlayer, thisPlayerGame);
                return;
            } catch (ClassNotFoundException e) {
                System.out.println("[RequestHandlerThread " + this.getName() + ":" + this.getId() + " ][WARNING] :: ClassNotFound occurred!");
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        System.out.println("[RequestHandlerThread " + this.getName() + ":" + this.getId() + " ][VERBOSE] :: Players game was updated");
        GameView GV = thisPlayerGame.generateGameView();
        if (thisPlayerGame.getWinnerIndex() != Game.NOT_DECIDED_INDEX) {//We have a winner
            GamePacket GP = new GamePacket(GameServerMain.GAMESERVER_NAME, thisPlayer.getName(), GameCommand.WINNER_DECIDED);
            if (thisPlayerGame.getWinnerIndex() == Game.DRAW_INDEX) {
                try {
                    toClientStream.writeObject(GP);
                    toClientStream.writeObject(GameForm.DRAW_NAME);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (thisPlayerGame.getPlayerNameByIndex(thisPlayerGame.getWinnerIndex()).equals(thisPlayer.getName())) {
                    //TODO : increment the number of rounds won on the database
                }
                try {
                    toClientStream.writeObject(GP);
                    toClientStream.writeObject(thisPlayerGame.getPlayerNameByIndex(thisPlayerGame.getWinnerIndex()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            GamePacket GP = new GamePacket(GameServerMain.GAMESERVER_NAME, thisPlayer.getName(), GameCommand.UPDATED);
            try {
                System.out.println("[RequestHandlerThread " + this.getName() + ":" + this.getId() + " ][VERBOSE] :: Player's game update sent");
                toClientStream.writeObject(GP);
                toClientStream.writeObject(GV);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
