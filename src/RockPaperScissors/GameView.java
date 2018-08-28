package RockPaperScissors;

import java.io.Serializable;

public class GameView implements Serializable {
    private static final long serialVersionUID = 8246821L;
    String playerOne;
    String playerTwo;
    boolean playerOneActed;
    boolean playerTwoActed;
    GameChoice playerOneChoice;
    GameChoice playerTwoChoice;
    int playerOneScore;
    int playerTwoScore;
    int draws;

    GameView(String one, boolean oneActed, int oneScore, GameChoice oneChoice, String two, boolean twoActed, int twoScore, GameChoice twoChoice, int draws) {
        this.draws = draws;
        this.playerOne = one;
        this.playerOneActed = oneActed;
        this.playerOneScore = oneScore;
        this.playerTwo = two;
        this.playerTwoActed = twoActed;
        this.playerTwoScore = twoScore;
        this.playerOneChoice = oneChoice;
        this.playerTwoChoice = twoChoice;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public boolean isPlayerOneActed() {
        return playerOneActed;
    }

    public boolean isPlayerTwoActed() {
        return playerTwoActed;
    }

    public int getPlayerOneScore() {
        return playerOneScore;
    }

    public int getPlayerTwoScore() {
        return playerTwoScore;
    }

    public int getDraws() {
        return draws;
    }

    public GameChoice getPlayerOneChoice() {
        return playerOneChoice;
    }

    public GameChoice getPlayerTwoChoice() {
        return playerTwoChoice;
    }
}
