package RockPaperScissors;

import Utils.StringUtils;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import static RockPaperScissors.Game.NOT_DECIDED_INDEX;

public class GameModuleTest implements Observer {
    /*The actual module*/
    private Game handledGame;

    GameModuleTest(Game handledGame) {
        this.handledGame = handledGame;
        handledGame.addObserver(this);
    }

    /*Launcher method*/
    public static void main(String[] args) {
        System.out.println("Random String : " + StringUtils.RandomAlfa(32));
        Game G = new Game(null, null);
        GameModuleTest GMT = new GameModuleTest(G);
        GMT.FirstPlay();
    }

    void FirstPlay() {
        Scanner sIN = new Scanner(System.in);
        System.out.println();
        System.out.println("Game test started");
        System.out.print("Player 1, select your play (1, 2, 3, 4, 5) : ");
        int choice = sIN.nextInt();
        GameChoice Gc = GameChoice.values()[choice];
        handledGame.Play(Gc, 0);
    }

    @Override
    public void update(Observable observable, Object o) {
        Game Ga = handledGame;
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println(Ga.Player0Wins + " - " + Ga.Draws + " - " + Ga.Player1Wins);
        if (Ga.winnerIndex != NOT_DECIDED_INDEX) {
            System.out.println("Player 1 Choice : " + Ga.getPlayerChoice(0));
            System.out.println("Player 2 Choice : " + Ga.getPlayerChoice(1));
            if (Ga.winnerIndex == 0) {
                System.out.println("Player 1 wins !");
            } else if (Ga.winnerIndex == 1) {
                System.out.println("Player 2 wins !");
            } else {
                System.out.println("It's a draw!");
            }
        } else if (Ga.hasPlayerChoosen(0)) {//Player 2 turn
            System.out.print("Player 2, select your play (1, 2, 3, 4, 5) : ");
            Scanner sIN = new Scanner(System.in);
            int choice = sIN.nextInt();
            GameChoice Gc = GameChoice.values()[choice];
            handledGame.Play(Gc, 1);
        }
    }
}
