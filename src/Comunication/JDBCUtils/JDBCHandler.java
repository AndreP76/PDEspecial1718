package Comunication.JDBCUtils;

import Comunication.JDBCUtils.DBExceptions.AuthenticationSQLError;
import Comunication.JDBCUtils.DBExceptions.DuplicateLoginException;
import Comunication.JDBCUtils.DBExceptions.DuplicateLogoutException;
import Comunication.JDBCUtils.DBExceptions.UnknownUserException;
import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;

import java.sql.*;
import java.util.ArrayList;

public class JDBCHandler {
    String databaseServerAddressString;
    String databasePortString;
    String databaseNameString = "PD";
    String connectionString;
    String connectionParameters = "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    String username;
    String password;

    public JDBCHandler(String serverIP, String port, String username, String password) {
        this.databaseServerAddressString = serverIP;
        this.databasePortString = port;
        this.username = username;
        this.password = password;

        connectionString = "jdbc:mysql://" + databaseServerAddressString + ":" + databasePortString + "/" + databaseNameString + "?" + connectionParameters;
    }

    public JDBCHandler(String serverIP, String port) {
        this(serverIP, port, "pduser", "pduser");
    }

    public JDBCHandler(String serverIP) {
        this(serverIP, "3306", "pduser", "pduser");
    }

    public JDBCHandler() {
        this("127.0.0.1", "3306", "pduser", "pduser");
    }

    public boolean ConnectToDB() {
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            return true;
        } catch (SQLException sqlex) {
            return false;
        }
    }

    //Create user-
    //login user-
    //logout user-
    //delete user-
    //Get player details by name-
    //Get player details by ID-
    //Get player quick details (username, log status)

    //Get all players name
    //Get all players quick details
    //Get all players details

    @Deprecated
    //Either fix or delete. Preferably delete
    public boolean CreateUser(PlayerInternalData PID) {
        //TODO : Verificar parametros
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            //TODO : Encriptar a password nem que seja com MD5
            Statement S = conn.createStatement();
            String query = "INSERT INTO Users (Name, Password, RealName) VALUES ('" + PID.getName() + "','" + PID.getPassword() + "','" + PID.getRealName() + "');";
            S.execute(query);
            return true;
        } catch (SQLException sqlex) {
            return false;
        }
    }

    public boolean CreateUser(String Name, String Password, String realName) {
        //TODO : Verificar parametros
        if (Name != null && Password != null && realName != null) {
            try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
                //TODO : Encriptar a password nem que seja com MD5
                Statement s = conn.createStatement();
                String query = "INSERT INTO Users (Name, Password, RealName) VALUES ('" + Name + "','" + Password + "','" + realName + "');";
                s.execute(query);
                return true;
            } catch (SQLException sqlex) {
                return false;
            }
        } else return false;
    }

    public boolean LoginUser(String Name, String Password) throws DuplicateLoginException, AuthenticationSQLError {
        try (Connection con = DriverManager.getConnection(connectionString, username, password)) {
            Statement queryFindUser = con.createStatement();
            String findUserQuery = "SELECT ID,LoggedIn FROM Users WHERE Name = '" + Name + "' AND Password = '" + Password + "';";
            ResultSet rs = queryFindUser.executeQuery(findUserQuery);
            if (rs != null) {
                if (rs.next()) {
                    int ID = rs.getInt("ID");
                    int login = rs.getInt("LoggedIn");
                    if (login == 1) {//user already logged in
                        //TODO : Se calhar guardar a data/hora do ultimo login
                        throw new DuplicateLoginException(Name);
                    } else {
                        String loginUserQuery = "UPDATE Users SET LoggedIn = 1 WHERE ID = " + ID + ";";
                        Statement update = con.createStatement();
                        update.execute(loginUserQuery);
                        return true;
                    }
                } else return false;
            } else return false;

        } catch (SQLException sqlex) {
            throw new AuthenticationSQLError();
        }
    }

    public boolean LogoutUser(String Name) throws AuthenticationSQLError, DuplicateLogoutException {
        try (Connection con = DriverManager.getConnection(connectionString, username, password)) {
            Statement queryFindUser = con.createStatement();
            String findUserQuery = "SELECT ID,LoggedIn FROM Users WHERE Name = '" + Name + "';";
            ResultSet rs = queryFindUser.executeQuery(findUserQuery);
            if (rs != null) {
                if (rs.next()) {
                    int ID = rs.getInt("ID");
                    int login = rs.getInt("LoggedIn");
                    if (login == 0) {//user not logged in
                        throw new DuplicateLogoutException(Name);
                    } else {
                        String loginUserQuery = "UPDATE Users SET LoggedIn = 0 WHERE ID = " + ID + ";";
                        Statement update = con.createStatement();
                        update.execute(loginUserQuery);
                        return true;
                    }
                } else return false;
            } else return false;

        } catch (SQLException sqlex) {
            throw new AuthenticationSQLError();
        }
    }

    public boolean DeleteUser(String Name, String Password) throws AuthenticationSQLError, UnknownUserException {
        try (Connection con = DriverManager.getConnection(connectionString, username, password)) {
            Statement queryFindUser = con.createStatement();
            String findUserQuery = "SELECT ID,LoggedIn FROM Users WHERE Name = '" + Name + "' AND Password = '" + Password + "';";
            ResultSet rs = queryFindUser.executeQuery(findUserQuery);
            if (rs != null) {
                if (rs.next()) {
                    int ID = rs.getInt("ID");
                    String deleteUser = "DELETE FROM Users WHERE ID = " + ID + ";";
                    Statement delete = con.createStatement();
                    delete.execute(deleteUser);
                    return true;
                } else throw new UnknownUserException(Name);
            } else throw new UnknownUserException(Name);
        } catch (SQLException sqlex) {
            throw new AuthenticationSQLError();
        }
    }

    public PlayerInternalData RetrievePlayer(String userName) {
        //TODO : Verificar parametros
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            Statement sqlQuery = conn.createStatement();
            String query = "SELECT * FROM Users WHERE Name = '" + userName + "';";
            ResultSet results = sqlQuery.executeQuery(query);
            if (results != null) {
                return results.next() ? new PlayerInternalData(results.getInt("ID"), results.getString("RealName"), results.getString("Name"), results.getInt("WonRounds"), results.getInt("LostRounds"), results.getInt("LoggedIn")) : null;
            } else return null;
        } catch (SQLException sqlex) {
            return null;
        }
    }

    public PlayerInternalData RetrievePlayer(int ID) {
        //TODO : Verificar parametros
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            Statement sqlQuery = conn.createStatement();
            String query = "SELECT * FROM Users WHERE ID = " + ID + ";";
            ResultSet results = sqlQuery.executeQuery(query);
            if (results != null) {
                return results.next() ? new PlayerInternalData(results.getInt("ID"), results.getString("RealName"), results.getString("Name"), results.getInt("WonRounds"), results.getInt("LostRounds"), results.getInt("LoggedIn")) : null;
            } else return null;
        } catch (SQLException sqlex) {
            return null;
        }
    }

    public PlayerInternalData RetrievePlayerQuick(String name) {
        //TODO : Verificar parametros
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            Statement sqlQuery = conn.createStatement();
            String query = "SELECT Name,LoggedIn FROM Users WHERE Name = '" + name + "';";
            ResultSet results = sqlQuery.executeQuery(query);
            if (results != null) {
                return results.next() ? new PlayerInternalData(results.getString("Name"), results.getInt("LoggedIn")) : null;
            } else return null;
        } catch (SQLException sqlex) {
            return null;
        }
    }

    public PlayerInternalData RetrievePlayerQuick(int ID) {
        //TODO : Verificar parametros
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            Statement sqlQuery = conn.createStatement();
            String query = "SELECT Name,LoggedIn FROM Users WHERE ID = " + ID + ";";
            ResultSet results = sqlQuery.executeQuery(query);
            if (results != null) {
                return results.next() ? new PlayerInternalData(results.getString("Name"), results.getInt("LoggedIn")) : null;
            } else return null;
        } catch (SQLException sqlex) {
            return null;
        }
    }

    public ArrayList<PlayerInternalData> RetrieveAllUsersQuick() {
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

    public ArrayList<PlayerInternalData> RetrieveAllUsersFull() {
        ArrayList<PlayerInternalData> PIDS = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            Statement sqlQuery = conn.createStatement();
            String query = "SELECT * FROM Users";
            ResultSet rs = sqlQuery.executeQuery(query);
            while (rs.next()) {
                PIDS.add(new PlayerInternalData(rs.getInt("ID"), rs.getString("RealName"), rs.getString("Name"), rs.getInt("WonRounds"), rs.getInt("LostRounds"), rs.getInt("LoggedIn")));
            }
            return PIDS;
        } catch (SQLException sqlex) {
            return null;
        }
    }

    public ArrayList<PlayerInternalData> getUnpairedClients() throws SQLException {
        ArrayList<PlayerInternalData> PIDS = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(connectionString, username, password)) {
            String query = "SELECT Name,LoggedIn,ID,LostRounds,RealName,WonRounds FROM Users WHERE ID NOT IN (SELECT PlayerID FROM Pairs WHERE Active = 1) AND ID NOT IN (SELECT PlayerTwo FROM Pairs WHERE Active = 1)";
            ResultSet rs = c.createStatement().executeQuery(query);
            if (rs != null) {
                while (rs.next()) {
                    PIDS.add(new PlayerInternalData(rs.getInt("ID"), rs.getString("RealName"), rs.getString("Name"), rs.getInt("WonRounds"), rs.getInt("LostRounds"), rs.getInt("LoggedIn")));
                }
                return PIDS;
            } else return null;
        }
    }

    public ArrayList<PlayerInternalData> getPairedClients() throws SQLException {
        ArrayList<PlayerInternalData> PIDS = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(connectionString, username, password)) {
            String query = "SELECT Name,LoggedIn,ID,LostRounds,RealName,WonRounds FROM Users WHERE ID IN (SELECT PlayerID FROM Pairs WHERE Active = 1) OR ID IN (SELECT PlayerTwo FROM Pairs WHERE Active = 1)";
            ResultSet rs = c.createStatement().executeQuery(query);
            if (rs != null) {
                while (rs.next()) {
                    PIDS.add(new PlayerInternalData(rs.getInt("ID"), rs.getString("RealName"), rs.getString("Name"), rs.getInt("WonRounds"), rs.getInt("LostRounds"), rs.getInt("LoggedIn")));
                }
                return PIDS;
            } else return null;
        }
    }

    public boolean ClientLoggedIn(String name) throws AuthenticationSQLError {
        try (Connection con = DriverManager.getConnection(connectionString, username, password)) {
            Statement queryFindUser = con.createStatement();
            String findUserQuery = "SELECT ID FROM Users WHERE Name = '" + name + "' AND LoggedIn = 1;";
            ResultSet rs = queryFindUser.executeQuery(findUserQuery);
            if (rs != null) {
                return rs.next();
            } else return false;
        } catch (SQLException sqlex) {
            throw new AuthenticationSQLError();
        }
    }

    public ArrayList<String> RetrieveAllUsersNames() {
        ArrayList<String> PIDS = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(connectionString, username, password)) {
            Statement sqlQuery = conn.createStatement();
            String query = "SELECT Name FROM Users";
            ResultSet rs = sqlQuery.executeQuery(query);
            while (rs.next()) {
                PIDS.add(rs.getString("Name"));
            }
            return PIDS;
        } catch (SQLException sqlex) {
            return null;
        }
    }

    //***************************|PAIRS|****************************\\

    public PairInternalData getPair(String token) throws SQLException {
        try (Connection con = DriverManager.getConnection(connectionString, username, password)) {
            Statement S = con.createStatement();
            String query = "SELECT * FROM Pairs WHERE Token = '" + token + "';";
            ResultSet rs = S.executeQuery(query);
            if (rs != null) {
                if (rs.next()) {
                    PairInternalData PID = new PairInternalData(RetrievePlayer(rs.getInt("PlayerID")), RetrievePlayer(rs.getInt("PlayerTwo")), token, rs.getInt("ID"));
                    return PID;
                } else return null;
            } else return null;
        }
    }

    public PairInternalData getPair(int ID) throws SQLException {
        try (Connection con = DriverManager.getConnection(connectionString, username, password)) {
            Statement S = con.createStatement();
            String query = "SELECT * FROM Pairs WHERE ID = " + ID + ";";
            ResultSet rs = S.executeQuery(query);
            if (rs != null) {
                if (rs.next()) {
                    PairInternalData PID = new PairInternalData(RetrievePlayer(rs.getInt("PlayerID")), RetrievePlayer(rs.getInt("PlayerTwo")), rs.getString("Token"), rs.getInt("ID"));
                    return PID;
                } else return null;
            } else return null;
        }
    }

    public PairInternalData createNewPair(PlayerInternalData P1, PlayerInternalData P2, String token) throws SQLException {
        if (P1.getID() == PlayerInternalData.UNKNOWN_DATA_INT) {//unknown ID
            P1 = RetrievePlayer(P1.getName());
        }

        if (P2.getID() == PlayerInternalData.UNKNOWN_DATA_INT) {//unknown ID
            P2 = RetrievePlayer(P2.getName());
        }

        if (P1 != null && P2 != null && token != null) {
            try (Connection con = DriverManager.getConnection(connectionString, username, password)) {
                Statement S = con.createStatement();
                String newPairQuery = "INSERT INTO Pairs (PlayerID, PlayerTwo, Token) VALUES (" + P1.getID() + "," + P2.getID() + ",'" + token + "');";
                S.execute(newPairQuery);
                return getPair(token);
            }
        } else return null;
    }

    public ArrayList<PairInternalData> getActivePairs() throws SQLException {
        ArrayList<PairInternalData> PIDS = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(connectionString, username, password)) {
            Statement s = c.createStatement();
            String getQuery = "SELECT * FROM Pairs WHERE Active = 1";
            ResultSet rs = s.executeQuery(getQuery);
            if (rs != null) {
                while (rs.next()) {
                    PIDS.add(new PairInternalData(RetrievePlayer(rs.getInt("PlayerID")), RetrievePlayer("PlayerTwo"), rs.getString("Token"), rs.getInt("ID")));
                }
                return PIDS;
            } else return null;
        }
    }

    //********************* GETTERS ********************************\\
    public String getDatabaseServerAddressString() {
        return databaseServerAddressString;
    }
    public String getConnectionString() {
        return connectionString;
    }
    public String getDatabaseNameString() {
        return databaseNameString;
    }
    public String getDatabasePortString() {
        return databasePortString;
    }
    //********************* SETTERS ********************************\\
    public void setDatabaseServerAddressString(String databaseServerAddressString) {
        this.databaseServerAddressString = databaseServerAddressString;
        refreshConnectionString();
    }
    public void setDatabasePortString(String databasePortString) {
        this.databasePortString = databasePortString;
        refreshConnectionString();
    }
    public void setDatabaseNameString(String databaseNameString) {
        this.databaseNameString = databaseNameString;
        refreshConnectionString();
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    //********************** MISC **********************************\\
    private void refreshConnectionString() {
        connectionString = "jdbc:mysql://" + databaseServerAddressString + ":" + databasePortString + "/" + databaseNameString + "?" + connectionParameters;
    }
}
