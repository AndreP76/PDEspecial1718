package GameClient.Forms;

import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.ClientsCallbackInterface;
import Comunication.RMIInterfaces.RMIManagementServerInterface;

import java.io.Serializable;

public class LoginCallbackHandler implements ClientsCallbackInterface, Serializable {
    LoginCallbackHandler() {//helper class because RMI is stupid

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