package Comunication.JDBCUtils.InternalData;

import RockPaperScissors.Game;
import Utils.StringUtils;

public class GameInternalData {
    Game G;
    PairInternalData playingPair;
    String gameToken;

    //TODO : use this class
    //When a game is started, create a GameInternalData object and store the Game object here
    //Store the game in the DB
    //then pass this to the handling threads
    //when a game is updated, the handling threads can serialize the game to a binary file on the savegames folder, with the same name as this token

    GameInternalData(Game G, PairInternalData playingPair, String gameToken) {
        this.G = G;
        this.playingPair = playingPair;
        this.gameToken = gameToken;
    }

    GameInternalData(Game G, PairInternalData playingPair) {
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
}
