package GameClient.Forms;

import Comunication.JDBCUtils.InternalData.GameInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.RMIManagementServerInterface;
import GameClient.Forms.TableModels.ReadOnlyTableModel;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class HistoryForm {
    private static final String PLAYER_ONE_TITLE = "Player one";
    private static final String PLAYER_TWO_TITLE = "Player two";
    private static final String PLAYER_ONE_WINS_TITLE = "Rounds won by player one";
    private static final String PLAYER_TWO_WINS_TITLE = "Rounds won by player two";
    private static final String DRAWS_TITLE = "Draws";
    private javax.swing.JTable gamesTable;
    private ReadOnlyTableModel gamesTableModel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPlayerName;
    private JFrame jf;
    private RMIManagementServerInterface masterServer;
    private PlayerInternalData playerID;

    HistoryForm(RMIManagementServerInterface masterServer, PlayerInternalData playerID) {
        this.masterServer = masterServer;
        this.playerID = playerID;
        initComponents();
        try {
            ArrayList<GameInternalData> games = masterServer.getPlayerGames(playerID);
            for (GameInternalData GID : games) {
                gamesTableModel.addRow(new Object[]{GID.getPlayingPair().getPlayerOne().getName(), GID.getScorePlayerOne(), GID.getScoreDraws(), GID.getScorePlayerTwo(), GID.getPlayingPair().getPlayerTwo().getName()});
            }
            gamesTable.setModel(gamesTableModel);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        gamesTable = new javax.swing.JTable();
        lblPlayerName = new javax.swing.JLabel();
        jf = new JFrame();
        jf.setTitle(playerID.getName() + "'s Game History");
        gamesTableModel = new ReadOnlyTableModel(null, new String[]{
                PLAYER_ONE_TITLE, PLAYER_ONE_WINS_TITLE, DRAWS_TITLE, PLAYER_TWO_WINS_TITLE, PLAYER_TWO_TITLE
        });

        gamesTable.setModel(gamesTableModel);
        jScrollPane1.setViewportView(gamesTable);

        lblPlayerName.setText("History for " + playerID.getName());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jf.getContentPane());
        jf.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                                        .addComponent(lblPlayerName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblPlayerName, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jf.pack();
        jf.setVisible(true);
    }
}
