package JDBCUtils;

public class PlayerInternalData {
    public static final String UNKNOWN_DATA_STRING = "*********************************";
    public static final int UNKNOWN_DATA_INT = -1;
    int wonRounds;
    int lostRounds;
    String name;
    String realName;
    String password;
    boolean loggedIn;
    int ID;

    public PlayerInternalData(int ID, String realName, String name, int wonRounds, int lostRounds, int loggedIn) {
        this(ID, realName, name, UNKNOWN_DATA_STRING, wonRounds, lostRounds, loggedIn);
    }

    public PlayerInternalData(String name, int loggedIn) {
        this(UNKNOWN_DATA_INT, UNKNOWN_DATA_STRING, name, UNKNOWN_DATA_STRING, UNKNOWN_DATA_INT, UNKNOWN_DATA_INT, loggedIn);
    }

    public PlayerInternalData(int ID, String realName, String name, String password, int wonRounds, int lostRounds, int loggedIn) {
        this.wonRounds = wonRounds;
        this.lostRounds = lostRounds;
        this.password = password;
        this.name = name;
        this.realName = realName;
        this.loggedIn = (loggedIn == 1);
    }
}
