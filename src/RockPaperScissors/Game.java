package RockPaperScissors;

import RockPaperScissors.Exceptions.InvalidCallException;
import RockPaperScissors.Exceptions.InvalidDecisionException;

import java.io.Serializable;
import java.util.Observable;

public class Game extends Observable implements Serializable {
    public static final int DRAW_INDEX = -1;
    public static final int NOT_DECIDED_INDEX = -2;

    int Player0Wins, Player1Wins, Draws;
    Player[] Players;
    int winnerIndex = NOT_DECIDED_INDEX;
    private GameChoice[] PlayerChoices;

    Game(Player Player1, Player Player2) {
        Player0Wins = Player1Wins = Draws = 0;
        PlayerChoices = new GameChoice[2];
        PlayerChoices[0] = GameChoice.None;
        PlayerChoices[1] = GameChoice.None;
        Players = new Player[2];
        Players[0] = Player1;
        Players[1] = Player2;
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
            } else {//Don't wait.
                notifyObservers();
            }
        }
    }

    int CalculateGameResult(GameChoice[] PC) {
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
}
