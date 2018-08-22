package GameClient.Forms;

import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.RMIChatRoomInterface;
import Comunication.RMIInterfaces.RMIManagementServerInterface;

import javax.swing.*;
import java.io.Serializable;
import java.rmi.RemoteException;

public class LoginForm extends JFrame implements Serializable {
    private RMIManagementServerInterface master;

    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JTextField passwordBox;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField usernameBox;
    private javax.swing.JLabel usernameLabel;

    private String name;
    private PlayerInternalData thisPlayerID;

    public LoginForm(RMIManagementServerInterface master, RMIChatRoomInterface chatServer) {
        this.master = master;
        setupUI();
        this.setVisible(true);
        btnLogin.addActionListener(actionEvent -> {
            if (!usernameBox.getText().isEmpty() && !passwordBox.getText().isEmpty()) {
                try {
                    name = usernameBox.getText();
                    if (this.master.login(new LoginCallbackHandler(), name, passwordBox.getText())) {
                        Runtime.getRuntime().addShutdownHook(new Thread() {
                            @Override
                            public void run() {
                                try {
                                    master.logout(null, name);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thisPlayerID = this.master.getPlayerData(name);
                        ChatTest ccf = new ChatTest(chatServer, thisPlayerID);
                        this.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid login credentials!");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        btnRegister.addActionListener(actionEvent -> {

        });
    }

    private void setupUI() {

        usernameBox = new javax.swing.JTextField();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        passwordBox = new javax.swing.JTextField();
        btnLogin = new javax.swing.JButton();
        btnRegister = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        usernameLabel.setText("Username :");

        passwordLabel.setText("Password : ");

        btnLogin.setText("Login");

        btnRegister.setText("Register");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(usernameLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(usernameBox, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(passwordLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(passwordBox))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnRegister, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(usernameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(usernameLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(passwordLabel)
                                        .addComponent(passwordBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(btnLogin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRegister)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }
}
