package GameClient.Forms;

import Colections.EventQueue;
import Comunication.ChatUtils.RMIChat.RMIChatClientModule;
import Comunication.ChatUtils.TCPChat.ChatPacket;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.RMIInterfaces.RMIChatRoomInterface;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;

public class ChatForm implements Serializable {
    private javax.swing.JButton btnSend;
    private DefaultListModel<String> playerListModel;
    private javax.swing.JList<String> playersList;
    private javax.swing.JTabbedPane chatTabs;
    private javax.swing.JTextField messageBox;
    private JScrollPane jScrollPane1;//wtv this is
    private String currentTarget;
    private HashMap<String, JTextArea> namesToTextarea;

    private EventQueue<ChatPacket> cp;
    private EventQueue<String> clis;

    private RMIChatClientModule CCM;

    ChatForm(RMIChatRoomInterface chatServer, PlayerInternalData thisPlayerID) {
        JFrame jf = new JFrame();
        setupUI(jf);
        namesToTextarea = new HashMap<>();
        cp = new EventQueue<>();
        clis = new EventQueue<>();

        /*try {
           CCM = new RMIChatClientModule(chatServer,thisPlayerID,cp,clis);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

        cp.addListener(evi -> {
            try {
                ChatPacket c = cp.dequeue();
                //write message to chat window
                JTextArea jta = findTextarea(c.getTarget().equals(ChatPacket.GENERAL_STRING) ? c.getTarget() : c.getSender());
                jta.append(c.getSender() + " :: " + c.getMessageContents() + "\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        clis.addListener(evi -> {
            try {
                String newClientName = clis.dequeue();
                playerListModel.addElement(newClientName);
                playersList.setModel(playerListModel);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        btnSend.addActionListener(act -> {
            try {
                if (!messageBox.getText().isEmpty()) {
                    CCM.sendMessage(currentTarget, messageBox.getText());
                    JTextArea jta = findTextarea(currentTarget);
                    jta.append("You :: " + messageBox.getText());
                    messageBox.setText("");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        chatTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                currentTarget = chatTabs.getSelectedComponent().getName();
            }
        });
        playersList.setSelectionMode(DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION);
        playersList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                String selectedName = playerListModel.elementAt(listSelectionEvent.getFirstIndex());
                if (!namesToTextarea.containsKey(selectedName)) {//already a tab open
                    addTextarea(selectedName);
                }
                for (Component c : chatTabs.getComponents()) {
                    if (c.getName().equals(selectedName)) {
                        chatTabs.setSelectedComponent(c);
                        break;
                    }
                }
            }
        });

        try {
            for (String c : chatServer.getClients()) {
                playerListModel.addElement(c);
            }
            playersList.setModel(playerListModel);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private JTextArea findTextarea(String s) {
        //if there is no text area, create new panel with s as name,
        // add to tabs, and add a jtextarea to dictionary
        if (namesToTextarea.containsKey(s)) {
            return namesToTextarea.get(s);
        } else {
            addTextarea(s);
            return findTextarea(s);
        }
    }

    private void addTextarea(String s) {
        //create new panel with s as name,
        //add to tabs, and add a jtextarea to dictionary
        JPanel newChatPanel = new JPanel();
        JTextArea jta = new JTextArea();
        SpringLayout layout = new SpringLayout();

        newChatPanel.setName(s);
        newChatPanel.setLayout(layout);

        newChatPanel.add(jta);
        chatTabs.add(newChatPanel);

        layout.putConstraint(SpringLayout.NORTH, jta, 0, SpringLayout.NORTH, newChatPanel);
        layout.putConstraint(SpringLayout.SOUTH, jta, 0, SpringLayout.SOUTH, newChatPanel);
        layout.putConstraint(SpringLayout.EAST, jta, 0, SpringLayout.EAST, newChatPanel);
        layout.putConstraint(SpringLayout.WEST, jta, 0, SpringLayout.WEST, newChatPanel);

        namesToTextarea.put(s, jta);
    }

    private void setupUI(JFrame jf) {
        chatTabs = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        playerListModel = new DefaultListModel<>();
        playersList = new javax.swing.JList<>();
        playersList.setModel(playerListModel);
        messageBox = new javax.swing.JTextField();
        btnSend = new javax.swing.JButton();

        jf.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        playersList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });
        jScrollPane1.setViewportView(playersList);

        messageBox.setText("messageBox");

        btnSend.setText("btnSend");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jf.getContentPane());
        jf.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(messageBox, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                                        .addComponent(chatTabs))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(chatTabs)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(messageBox)
                                        .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))
                                .addContainerGap())
        );

        jf.pack();
        jf.setVisible(true);
    }
}
