package Comunication.JDBCUtils.DBExceptions;

public class UnknownUserException extends Exception {
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
