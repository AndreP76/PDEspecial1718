package RockPaperScissors;

import RockPaperScissors.Exceptions.InvalidCallException;
import RockPaperScissors.Exceptions.InvalidDecisionException;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.Observable;

public class Game extends Observable implements Serializable {
    public static final int DRAW_INDEX = -1;
    public static final int NOT_DECIDED_INDEX = -2;

    int Player0Wins, Player1Wins, Draws;
    private String[] Players;
    int winnerIndex = NOT_DECIDED_INDEX;
    int currentRound = 0;
    int maxRounds = 0;
    private GameChoice[] PlayerChoices;

    public Game(String Player1, String Player2) {
        Player0Wins = Player1Wins = Draws = 0;
        PlayerChoices = new GameChoice[2];
        PlayerChoices[0] = GameChoice.None;
        PlayerChoices[1] = GameChoice.None;
        Players = new String[2];
        Players[0] = Player1;
        Players[1] = Player2;
    }

    public void Play(String s, GameChoice gc) {
        if (Players[0].equals(s))
            Play(gc, 0);
        if (Players[1].equals(s))
            Play(gc, 1);
    }

    void Play(GameChoice C, int PI) {
        if (PlayerChoices[PI] == GameChoice.None) {
            PlayerChoices[PI] = C;
            this.setChanged();
            if (PlayerChoices[0] != GameChoice.None && PlayerChoices[1] != GameChoice.None) {//we can calculate a winner, so wait to notify
                winnerIndex = CalculateGameResult(PlayerChoices);
                if (winnerIndex == DRAW_INDEX) {
                    Draws++;
                } else if (winnerIndex == 0) {
                    Player0Wins++;
                } else if (winnerIndex == 1) {
                    Player1Wins++;
                }
                notifyObservers();
                resetGameAndIncreaseRounds();
            } else {//Don't wait.
                notifyObservers();
            }
        }
    }

    private void resetGameAndIncreaseRounds() {
        PlayerChoices[0] = GameChoice.None;
        PlayerChoices[1] = GameChoice.None;
        winnerIndex = NOT_DECIDED_INDEX;
        setChanged();
        notifyObservers();
    }

    private int CalculateGameResult(GameChoice[] PC) {
        switch (PC[0]) {//Player 0 choice
            case Rock: {
                switch (PC[1]) {
                    case None://WTF?
                        throw new InvalidCallException();
                    case Rock://DRAW
                        return DRAW_INDEX;
                    case Paper://0 Loses
                        return 1;
                    case Scissors:// 0 Wins
                        return 0;
                    case Lizard://0 Wins
                        return 0;
                    case Spock://0 Loses
                        return 1;
                }
                break;
            }
            case Paper: {
                switch (PC[1]) {
                    case None:
                        throw new InvalidCallException();
                    case Rock://0 wins
                        return 0;
                    case Paper://Draw
                        return DRAW_INDEX;
                    case Scissors://0 loses
                        return 1;
                    case Lizard://0 loses
                        return 1;
                    case Spock://0 wins
                        return 0;
                }
                break;
            }
            case Scissors: {
                switch (PC[1]) {
                    case None:
                        throw new InvalidCallException();
                    case Rock:
                        return 1;
                    case Paper:
                        return 0;
                    case Scissors:
                        return DRAW_INDEX;
                    case Lizard:
                        return 0;
                    case Spock:
                        return 1;
                }
                break;
            }
            case Spock: {
                switch (PC[1]) {
                    case None:
                        throw new InvalidCallException();
                    case Rock:
                        return 0;
                    case Paper:
                        return 1;
                    case Scissors:
                        return 0;
                    case Lizard:
                        return 1;
                    case Spock:
                        return DRAW_INDEX;
                }
                break;
            }
            case Lizard: {
                switch (PC[1]) {
                    case None:
                        throw new InvalidCallException();
                    case Rock:
                        return 1;
                    case Paper:
                        return 0;
                    case Scissors:
                        return 1;
                    case Lizard:
                        return DRAW_INDEX;
                    case Spock:
                        return 0;
                }
            }
            case None: {
                throw new InvalidCallException();
            }
            default: {
                throw new InvalidCallException();
            }
        }
        throw new InvalidDecisionException();
    }

    boolean hasPlayerChoosen(int PlayerIndex) {
        return PlayerIndex >= 0 && PlayerIndex <= 1 && PlayerChoices[PlayerIndex] != GameChoice.None;
    }

    GameChoice getPlayerChoice(int PlayerIndex) {
        if (PlayerIndex >= 0 && PlayerIndex <= 1) return PlayerChoices[PlayerIndex];
        else return null;
    }

    public GameView generateGameView() {
        return new GameView(Players[0], PlayerChoices[0] != GameChoice.None, Player0Wins, PlayerChoices[0] != GameChoice.None && PlayerChoices[1] != GameChoice.None ? PlayerChoices[0] : GameChoice.None, Players[1], PlayerChoices[1] != GameChoice.None, Player1Wins, PlayerChoices[0] != GameChoice.None && PlayerChoices[1] != GameChoice.None ? PlayerChoices[1] : GameChoice.None, Draws);
    }

    public boolean hasPlayerChoosen(String sender) throws InvalidObjectException {
        if (Players[0].equals(sender))
            return hasPlayerChoosen(0);
        if (Players[1].equals(sender))
            return hasPlayerChoosen(1);
        throw new InvalidObjectException("Player does not exist");
    }

    public int getWinnerIndex() {
        return winnerIndex;
    }

    public String getPlayerNameByIndex(int winnerIndex) {
        if (winnerIndex >= 0 && winnerIndex <= 1)
            return Players[winnerIndex];
        return null;
    }
}
