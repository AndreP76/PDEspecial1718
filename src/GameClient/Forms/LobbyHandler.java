package GameClient.Forms;

import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.ClientsCallbackInterface;
import Comunication.RMIInterfaces.RMIManagementServerInterface;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.function.Consumer;

public class LobbyHandler extends UnicastRemoteObject implements ClientsCallbackInterface {
    private PlayerInternalData PID;
    private Consumer<PairInternalData> onAccept;
    private Consumer<PlayerInternalData> onPlayerJoined;
    private Consumer<PlayerInternalData> onPlayerLeft;
    private Consumer<PlayerInternalData> onPlayerUpdate;

    LobbyHandler(PlayerInternalData PID) throws RemoteException {
        super();
        this.PID = PID;
    }

    //<editor-fold desc="Callback interface methods">
    @Override
    public PlayerInternalData getClientInfo() {
        return PID;
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
        int dialogRes = JOptionPane.showConfirmDialog(null, "Player " + clientInfo.getName() + " wants to play with you", "Pair request", JOptionPane.YES_NO_OPTION);
        return dialogRes == JOptionPane.YES_OPTION;
    }

    @Override
    public void onPairRequestRejected() {
        JOptionPane.showMessageDialog(null, "Pair request rejected");
    }

    @Override
    public void onPairRequestAccepted(PairInternalData PID) {
        onAccept.accept(PID);
    }

    @Override
    public void newPlayerJoined(PlayerInternalData PID) {
        onPlayerJoined.accept(PID);
    }

    @Override
    public void playerLeft(PlayerInternalData PID) {
        onPlayerLeft.accept(PID);
    }

    @Override
    public void playerUpdate(PlayerInternalData PID) {
        onPlayerUpdate.accept(PID);
    }
    //</editor-fold>
}
