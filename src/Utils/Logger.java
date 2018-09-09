package Utils;

import java.io.PrintStream;

public class Logger {
    public static PrintStream out = System.out;
    private static boolean DEBUG_enabled = true;
    private static boolean VERBOSE_enabled = true;
    private static boolean ERROR_enabled = true;
    private static boolean WARNING_enabled = true;
    private static boolean STANDARD_enabled = true;
    private static boolean INFO_enabled = true;

    public static void setLoggerStream(PrintStream out) {
        Logger.out = out;
    }

    private static void log(String name, String message, LoggerLevel level) {
        if (levelEnabled(level)) {
            out.println("[" + name + "][" + resolveLevel(level) + "] :: " + message);
        }
    }

    private static boolean levelEnabled(LoggerLevel level) {
        switch (level) {
            case DEBUG:
                return DEBUG_enabled;
            case VERBOSE:
                return VERBOSE_enabled;
            case INFO:
                return INFO_enabled;
            case STANDARD:
                return STANDARD_enabled;
            case WARNING:
                return WARNING_enabled;
            case ERROR:
                return ERROR_enabled;
            default:
                return false;
        }
    }

    public static void logInfo(String name, String message) {
        log(name, message, LoggerLevel.INFO);
    }

    public static void logDebug(String name, String message) {
        log(name, message, LoggerLevel.DEBUG);
    }

    public static void logError(String name, String message) {
        log(name, message, LoggerLevel.ERROR);
    }

    public static void logVerbose(String name, String message) {
        log(name, message, LoggerLevel.VERBOSE);
    }

    public static void logWarning(String name, String message) {
        log(name, message, LoggerLevel.WARNING);
    }

    public static void logStandard(String name, String message) {
        log(name, message, LoggerLevel.STANDARD);
    }

    public static String resolveLevel(LoggerLevel level) {
        switch (level) {
            case INFO:
                return "INFO";
            case DEBUG:
                return "DEBUG";
            case ERROR:
                return "ERROR";
            case VERBOSE:
                return "VERBOSE";
            case WARNING:
                return "WARNING";
            case STANDARD:
                return "STANDARD";
            default:
                return "";
        }
    }

    public static void logException(Throwable e) {
        e.printStackTrace(out);
    }
}
