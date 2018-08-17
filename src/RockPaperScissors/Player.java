package RockPaperScissors;

import java.io.Serializable;

public class Player implements Serializable {
    int PlayerIndexInGame;
    String userName;//from the database
    String userToken;//unused for now
    int PlayerID;//from the database

    Player(int pi) {
        PlayerIndexInGame = pi;
    }
}
