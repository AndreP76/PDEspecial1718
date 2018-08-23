package GameClient.Forms;

import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.RMIManagementServerInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class LobbyForm {
    //</editor-fold>
    //<editor-fold desc="Graphical Constants">
    private static final String NAME_COLUMN_TITLE = "Name";
    private static final String WINS_COLUMN_TITLE = "Wins \\ Losses";
    private static final String PAIR_COLUMN_TITLE = "Paired";
    private static final String PLAY_COLUMN_TITLE = "Playing";
    private static final String JOIN_COLUMN_TITLE = "Request Pair";
    //<editor-fold desc="Graphical components">
    private javax.swing.JButton btnCancelPair;
    private javax.swing.JLabel currentPairLbl;
    private javax.swing.JTextField currentPairNameBox;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable playersTable;
    private javax.swing.JButton quitBtn;
    private JFrame jf;
    //
    private DefaultTableModel playersTableModel;
    private HashMap<Integer, PlayerInternalData> lineIndexToPlayers;
    private RMIManagementServerInterface managementServer;
    private LobbyHandler lobbyHandler;
    private PlayerInternalData PID;

    LobbyForm(RMIManagementServerInterface managementServer, PlayerInternalData PID) {
        this.managementServer = managementServer;
        this.PID = PID;
        jf = new JFrame();
        setupUI(jf);
        jf.setVisible(true);
        jf.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {

            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });

        try {
            ArrayList<PlayerInternalData> pls = managementServer.getActivePlayersData();
            for (PlayerInternalData p : pls) {
                if (!p.getName().equals(PID.getName())) {
                    JButton requestButton = new JButton();
                    requestButton.setText("Request pair");
                    requestButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            try {
                                managementServer.requestPair(p, lobbyHandler);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    playersTableModel.addRow(new Object[]{p.getName(), p.getWonRounds() + "\\" + p.getLostRounds(), p.getPairedPlayer() != null ? "Yes" : "No", "TODO", requestButton});
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setupUI(JFrame j) {
        jScrollPane1 = new javax.swing.JScrollPane();
        playersTable = new javax.swing.JTable();
        currentPairLbl = new javax.swing.JLabel();
        currentPairNameBox = new javax.swing.JTextField();
        btnCancelPair = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        quitBtn = new javax.swing.JButton();
        playersTableModel = new javax.swing.table.DefaultTableModel(null,
                new String[]{
                        NAME_COLUMN_TITLE, WINS_COLUMN_TITLE, PAIR_COLUMN_TITLE, PLAY_COLUMN_TITLE, JOIN_COLUMN_TITLE
                }
        );
        j.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        playersTable.setModel(playersTableModel);
        jScrollPane1.setViewportView(playersTable);

        TableCellRenderer tcr = new JTableButtonRenderer();
        playersTable.getColumn(JOIN_COLUMN_TITLE).setCellRenderer(tcr);

        currentPairLbl.setText("Current Pair : ");
        btnCancelPair.setText("Cancel Pair");
        jButton2.setText("Resign Game");
        jLabel1.setText("Players In Lobby");
        quitBtn.setText("Quit Lobby");
        currentPairNameBox.setFocusable(false);
        currentPairNameBox.setEditable(false);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(j.getContentPane());
        j.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(currentPairLbl)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(currentPairNameBox, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancelPair)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(quitBtn)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(quitBtn))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(currentPairLbl)
                                        .addComponent(currentPairNameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCancelPair)
                                        .addComponent(jButton2))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        j.pack();
    }

    private static class JTableButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = (JButton) value;
            return button;
        }
    }
}
