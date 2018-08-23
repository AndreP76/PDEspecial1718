package Comunication.JDBCUtils.DBExceptions;

import java.rmi.RemoteException;

public class UnknownUserException extends RemoteException {
    String name;

    public UnknownUserException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getMessage() {
        return "Unknown user " + name + " or wrong password";
    }
}
