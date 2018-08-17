package JDBCUtils;

import java.sql.*;
import java.util.ArrayList;

public class JDBCHandler {
    String databaseServerAddressString;
    String databasePortString;
    String databaseNameString = "PD";
    String connectionString;
    String connectionParameters = "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    String username = "pduser";
    String password = "pduser";//coff coff bad password coff coff

    JDBCHandler(String serverIP, String port) {
        this.databaseServerAddressString = serverIP;
        this.databasePortString = port;

        connectionString = "jdbc:mysql://" + databaseServerAddressString + ":" + databasePortString + "/" + databaseNameString + "?" + connectionParameters;
    }

    JDBCHandler(String serverIP) {
        this(serverIP, "3306");
    }

    JDBCHandler() {
        this("127.0.0.1", "3306");
    }

    boolean ConnectToDB() {
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            return true;
        } catch (SQLException sqlex) {
            return false;
        }
    }

    PlayerInternalData RetrievePlayerByName(String userName) {
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            Statement sqlQuery = conn.createStatement();
            String query = "SELECT * FROM Users WHERE BINARY Name = '" + userName + "';";
            ResultSet results = sqlQuery.executeQuery(query);
            if (results != null) {
                results.next();
                return new PlayerInternalData(results.getInt("ID"), results.getString("RealName"), results.getString("Name"), results.getInt("WonRounds"), results.getInt("LostRounds"), results.getInt("LoggedIn"));
            } else return null;
        } catch (SQLException sqlex) {
            return null;
        }
    }

    ArrayList<PlayerInternalData> RetrieveAllUsersQuick() {
        ArrayList<PlayerInternalData> PIDS = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            Statement sqlQuery = conn.createStatement();
            String query = "SELECT Name,LoggedIn FROM Users";
            ResultSet rs = sqlQuery.executeQuery(query);
            while (rs.next()) {
                PIDS.add(new PlayerInternalData(rs.getString("Name"), rs.getInt("LoggedIn")));
            }
            return PIDS;
        } catch (SQLException sqlex) {
            return null;
        }
    }
}
