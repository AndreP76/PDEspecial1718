package Comunication.JDBCUtils.DBExceptions;

import java.rmi.RemoteException;

public class DuplicateLogoutException extends RemoteException {
    private String name;

    public DuplicateLogoutException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "User " + name + " is not logged in or already logged out";
    }

    public String getName() {
        return name;
    }
}
