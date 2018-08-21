package GameClient;

import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.ClientsCallbackInterface;
import Comunication.RMIInterfaces.RMIManagementServerInterface;

import javax.swing.*;
import java.rmi.RemoteException;

public class ClientLoginForm extends JFrame implements ClientsCallbackInterface {
    RMIManagementServerInterface master;
    private JPanel mainPanel;
    private JTextField usernameBox;
    private JTextField passwordBox;
    private JButton btnLogin;
    private JButton btnRegister;
    private String name;
    private PlayerInternalData thisPlayerID;

    ClientLoginForm(RMIManagementServerInterface master) {
        this.master = master;
        this.setContentPane(mainPanel);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        btnLogin.addActionListener(actionEvent -> {
            if (!usernameBox.getText().isEmpty() && !passwordBox.getText().isEmpty()) {
                try {
                    name = usernameBox.getText();
                    if (master.login(this, name, passwordBox.getText())) {
                        thisPlayerID = master.getPlayerData(name);
                        //launch next form and close this one
                    } else {
                        //clear fields and show error message
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        btnRegister.addActionListener(actionEvent -> {

        });
    }

    @Override
    public PlayerInternalData getClientInfo() {
        if (thisPlayerID == null)
            return new PlayerInternalData(name);
        else return thisPlayerID;
    }

    @Override
    public void onDuplicateLogin() {
        JOptionPane.showMessageDialog(null, "Player is already logged in! If you believe this to be an error, ask the game administrator for help");
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
