package Comunication.JDBCUtils;

import Comunication.JDBCUtils.DBExceptions.AuthenticationSQLError;
import Comunication.JDBCUtils.DBExceptions.DuplicateLoginException;
import Comunication.JDBCUtils.DBExceptions.DuplicateLogoutException;
import Comunication.JDBCUtils.DBExceptions.UnknownUserException;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Utils.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class JDBCHandlerTestModule {
    private static JDBCHandler j;

    private static void ShowPlayerInfo(PlayerInternalData PID) {
        if (PID == null) {
            System.out.println("Player data empty");
        } else {
            System.out.println("Player " + PID.getName());
            System.out.println("ID : " + PID.getID());
            System.out.println("Real name : " + PID.getRealName());
            System.out.println("Games Won/Lost : " + PID.getWonRounds() + "/" + PID.getLostRounds());
            System.out.println("Logged in : " + (PID.getLoggedIn() ? "True" : "False"));
        }
    }

    private static void ShowPlayersInfo(Iterable<PlayerInternalData> PIDS) {
        if (PIDS != null) {
            for (PlayerInternalData PID : PIDS) {
                System.out.println(PID.getID() + "\t" + PID.getName() + "\t" + PID.getRealName() + "\t" + PID.getPassword() + "\t" + PID.getLoggedIn() + "\t" + PID.getWonRounds() + "w\t" + PID.getLostRounds() + "l\t");
            }
        } else System.out.println("Empty users data...");
    }

    private static boolean TestConnection() {
        System.out.println("Database connection test initiated");
        try {
            if (j.ConnectToDB()) {
                System.out.println("Database connection test successful!");
                return true;
            } else {
                System.out.println("Database connection test failed...");
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private static void TestRetrieval() {
        System.out.println("\n\nStarting full retrieval test");
        Scanner sIN = new Scanner(System.in);
        System.out.print("User name to search : ");
        String userName = sIN.nextLine();
        PlayerInternalData PID = j.RetrievePlayer(userName);
        ShowPlayerInfo(PID);
    }

    private static void TestQuickRetrieval() {
        System.out.println("\n\nStarting quick retrieval test");
        Scanner sIN = new Scanner(System.in);
        System.out.print("User name to search : ");
        String userName = sIN.nextLine();
        PlayerInternalData PID = j.RetrievePlayerQuick(userName);
        ShowPlayerInfo(PID);
    }

    private static void TestAllUsersQuick() {
        System.out.println("\n\nStarting quick users retrieval test");
        ArrayList<PlayerInternalData> RS = j.RetrieveAllUsersQuick();
        ShowPlayersInfo(RS);
    }

    private static void TestAllUsersFull() {
        System.out.println("\n\nStarting full users retrieval test");
        ArrayList<PlayerInternalData> RS = j.RetrieveAllUsersFull();
        ShowPlayersInfo(RS);
    }

    private static void TestUserCreation() {
        System.out.println("\n\nTesting user creation");
        String name, pass, rname;
        Scanner Sin = new Scanner(System.in);
        System.out.print("New username : ");
        name = Sin.nextLine();
        System.out.print("New password : ");
        pass = Sin.nextLine();
        System.out.print("New realname : ");
        rname = Sin.nextLine();
        if (j.CreateUser(name, pass, rname)) {
            System.out.println("Creation successful");
        } else System.out.println("Creation failed");
    }

    private static boolean TestLogin() {
        System.out.println("\n\nTesting login");
        String name, pass;
        Scanner Sin = new Scanner(System.in);
        System.out.print("Username : ");
        name = Sin.nextLine();
        System.out.print("Password : ");
        pass = Sin.nextLine();
        try {
            if (j.LoginUser(name, pass)) {
                System.out.println("Login success!");
                return true;
            } else {
                System.out.println("Login failed!");
                return false;
            }
        } catch (DuplicateLoginException e) {
            System.out.println("User is already logged in");
            return false;
        } catch (AuthenticationSQLError authenticationSQLError) {
            System.out.println("Error establishing link to DB!");
            return false;
        }
    }

    private static boolean TestLogout() {
        System.out.println("\n\nTesting logout");
        String name;
        Scanner Sin = new Scanner(System.in);
        System.out.print("Username : ");
        name = Sin.nextLine();
        try {
            if (j.LogoutUser(name)) {
                System.out.println("Logout success!");
                return true;
            } else {
                System.out.println("Logout failed!");
                return false;
            }
        } catch (AuthenticationSQLError authenticationSQLError) {
            System.out.println("Error establishing link to DB!");
            return false;
        } catch (DuplicateLogoutException e) {
            System.out.println("User is already logged out");
            return false;
        }
    }

    private static void TestUserDeletion() {
        System.out.println("\n\nTesting deletion.\nBE VERY CAREFUL HERE");
        String name, pass;
        Scanner Sin = new Scanner(System.in);
        System.out.print("Username : ");
        name = Sin.nextLine();
        System.out.print("Password : ");
        pass = Sin.nextLine();
        try {
            if (j.DeleteUser(name, pass)) {
                System.out.println("User successfully deleted!");
            } else System.out.println("User could not be deleted");
        } catch (AuthenticationSQLError authenticationSQLError) {
            System.out.println("Error linking to DB");
        } catch (UnknownUserException e) {
            System.out.println("User does not exist");
        }
    }

    public static void main(String[] args) {
        j = new JDBCHandler("localhost", "3306", "pduser", "pduser");

        if (TestConnection()) {
            //TestRetrieval();
            TestPairDeactivation();
            /*TestQuickRetrieval();
            TestAllUsersQuick();
            TestAllUsersFull();
            TestUserCreation();
            TestAllUsersFull();
            TestLogin();
            TestAllUsersQuick();
            TestLogout();
            TestAllUsersQuick();
            TestUserDeletion();
            TestAllUsersFull();*/
        }
    }

    private static void TestPairDeactivation() {
        String PlayerOneName = StringUtils.RandomLetters(6);
        String PlayerTwoName = StringUtils.RandomLetters(6);
        j.CreateUser(PlayerOneName, StringUtils.RandomAlfa(6), StringUtils.RandomLetters(12));
        System.out.println("[JDBC Test][DEBUG] :: Created player one : " + PlayerOneName);
        j.CreateUser(PlayerTwoName, StringUtils.RandomAlfa(6), StringUtils.RandomLetters(12));
        System.out.println("[JDBC Test][DEBUG] :: Created player two : " + PlayerTwoName);

        PlayerInternalData PlayerOneID = j.RetrievePlayer(PlayerOneName);
        System.out.println("[JDBC Test][DEBUG] :: Retrieved Player One");
        PlayerInternalData PlayerTwoID = j.RetrievePlayer(PlayerTwoName);
        System.out.println("[JDBC Test][DEBUG] :: Retrieved Player Two");

        String OneTwoPairID = StringUtils.RandomAlfa(32);
        try {
            j.createNewPair(PlayerOneID, PlayerTwoID, OneTwoPairID, true);
            System.out.println("[JDBC Test][DEBUG] :: Created new pair [" + OneTwoPairID + "]");
            try {
                j.DeactivatePair(OneTwoPairID);
                System.out.println("[JDBC Test][DEBUG] :: Deactivated pair");
            } catch (SQLException e) {
                System.out.println("[JDBC Test][DEBUG] :: Deactivation of pair failed");
            }
        } catch (SQLException e) {
            System.out.println("[JDBC Test][DEBUG] :: Creation of pair failed");
        }
    }
}
