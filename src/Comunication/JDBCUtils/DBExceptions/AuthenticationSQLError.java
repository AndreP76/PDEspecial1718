package Comunication.JDBCUtils.DBExceptions;

import java.rmi.RemoteException;

public class AuthenticationSQLError extends RemoteException {
    public AuthenticationSQLError() {
    }

    @Override
    public String getMessage() {
        return "Unknown error trying to login";
    }
}
