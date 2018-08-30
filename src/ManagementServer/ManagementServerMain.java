package ManagementServer;

import Comunication.ChatUtils.RMIChat.RMIChatRoomModule;
import Comunication.JDBCUtils.DBExceptions.DuplicateLogoutException;
import Comunication.JDBCUtils.DBExceptions.UnknownUserException;
import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.JDBCUtils.JDBCHandler;
import Comunication.RMIHandlers.RMIHeartbeatService;
import Comunication.RMIInterfaces.ClientsCallbackInterface;
import Comunication.RMIInterfaces.RMIManagementServerInterface;
import Utils.StringUtils;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class ManagementServerMain extends UnicastRemoteObject implements RMIManagementServerInterface {
    public static final String MANAGEMENT_SERVER_RMI = "MSRMI";
    private JDBCHandler DBHandler;
    private RMIHeartbeatService RMIHS;

    //*********************RMI MANAGEMENT STUFF**************************\\
    private RMIChatRoomModule RMICRM;
    private HashMap<String, ClientsCallbackInterface> ClientInterfaces;

    private ManagementServerMain(JDBCHandler DBHandler, RMIHeartbeatService RMIHS, RMIChatRoomModule RMICRM) throws RemoteException {
        this.DBHandler = DBHandler;
        this.RMIHS = RMIHS;
        this.RMICRM = RMICRM;
        ClientInterfaces = new HashMap<>();
        if (LocateRegistry.getRegistry() == null) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        try {
            Naming.rebind("rmi://localhost/" + MANAGEMENT_SERVER_RMI, this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Set<String> cc = ClientInterfaces.keySet();
                for (String c : cc) {
                    try {
                        System.out.println("[Management server][VERBOSE] :: Logging out user " + c);
                        DBHandler.LogoutUser(c);
                    } catch (RemoteException e) {
                        if (!(e instanceof DuplicateLogoutException)) {//kinda to be expected, so don't care
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void main(String args[]) {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        if (args.length >= 1) {
            String DBServerIP = args[0];
            JDBCHandler DBHandler;
            if (args.length == 4) {
                DBHandler = new JDBCHandler(DBServerIP, args[1], args[2], args[3]);
            } else if (args.length == 2) {
                DBHandler = new JDBCHandler(DBServerIP, args[1]);
            } else if (args.length == 1) {
                DBHandler = new JDBCHandler(DBServerIP);
            } else {
                usage();
                return;
            }

            if (DBHandler.ConnectToDB()) {
                try {
                    if (LocateRegistry.getRegistry() == null) {
                        LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                    }
                    RMIHeartbeatService RMIHS = new RMIHeartbeatService(DBHandler.getDatabaseServerAddressString(), DBHandler.getDatabasePortString(), DBHandler.getUsername(), DBHandler.getPassword());
                    System.out.println("[Heartbeat service][INFO] :: Heartbeat service started!");
                    try {
                        RMIChatRoomModule RMICRM = new RMIChatRoomModule("RMIC", "127.0.0.1");//TODO : Talvez receber estes parametros por linha de comandos?
                        System.out.println("[ChatRoom Service][INFO] :: ChatRoom service started!");

                        ManagementServerMain Msm = new ManagementServerMain(DBHandler, RMIHS, RMICRM);
                        System.out.println("[ManagementServer][INFO] :: Server ready!");

                        Scanner sIN = new Scanner(System.in);
                        while (true) {
                            System.out.print("Command : ");
                            String command = sIN.nextLine();
                            if (command.equals("exit")) {
                                System.out.println("[Management server][INFO] :: Management server shutting down!");
                                System.exit(0);
                                return;
                            }
                        }
                    } catch (RemoteException e) {
                        System.out.println("[ChatRoom service][ERROR] :: Remote error on ChatRoom Service! Shutting down!");
                        System.err.println("[ChatRoom service][ERROR] :: Remote error on ChatRoom service. Error message : " + e.getMessage() + "\nCaused by : " + e.getCause() + "\nStacktrace : ");
                        for (StackTraceElement ste : e.getStackTrace()) {
                            System.err.println(ste.toString());
                        }
                        System.exit(-5);
                    } catch (MalformedURLException e) {
                        System.out.println("[ChatRoom service][ERROR] :: ChatRoom Service resource URL malformed! Shutting down!");
                        System.exit(-6);
                    }
                } catch (RemoteException e) {
                    System.out.println("[Heartbeat service][ERROR] :: Remote error on Heartbeat Service! Shutting down!");
                    System.err.println("[Heartbeat service][ERROR] :: Remote error on Heartbeat service. Error message : " + e.getMessage() + "\nCaused by : " + e.getCause() + "\nStacktrace : ");
                    for (StackTraceElement ste : e.getStackTrace()) {
                        System.err.println(ste.toString());
                    }
                    System.exit(-7);
                }
            } else {
                System.out.println("[JDBCHandler][ERROR] :: Error establishing link to MariaDB/MySQL server! Shutting down!");
                System.exit(-8);
            }
        } else {
            usage();
            return;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("[Management server][INFO] :: Unbinding Management Server RMI Interface");
        Naming.unbind("rmi://localhost/" + MANAGEMENT_SERVER_RMI);
    }

    private static void usage() {
        System.out.println("programa <IP Base de dados> [porta [username password]]");
    }

    @Deprecated
    @Override
    public boolean registerNewClient(PlayerInternalData PID) {
        return DBHandler.CreateUser(PID);
    }

    @Override
    public boolean registerNewClient(String name, String password, String realName) {
        System.out.println("[Management server][VERBOSE] :: Registering new user : " + name);
        return DBHandler.CreateUser(name, password, realName);
    }

    @Override
    public boolean login(ClientsCallbackInterface CCI, String name, String password) throws RemoteException {
        System.out.println("[Management server][VERBOSE] :: Logging in user : " + name);
            if (DBHandler.LoginUser(name, password)) {
                ClientInterfaces.put(name, CCI);
                PlayerInternalData PID = DBHandler.RetrievePlayer(name);
                for (ClientsCallbackInterface c : ClientInterfaces.values()) {
                    if (!c.getClientInfo().getName().equals(name)) {
                        c.newPlayerJoined(PID);
                    }
                }
                return true;
            } else return false;
    }

    @Override
    public boolean logout(String name) throws RemoteException {
        System.out.println("[Management server][VERBOSE] :: Logging out user : " + name);
        if (ClientInterfaces.containsKey(name)) {
            ClientInterfaces.remove(name);
            for (ClientsCallbackInterface c : ClientInterfaces.values()) {
                c.playerLeft(new PlayerInternalData(name));
            }
        }
        return DBHandler.LogoutUser(name);
    }

    @Override
    public ArrayList<String> getActivePlayers() {
        return DBHandler.RetrieveAllUsersNames();
    }

    @Override
    public ArrayList<PlayerInternalData> getActivePlayersStatus() {
        return DBHandler.RetrieveAllUsersQuick();
    }

    @Override
    public ArrayList<PlayerInternalData> getActivePlayersPaired() throws SQLException {
        return DBHandler.getPairedClients();
    }

    @Override
    public ArrayList<PlayerInternalData> getUnpairedActivePlayers() throws SQLException {
        return DBHandler.getUnpairedClients();
    }

    @Override
    public void requestPair(PlayerInternalData player, ClientsCallbackInterface requester) throws RemoteException {
        System.out.println("[Management server][VERBOSE] :: User " + requester.getClientInfo() + " has requested a pair with " + player.getName());
            if (DBHandler.ClientLoggedIn(requester.getClientInfo().getName())) {//requester is logged in
                System.out.println("[Management server][VERBOSE] :: Requester is logged in and valid");
                if (ClientInterfaces.containsKey(player.getName())) {//target is registred in the server interfaces
                    System.out.println("[Management server][VERBOSE] :: Target is logged in and valid");
                    ClientsCallbackInterface cci = ClientInterfaces.get(player.getName());
                    System.out.println("[Management server][VERBOSE] :: Sending request to target");
                    if (cci.onPairRequested(requester.getClientInfo(), this)) {
                        System.out.println("[Management server][VERBOSE] :: Target accepted");
                        try {
                            PairInternalData PID = DBHandler.createNewPair(cci.getClientInfo(), requester.getClientInfo(), StringUtils.RandomAlfa(32), true);
                            cci.onPairRequestAccepted(PID);
                            requester.onPairRequestAccepted(PID);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {//the client rejected
                        System.out.println("[Management server][VERBOSE] :: Target refused");
                        requester.onPairRequestRejected();
                    }
                } else {
                    throw new UnknownUserException(player.getName());
                }
            }
    }

    @Override
    public PlayerInternalData getPlayerData(String name) {
        return DBHandler.RetrievePlayer(name);
    }

    @Override
    public ArrayList<PlayerInternalData> getActivePlayersData() {
        return DBHandler.RetrieveActiveUsersFull();
    }

    @Override
    public String getGameServerIP() {
        return RMIHS.getGameServerIP();
    }
}
