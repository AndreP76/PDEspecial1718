package Comunication.JDBCUtils.InternalData;

import java.io.Serializable;

public class PairInternalData implements Serializable {
    PlayerInternalData PlayerOne;
    PlayerInternalData PlayerTwo;
    String token;
    Boolean active;
    Integer ID;

    PairInternalData() {
        this(null, null, null, -1, null);
    }

    public PairInternalData(PlayerInternalData PID1, PlayerInternalData PID2, String token, int ID, Boolean active) {
        this.ID = ID;
        this.token = token;
        PlayerOne = PID1;
        PlayerTwo = PID2;
        this.active = active;
    }

    public PlayerInternalData getPlayerOne() {
        return PlayerOne;
    }

    public void setPlayerOne(PlayerInternalData playerOne) {
        PlayerOne = playerOne;
    }

    public PlayerInternalData getPlayerTwo() {
        return PlayerTwo;
    }

    public void setPlayerTwo(PlayerInternalData playerTwo) {
        PlayerTwo = playerTwo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Boolean getActive() {
        return active;
    }
}
