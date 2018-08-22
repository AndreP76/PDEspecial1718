package GameClient;

import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.ClientsCallbackInterface;
import Comunication.RMIInterfaces.RMIManagementServerInterface;
import GameClient.Forms.ChatTest;
import ManagementServer.ManagementServerMain;

import java.io.Serializable;

public class ClientMain implements ClientsCallbackInterface, Serializable {
    public static void main(String[] args) {
        String ManagementServerIP = args.length >= 1 ? args[0] : "localhost";
        String ManagementServerServiceName = args.length >= 2 ? args[1] : ManagementServerMain.MANAGEMENT_SERVER_RMI;
        String ChatServerServiceName = "RMIC";
        //DEBUG ONLY
        ChatTest ct = new ChatTest();

        /*try {
            LoginForm lf = new LoginForm((RMIManagementServerInterface) Naming.lookup("//" + ManagementServerIP + "/" + ManagementServerServiceName),(RMIChatRoomInterface) Naming.lookup("//"+ManagementServerIP + "/" + ChatServerServiceName));
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public PlayerInternalData getClientInfo() {
        return null;
    }

    @Override
    public void onDuplicateLogin() {

    }

    @Override
    public void onSQLError() {

    }

    @Override
    public void onDuplicateLogout() {

    }

    @Override
    public void onInvalidPairRequest() {

    }

    @Override
    public boolean onPairRequested(PlayerInternalData clientInfo, RMIManagementServerInterface managementServerMain) {
        return false;
    }

    @Override
    public void onPairRequestRejected() {

    }

    @Override
    public void onPairRequestAccepted(PairInternalData PID) {

    }
}
