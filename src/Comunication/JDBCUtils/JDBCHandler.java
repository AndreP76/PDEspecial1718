package Comunication.JDBCUtils;

import Comunication.JDBCUtils.DBExceptions.AuthenticationSQLError;
import Comunication.JDBCUtils.DBExceptions.DuplicateLoginException;
import Comunication.JDBCUtils.DBExceptions.DuplicateLogoutException;
import Comunication.JDBCUtils.DBExceptions.UnknownUserException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

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
            String query = "INSERT INTO Users (Name, Password, RealName) VALUES ('" + PID.name + "','" + PID.password + "','" + PID.realName + "');";
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

    public ArrayList<PlayerInternalData> RetrieveAllUsers() {
        throw new NotImplementedException();
    }

    public Collection<PlayerInternalData> getUnpairedClients() {
        throw new NotImplementedException();
    }

    public Collection<PlayerInternalData> getPairedClients() {
        throw new NotImplementedException();
    }

    public boolean ClientLoggedIn(String name) {
        throw new NotImplementedException();
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
