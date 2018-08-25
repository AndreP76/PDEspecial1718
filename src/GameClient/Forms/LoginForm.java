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
                    LobbyHandler lh = new LobbyHandler(new PlayerInternalData(name));
                    if (this.master.login(lh, name, passwordBox.getText())) {
                        thisPlayerID = this.master.getPlayerData(name);
                        lh.setPlayerData(thisPlayerID);
                        ChatForm ccf = new ChatForm(chatServer, thisPlayerID);
                        LobbyForm lf = new LobbyForm(master, thisPlayerID, lh);//must carry the lobbyhandler since it's now registred
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
            JPanel jp = new JPanel();
            //<editor-fold desc="Setup Register UI">
            JLabel jLabel1;
            JLabel jLabel2;
            JLabel jLabel3;
            JTextField passwordBox;
            JTextField realNameBox;
            JTextField usernameBox;

            jLabel1 = new javax.swing.JLabel();
            jLabel2 = new javax.swing.JLabel();
            jLabel3 = new javax.swing.JLabel();
            usernameBox = new javax.swing.JTextField();
            realNameBox = new javax.swing.JTextField();
            passwordBox = new javax.swing.JTextField();

            jLabel1.setText("Username :");

            jLabel2.setText("Real name :");

            jLabel3.setText("Password : ");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jp);
            jp.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel3))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(passwordBox, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                                            .addComponent(realNameBox)
                                            .addComponent(usernameBox))
                                    .addContainerGap())
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel1)
                                            .addComponent(usernameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel2)
                                            .addComponent(realNameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel3)
                                            .addComponent(passwordBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            //</editor-fold>
            int result = JOptionPane.showConfirmDialog(null, jp, "Registration form", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (!usernameBox.getText().isEmpty() && !passwordBox.getText().isEmpty() && !realNameBox.getText().isEmpty()) {
                    try {
                        if (master.registerNewClient(usernameBox.getText(), passwordBox.getText(), realNameBox.getText())) {
                            JOptionPane.showMessageDialog(null, "Registration successfull.\nYou can now login!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid credentials!");
                        }
                    } catch (RemoteException e) {
                        JOptionPane.showMessageDialog(null, "Remote error ocurred!", "Remote error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
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
