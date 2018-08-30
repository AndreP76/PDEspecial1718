import GameClient.ClientMain;
import GameServer.GameServerMain;
import ManagementServer.ManagementServerMain;

public class DebugMain {
    public static void main(String[] args) {
        Thread managementServerThread = new Thread() {
            @Override
            public void run() {
                ManagementServerMain.main(new String[]{"127.0.0.1", "3306", "pduser", "pduser"});
            }
        };

        Thread gameServerThread = new Thread() {
            @Override
            public void run() {
                GameServerMain.main(new String[]{"127.0.0.1"});
            }
        };

        Thread playerOne = new Thread() {
            @Override
            public void run() {
                ClientMain.main(new String[]{});
            }
        };

        Thread playerTwo = new Thread() {
            @Override
            public void run() {
                ClientMain.main(new String[]{});
            }
        };

        managementServerThread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameServerThread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        playerOne.start();
        playerTwo.start();
    }
}
