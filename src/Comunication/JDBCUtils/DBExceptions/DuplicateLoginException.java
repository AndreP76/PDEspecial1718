package Comunication.JDBCUtils.DBExceptions;

public class DuplicateLoginException extends Exception {
    private String user;

    public DuplicateLoginException(String username) {
        user = username;
    }

    @Override
    public String getMessage() {
        return "User " + user + "is already logged in.";
    }

    public String getUser() {
        return user;
    }
}
