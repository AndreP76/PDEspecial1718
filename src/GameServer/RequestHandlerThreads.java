package GameServer;

import Comunication.ChatUtils.DataPackets.GameCommand;
import Comunication.ChatUtils.DataPackets.GamePacket;
import Comunication.JDBCUtils.InternalData.GameInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import GameClient.Forms.GameForm;
import RockPaperScissors.Game;
import RockPaperScissors.GameChoice;
import RockPaperScissors.GameView;
import Utils.Logger;

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
    int gameRound = 0;
    private RequestsThread masterThread;
    private GameInternalData thisPlayerGameData;


    public RequestHandlerThreads(Socket newClientSocket, ObjectInputStream fromClient, ObjectOutputStream toClient, PlayerInternalData playerID, GameInternalData thisPlayerGame, RequestsThread masterThread) {
        toClientStream = toClient;
        fromClientStream = fromClient;
        clientSocket = newClientSocket;
        thisPlayer = playerID;
        this.thisPlayerGameData = thisPlayerGame;
        if (thisPlayerGame != null) {
            thisPlayerGame.getG().addObserver(this);
        }
        this.masterThread = masterThread;
    }

    @Override
    public void run() {
        Logger.logInfo("Request Handler Thread " + this.getName() + ":" + this.getId(), "Handler thread started");
        while (!isInterrupted()) {
            try {
                GamePacket gp = (GamePacket) fromClientStream.readObject();
                Logger.logVerbose("Request Handler Thread " + this.getName() + ":" + this.getId(), "Received a client message");
                if (gp.getCommand() == GameCommand.MAKE_PLAY) {
                    Logger.logVerbose("Request Handler Thread " + this.getName() + ":" + this.getId(), "Message was a move");
                    GameChoice GC = (GameChoice) fromClientStream.readObject();
                    if (!thisPlayerGameData.getG().hasPlayerChoosen(gp.getSender())) {
                        thisPlayerGameData.getG().Play(gp.getSender(), GC);
                    }
                    GameServerMain.saveGame(thisPlayerGameData);
                } else if (gp.getCommand() == GameCommand.PLAYER_LEAVING) {//Oh my, how rude
                    Logger.logVerbose("Request Handler Thread " + this.getName() + ":" + this.getId(), "Message was a leaving notification");
                    masterThread.playerLeaving(thisPlayer, thisPlayerGameData);
                    return;
                }
            } catch (IOException e) {
                Logger.logError("Request Handler Thread " + this.getName() + ":" + this.getId(), "IOException occurred! Thread terminating");
                masterThread.playerLeaving(thisPlayer, thisPlayerGameData);
                return;
            } catch (ClassNotFoundException e) {
                Logger.logInfo("Request Handler Thread " + this.getName() + ":" + this.getId(), "ClassNotFound occurred");
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        System.out.println("[RequestHandlerThread " + this.getName() + ":" + this.getId() + " ][VERBOSE] :: Players game was updated");
        GameView GV = thisPlayerGameData.getG().generateGameView();
        if (thisPlayerGameData.getG().getWinnerIndex() != Game.NOT_DECIDED_INDEX) {//We have a winner
            GamePacket GP = new GamePacket(GameServerMain.GAMESERVER_NAME, thisPlayer.getName(), GameCommand.WINNER_DECIDED);
            if (thisPlayerGameData.getG().getWinnerIndex() == Game.DRAW_INDEX) {
                try {
                    toClientStream.writeObject(GP);
                    toClientStream.writeObject(GameForm.DRAW_NAME);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (thisPlayerGameData.getG().getPlayerNameByIndex(thisPlayerGameData.getG().getWinnerIndex()).equals(thisPlayer.getName())) {
                    masterThread.IncreasePlayerRoundsWon(thisPlayer.getName());
                } else {
                    masterThread.IncreasePlayerRoundsLost(thisPlayer.getName());
                }
                try {
                    toClientStream.writeObject(GP);
                    toClientStream.writeObject(thisPlayerGameData.getG().getPlayerNameByIndex(thisPlayerGameData.getG().getWinnerIndex()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (gameRound != thisPlayerGameData.getG().getCurrentGameRounds()) {
            gameRound = thisPlayerGameData.getG().getCurrentGameRounds();
            //Send the startgame again, so the forms unblock
            try {
                toClientStream.writeObject(new GamePacket(GameServerMain.GAMESERVER_NAME, thisPlayer.getName(), GameCommand.STARTED));
                toClientStream.writeObject(GV);
            } catch (IOException e) {
                e.printStackTrace();
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

    public void setThisPlayerGameData(GameInternalData thisPlayerGameData) {
        if (this.thisPlayerGameData != null) {
            if (this.thisPlayerGameData.getG() != null) {
                this.thisPlayerGameData.getG().deleteObserver(this);
            }
        }
        this.thisPlayerGameData = thisPlayerGameData;
        if (this.thisPlayerGameData != null) {
            if (this.thisPlayerGameData.getG() != null) {
                this.thisPlayerGameData.getG().addObserver(this);
            }
        }
    }

    public void setThisPlayerGame(Game thisPlayerGame) {
        if (this.thisPlayerGameData.getG() != null) {
            this.thisPlayerGameData.getG().deleteObserver(this);
        }
        this.thisPlayerGameData.setG(thisPlayerGame, this);
        if (this.thisPlayerGameData.getG() != null) {
            this.thisPlayerGameData.getG().addObserver(this);
        }
    }
}
