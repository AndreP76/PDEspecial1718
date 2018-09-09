package ManagementServer;

import Comunication.ChatUtils.RMIChat.RMIChatRoomModule;
import Comunication.JDBCUtils.DBExceptions.DuplicateLogoutException;
import Comunication.JDBCUtils.DBExceptions.UnknownUserException;
import Comunication.JDBCUtils.InternalData.GameInternalData;
import Comunication.JDBCUtils.InternalData.PairInternalData;
import Comunication.JDBCUtils.InternalData.PlayerInternalData;
import Comunication.JDBCUtils.JDBCHandler;
import Comunication.RMIHandlers.RMIHeartbeatService;
import Comunication.RMIInterfaces.ClientsCallbackInterface;
import Comunication.RMIInterfaces.RMIManagementServerInterface;
import Utils.Logger;
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
    private final HashMap<String, ClientsCallbackInterface> ClientInterfaces = new HashMap<>();

    private ManagementServerMain(JDBCHandler DBHandler, RMIHeartbeatService RMIHS, RMIChatRoomModule RMICRM) throws RemoteException {
        this.DBHandler = DBHandler;
        this.RMIHS = RMIHS;
        this.RMICRM = RMICRM;
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
                synchronized (ClientInterfaces) {
                    Set<String> cc = ClientInterfaces.keySet();
                    for (String c : cc) {
                        try {
                            Logger.logInfo("Management server", "Logging out user " + c);
                            DBHandler.LogoutUser(c);
                        } catch (RemoteException e) {
                            if (!(e instanceof DuplicateLogoutException)) {//kinda to be expected, so don't care
                                e.printStackTrace();
                            }
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

            try {
                if (DBHandler.ConnectToDB()) {
                    try {
                        if (LocateRegistry.getRegistry() == null) {
                            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                        }
                        RMIHeartbeatService RMIHS = new RMIHeartbeatService(DBHandler.getDatabaseServerAddressString(), DBHandler.getDatabasePortString(), DBHandler.getUsername(), DBHandler.getPassword());
                        Logger.logInfo("Management server", "Heartbeat service started");
                        try {
                            RMIChatRoomModule RMICRM = new RMIChatRoomModule("RMIC", "127.0.0.1");
                            Logger.logInfo("Management server", "ChatRoom service started!");

                            ManagementServerMain Msm = new ManagementServerMain(DBHandler, RMIHS, RMICRM);
                            Logger.logInfo("Management server", "Server ready!");

                            Scanner sIN = new Scanner(System.in);
                            while (true) {
                                System.out.print("Command : ");
                                String command = sIN.nextLine();
                                if (command.equals("exit")) {
                                    Logger.logInfo("Management server", "Management server shutting down");
                                    System.exit(0);
                                    return;
                                }
                            }
                        } catch (RemoteException e) {
                            Logger.logError("Management server", "Remote error on ChatRoom Service! Shutting down!");
                            Logger.logException(e);
                            System.exit(-5);
                        } catch (MalformedURLException e) {
                            Logger.logError("Management server", "ChatRoom Service resource URL malformed! Shutting down!");
                            System.exit(-6);
                        }
                    } catch (RemoteException e) {
                        Logger.logError("Management server", "Remote error on Heartbeat Service! Shutting down!");
                        System.exit(-7);
                    }
                } else {
                    Logger.logError("JDBCHandler", "Error establishing link to MariaDB/MySQL server! Shutting down!");
                    System.exit(-8);
                }
            } catch (SQLException e) {
                Logger.logError("JDBCHandler", "Could not connect to MariaDB/MySQL server! Shutting down!");
                Logger.logException(e);
            }
        } else {
            usage();
            return;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Logger.logInfo("Management server", "Unbinding Management Server RMI Interface");
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
        Logger.logVerbose("Management server", "Registering new user " + name);
        return DBHandler.CreateUser(name, password, realName);
    }

    @Override
    public boolean login(ClientsCallbackInterface CCI, String name, String password) throws RemoteException {
        Logger.logVerbose("Management server", "Logging in user : " + name);
        synchronized (ClientInterfaces) {
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
    }

    @Override
    public boolean logout(String name) throws RemoteException {
        Logger.logVerbose("Management server", "Logging out user : " + name);
        synchronized (ClientInterfaces) {
            if (ClientInterfaces.containsKey(name)) {
                ClientInterfaces.remove(name);
                for (ClientsCallbackInterface c : ClientInterfaces.values()) {
                    c.playerLeft(new PlayerInternalData(name));
                }
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
        Logger.logVerbose("Management server", "User " + requester.getClientInfo() + " has requested a pair with " + player.getName());
            if (DBHandler.ClientLoggedIn(requester.getClientInfo().getName())) {//requester is logged in
                Logger.logVerbose("Management server", "Requester is logged in and valid");
                if (ClientInterfaces.containsKey(player.getName())) {//target is registred in the server interfaces
                    Logger.logVerbose("Management server", "Target is logged in and valid");
                    ClientsCallbackInterface cci = ClientInterfaces.get(player.getName());
                    Logger.logVerbose("Management server", "Sending request to target");
                    if (cci.onPairRequested(requester.getClientInfo(), this)) {
                        Logger.logVerbose("Management server", "Target accepted");
                        try {
                            PairInternalData PID = DBHandler.createNewPair(cci.getClientInfo(), requester.getClientInfo(), StringUtils.RandomAlfa(32), true);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            cci.onPairRequestAccepted(PID);
                            requester.onPairRequestAccepted(PID);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {//the client rejected
                        Logger.logVerbose("Management server", "Target refused");
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

    @Override
    public ArrayList<GameInternalData> getPlayerGames(PlayerInternalData playerID) {
        return DBHandler.retriveAllGamesForPlayer(playerID.getName());
    }
}
