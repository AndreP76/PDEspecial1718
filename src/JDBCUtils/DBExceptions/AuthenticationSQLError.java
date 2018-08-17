package JDBCUtils.DBExceptions;

public class AuthenticationSQLError extends Exception {
    public AuthenticationSQLError() {
    }

    @Override
    public String getMessage() {
        return "Unknown error trying to login";
    }
}
