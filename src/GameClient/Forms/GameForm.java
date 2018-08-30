package GameClient.Forms;

import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import RockPaperScissors.GameChoice;
import RockPaperScissors.GameView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class GameForm {
    public static final String DRAW_NAME = "5551651534531354354355135242466756+8756827+6857+685+6758+7586+756+8756+8567+586";//lulz
    GameHandler gh;
    // <editor-fold defaultstate="collapsed" desc="UI Setup methods">
    private JButton btnLizard;
    private JButton btnPaper;
    private JButton btnQuit;
    private JButton btnRocket;
    private JButton btnScissors;
    private JButton btnSpock;
    private JLabel jLabel2;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JScrollPane jScrollPane1;
    private JLabel lblDraws;
    private JLabel lblPlayerOneChoice;
    private JLabel lblPlayerOneScore;
    private JLabel lblPlayerTwoChoice;
    private JLabel lblPlayerTwoScore;
    private JTextArea textAreaRules;
    private JFrame frame;

    private PlayerInternalData thisPlayerID;
    private LobbyForm lf;

    GameForm(String gameServerIP, PlayerInternalData thisPlayerID, LobbyForm lf) {
        this.lf = lf;
        setupUI();
        this.thisPlayerID = thisPlayerID;
        gh = new GameHandler(gameServerIP, this.thisPlayerID);
        frame.setVisible(true);
        blockForm();

        gh.setOnGameStarted(new Consumer<GameView>() {
            @Override
            public void accept(GameView gameView) {
                unblockForm();
                renderGameView(gameView);
            }
        });
        gh.setOnWinnerDecided(new Consumer<String>() {
            @Override
            public void accept(String playerName) {
                if (playerName.equals(DRAW_NAME)) {
                    JOptionPane.showMessageDialog(null, "This round was a draw...");
                }
                JOptionPane.showMessageDialog(null, playerName + " has won this round!");
                unblockForm();
            }
        });
        gh.setOnGameStopped(new Consumer<Void>() {
            @Override
            public void accept(Void aVoid) {
                //TODO
            }
        });
        gh.setOnGameUpdated(new Consumer<GameView>() {
            @Override
            public void accept(GameView gameView) {
                renderGameView(gameView);
            }
        });
        gh.setOnPlayerQuit(new Consumer<PlayerInternalData>() {//winner by default
            @Override
            public void accept(PlayerInternalData playerInternalData) {

            }
        });

        //Setup the buttons
        btnLizard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gh.sendGameMove(GameChoice.Lizard);
                blockForm();
            }
        });
        btnScissors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gh.sendGameMove(GameChoice.Scissors);
                blockForm();
            }
        });
        btnSpock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gh.sendGameMove(GameChoice.Spock);
                blockForm();
            }
        });
        btnPaper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gh.sendGameMove(GameChoice.Paper);
                blockForm();
            }
        });
        btnRocket.addActionListener(new ActionListener() {//why did I name this button Rocket ?
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gh.sendGameMove(GameChoice.Rock);
                blockForm();
            }
        });

        gh.start();
        gh.sendGameStart();
    }

    private void renderGameView(GameView gameView) {
        lblDraws.setText("Draws : " + gameView.getDraws());
        lblPlayerOneScore.setText(gameView.getPlayerOne() + "'s score : " + gameView.getPlayerOneScore());
        lblPlayerTwoScore.setText(gameView.getPlayerTwo() + "'s score : " + gameView.getPlayerTwoScore());

        lblPlayerOneChoice.setText(gameView.getPlayerOneChoice().toString());
        lblPlayerTwoChoice.setText(gameView.getPlayerTwoChoice().toString());
    }

    private void unblockForm() {
        btnLizard.setEnabled(true);
        btnPaper.setEnabled(true);
        btnSpock.setEnabled(true);
        btnScissors.setEnabled(true);
        btnRocket.setEnabled(true);
        btnQuit.setEnabled(true);
    }

    private void blockForm() {
        btnLizard.setEnabled(false);
        btnPaper.setEnabled(false);
        btnSpock.setEnabled(false);
        btnScissors.setEnabled(false);
        btnRocket.setEnabled(false);
        btnQuit.setEnabled(false);
    }

    private void setupUI() {
        btnSpock = new javax.swing.JButton();
        btnPaper = new javax.swing.JButton();
        btnScissors = new javax.swing.JButton();
        btnRocket = new javax.swing.JButton();
        btnLizard = new javax.swing.JButton();

        jPanel1 = new javax.swing.JPanel();
        lblPlayerOneScore = new javax.swing.JLabel();
        lblDraws = new javax.swing.JLabel();
        lblPlayerTwoScore = new javax.swing.JLabel();

        btnQuit = new javax.swing.JButton();

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaRules = new javax.swing.JTextArea();
        textAreaRules.setEditable(false);
        jPanel3 = new javax.swing.JPanel();
        lblPlayerOneChoice = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblPlayerTwoChoice = new javax.swing.JLabel();
        frame = new JFrame() {
            @Override
            public void dispose() {
                super.dispose();
                gh.sendPlayerLeaving();
                lf.showGUI(GameForm.this);
            }
        };

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        btnSpock.setText("Spock");
        btnPaper.setText("Paper");
        btnScissors.setText("Scissors");
        btnRocket.setText("Rock");
        btnLizard.setText("Lizzard");
        lblPlayerOneScore.setText("Player 1 score");
        lblDraws.setText("Draws");
        lblPlayerTwoScore.setText("Player 2 score");
        btnQuit.setText("Quit Game");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblPlayerTwoScore)
                                        .addComponent(lblPlayerOneScore)
                                        .addComponent(lblDraws))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 158, Short.MAX_VALUE)
                                .addComponent(btnQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnQuit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(lblPlayerOneScore)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblDraws)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblPlayerTwoScore)
                                                .addGap(0, 22, Short.MAX_VALUE)))
                                .addContainerGap())
        );

        textAreaRules.setColumns(20);
        textAreaRules.setRows(5);
        textAreaRules.setText("Scissors wins against Paper and Lizzard\nPaper wins against Rock and Spock\nRock wins against Scissors and Lizzard\nLizzard wins against Spock and Paper\nSpock wins against Scissors and Rock");
        jScrollPane1.setViewportView(textAreaRules);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        lblPlayerOneChoice.setText("Scissors");

        jLabel2.setText("vs");

        lblPlayerTwoChoice.setText("Scissors");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(113, 113, 113)
                                .addComponent(lblPlayerOneChoice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPlayerTwoChoice)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap(44, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblPlayerOneChoice)
                                        .addComponent(jLabel2)
                                        .addComponent(lblPlayerTwoChoice))
                                .addGap(42, 42, 42))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(23, 23, 23)
                                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(btnRocket)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnPaper)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnScissors)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnLizard)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnSpock))
                                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(35, 35, 35)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnRocket)
                                                        .addComponent(btnPaper)
                                                        .addComponent(btnScissors)
                                                        .addComponent(btnLizard)
                                                        .addComponent(btnSpock))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );

        frame.pack();
    }// </editor-fold>
}
