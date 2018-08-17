package JDBCUtils;

public class PlayerInternalData {
    int wonRounds;
    int lostRounds;
    String name;
    String realName;
    boolean loggedIn;
    int ID;

    public PlayerInternalData(int ID, String realName, String name, int wonRounds, int lostRounds, int loggedIn) {
        this.wonRounds = wonRounds;
        this.lostRounds = lostRounds;
        this.name = name;
        this.realName = realName;
        this.loggedIn = (loggedIn == 1);
    }

    public PlayerInternalData(String name, int loggedIn) {
        this.loggedIn = loggedIn == 1;
        this.name = name;
    }
}
