package Comunication.JDBCUtils.DBExceptions;

import java.rmi.RemoteException;

public class DuplicateLoginException extends RemoteException {
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
