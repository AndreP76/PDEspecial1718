package GameClient;

import Comunication.RMIInterfaces.RMIChatRoomInterface;
import Comunication.RMIInterfaces.RMIManagementServerInterface;
import GameClient.Forms.LoginForm;
import ManagementServer.ManagementServerMain;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientMain implements Serializable {
    public static void main(String[] args) {
        String ManagementServerIP = args.length >= 1 ? args[0] : "localhost";
        String ManagementServerServiceName = args.length >= 2 ? args[1] : ManagementServerMain.MANAGEMENT_SERVER_RMI;
        String ChatServerServiceName = "RMIC";
        //DEBUG ONLY
        //ChatForm ct = new ChatForm(new PlayerInternalData(StringUtils.RandomAlfa(6)));
        try {
            LoginForm lf = new LoginForm((RMIManagementServerInterface) Naming.lookup("rmi://" + ManagementServerIP + "/" + ManagementServerServiceName), (RMIChatRoomInterface) Naming.lookup("//" + ManagementServerIP + "/" + ChatServerServiceName));
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
