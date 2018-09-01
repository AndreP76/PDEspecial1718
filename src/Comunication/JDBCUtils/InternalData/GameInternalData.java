package Comunication.JDBCUtils.InternalData;

import GameServer.RequestHandlerThreads;
import RockPaperScissors.Game;
import Utils.StringUtils;

import java.io.Serializable;

public class GameInternalData implements Serializable {
    public static final long serialVersionUID = 42L;
    Game G;
    PairInternalData playingPair;
    String gameToken;

    private int ScorePlayerOne;
    private int ScorePlayerTwo;
    private int ScoreDraws;

    //TODO : use this class
    //When a game is started, create a GameInternalData object and store the Game object here DONE
    //Store the game in the DB
    //then pass this to the handling threads
    //when a game is updated, the handling threads can serialize the game to a binary file on the savegames folder, with the same name as this token

    GameInternalData(Game G, PairInternalData playingPair, String gameToken) {
        this.G = G;
        this.playingPair = playingPair;
        this.gameToken = gameToken;
    }

    public GameInternalData(int playerOneScore, int playerTwoScore, int drawScore, PairInternalData playingPair, String gameToken) {
        this.G = null;
        this.playingPair = playingPair;
        this.gameToken = gameToken;

        ScorePlayerOne = playerOneScore;
        ScorePlayerTwo = playerTwoScore;
        ScoreDraws = drawScore;
    }

    public GameInternalData(Game G, PairInternalData playingPair) {
        this(G, playingPair, StringUtils.RandomAlfa(32));
    }

    public Game getG() {
        return G;
    }

    public PairInternalData getPlayingPair() {
        return playingPair;
    }

    public String getGameToken() {
        return gameToken;
    }

    public int getScoreDraws() {
        if (G != null) {
            return G.getDraws();
        }
        return ScoreDraws;
    }

    public int getScorePlayerOne() {
        if (G != null) {
            return G.getPlayer0Wins();
        }
        return ScorePlayerOne;
    }

    public int getScorePlayerTwo() {
        if (G != null) {
            return G.getPlayer1Wins();
        }
        return ScorePlayerTwo;
    }

    public void setG(Game thisPlayerGame, RequestHandlerThreads requestHandlerThreads) {
        this.G = thisPlayerGame;
    }
}
