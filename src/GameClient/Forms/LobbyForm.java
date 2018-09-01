package GameClient.Forms;

import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.RMIManagementServerInterface;
import GameClient.Forms.TableModels.ReadOnlyTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class LobbyForm extends UnicastRemoteObject {
    //</editor-fold>
    //<editor-fold desc="Graphical Constants">
    private static final String NAME_COLUMN_TITLE = "Name";
    private static final String WINS_COLUMN_TITLE = "Wins \\ Losses";
    private static final String PAIR_COLUMN_TITLE = "Paired";
    private static final String PLAY_COLUMN_TITLE = "Playing";
    private static final String JOIN_COLUMN_TITLE = "Request Pair";
    private static final int NAME_COLUMN_INDEX = 0;
    private static final int WINS_COLUMN_INDEX = 1;
    private static final int PAIR_COLUMN_INDEX = 2;
    private static final int PLAY_COLUMN_INDEX = 3;
    private static final int JOIN_COLUMN_INDEX = 4;
    //<editor-fold desc="Graphical components">
    private javax.swing.JLabel currentPairLbl;
    private javax.swing.JTextField currentPairNameBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable playersTable;
    private JButton quitBtn;
    private JButton btnGameHistory;
    private JFrame jf;
    //
    private ReadOnlyTableModel playersTableModel;
    private HashMap<Integer, PlayerInternalData> lineIndexToPlayers;
    private RMIManagementServerInterface managementServer;
    private LobbyHandler lobbyHandler;
    private PlayerInternalData PID;

    LobbyForm(RMIManagementServerInterface managementServer, PlayerInternalData PID, LobbyHandler lh) throws RemoteException {
        super();
        this.managementServer = managementServer;
        this.PID = PID;
        jf = new JFrame();
        jf.setTitle(PID.getName() + " Lobby");
        setupUI(jf);
        jf.setVisible(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                managementServer.logout(PID.getName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }));
        lobbyHandler = lh;
        try {
            ArrayList<PlayerInternalData> pls = this.managementServer.getActivePlayersData();
            for (PlayerInternalData p : pls) {
                addPlayerToTable(p);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        lobbyHandler.setOnAccept((Pair) -> {
            String gameServerIP = null;
            try {
                gameServerIP = managementServer.getGameServerIP();
                GameForm gf = new GameForm(gameServerIP, PID, this);
                jf.setVisible(false);
                return;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            //start a gameform and block this form from interacting
            //JOptionPane.showMessageDialog(null, "Player has accepted!! Yay!!\n Now implement this properly you bastard!\nAnd remove this text box please");
        });
        lobbyHandler.setOnPlayerJoined(this::addPlayerToTable);
        lobbyHandler.setOnPlayerLeft((Player) -> {
            for (int i = 0; i < playersTableModel.getRowCount(); i++) {
                String s = (String) playersTableModel.getValueAt(i, 0);
                if (s.equals(Player.getName())) {
                    playersTableModel.removeRow(i);
                    break;
                }
            }
        });
        lobbyHandler.setOnPlayerUpdate((Player) -> {
            for (int i = 0; i < playersTableModel.getRowCount(); ++i) {
                String s = (String) playersTableModel.getValueAt(i, 0);
                if (s.equals(Player.getName())) {
                    playersTableModel.removeRow(i);
                    addPlayerToTable(Player);
                    break;
                }
            }
        });
        playersTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() >= 2) {
                    int row = playersTable.rowAtPoint(mouseEvent.getPoint());
                    //int column = playersTable.rowAtPoint(mouseEvent.getPoint());
                    String playername = (String) playersTable.getValueAt(row, 0);
                    try {
                        managementServer.requestPair(new PlayerInternalData(playername), lobbyHandler);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });
        quitBtn.addActionListener(actionEvent -> {
            try {
                managementServer.logout(PID.getName());
                System.exit(0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        btnGameHistory.addActionListener(actionEvent -> {
            HistoryForm hf = new HistoryForm(managementServer, PID);
        });
    }

    private void addPlayerToTable(PlayerInternalData p) {
        if (!p.getName().equals(PID.getName())) {
            JButton requestButton = new JButton();
            requestButton.setText("Request pair");
            playersTableModel.addRow(new Object[]{p.getName(), p.getWonRounds() + "\\" + p.getLostRounds(), p.getPairedPlayer() != null ? "Yes" : "No", "TODO", requestButton});
        }
    }

    private void setupUI(JFrame j) {
        jScrollPane1 = new javax.swing.JScrollPane();
        playersTable = new javax.swing.JTable();
        currentPairLbl = new javax.swing.JLabel();
        currentPairNameBox = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnGameHistory = new JButton();
        quitBtn = new JButton();
        playersTableModel = new ReadOnlyTableModel(null,
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
        jLabel1.setText("Players In Lobby");
        quitBtn.setText("Quit Lobby");
        btnGameHistory.setText("Game History");
        currentPairNameBox.setFocusable(false);
        currentPairNameBox.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(j.getContentPane());
        j.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(currentPairLbl)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(currentPairNameBox))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnGameHistory)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(quitBtn)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(quitBtn)
                                        .addComponent(btnGameHistory))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(currentPairLbl)
                                        .addComponent(currentPairNameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(17, Short.MAX_VALUE))
        );

        j.pack();
    }

    public int showGUI(GameForm gameForm) {//so only the gameForm can render this again
        int x = gameForm.hashCode();
        jf.setVisible(true);
        return x;
    }

    private static class JTableButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = (JButton) value;
            return button;
        }
    }
}
