package JDBCUtils;

import java.util.ArrayList;
import java.util.Scanner;

public class JDBCHandlerTestModule {
    public static void main(String[] args) {
        JDBCHandler j = new JDBCHandler("localhost", "3306");
        System.out.println("Database connection test initiated");
        if (j.ConnectToDB()) {
            System.out.println("Database connection test successful!");
            Scanner sIN = new Scanner(System.in);
            System.out.print("User name to search : ");
            PlayerInternalData PID = j.RetrievePlayerByName(sIN.nextLine());
            if (PID == null) {
                System.out.println("Player not found...");
            } else {
                System.out.println("Player " + PID.name + "\nReal name : " + PID.realName +
                        "\nGames Won/Lost : " + PID.wonRounds + "/" + PID.lostRounds +
                        " (" + (((PID.wonRounds + PID.lostRounds) / (PID.wonRounds + 1)) * 100) +
                        "%)\nLogged in : " + (PID.loggedIn ? "True" : "False") +
                        "\n\n User information obtained successfully");
            }
            System.out.println("\nFinding all users\n");
            ArrayList<PlayerInternalData> RS = j.RetrieveAllUsersQuick();
            if (RS != null) {
                System.out.println("Data found :\n <Name> || <State>");
                for (int i = 0; i < RS.size(); ++i) {
                    System.out.println(RS.get(i).name + " || " + RS.get(i).loggedIn);
                }
            } else {
                System.out.println("No data received...");
            }

        } else System.out.println("Database connection test failed...\nShutting down");
    }
}
