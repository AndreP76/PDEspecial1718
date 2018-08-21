package Comunication.JDBCUtils;

import Comunication.JDBCUtils.DBExceptions.AuthenticationSQLError;
import Comunication.JDBCUtils.DBExceptions.DuplicateLoginException;
import Comunication.JDBCUtils.DBExceptions.DuplicateLogoutException;
import Comunication.JDBCUtils.DBExceptions.UnknownUserException;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;

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
        if (j.ConnectToDB()) {
            System.out.println("Database connection test successful!");
            return true;
        } else {
            System.out.println("Database connection test failed...");
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
            TestRetrieval();
            TestQuickRetrieval();
            TestAllUsersQuick();
            TestAllUsersFull();
            TestUserCreation();
            TestAllUsersFull();
            TestLogin();
            TestAllUsersQuick();
            TestLogout();
            TestAllUsersQuick();
            TestUserDeletion();
            TestAllUsersFull();
        }
    }
}
