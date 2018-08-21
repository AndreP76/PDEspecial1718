package ManagementServer;

import Comunication.ChatUtils.RMIChat.RMIChatRoomModule;
import Comunication.JDBCUtils.DBExceptions.AuthenticationSQLError;
import Comunication.JDBCUtils.DBExceptions.DuplicateLoginException;
import Comunication.JDBCUtils.DBExceptions.DuplicateLogoutException;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

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
            Naming.rebind("//localhost/" + MANAGEMENT_SERVER_RMI, this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
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
                    RMIHeartbeatService RMIHS = new RMIHeartbeatService(DBHandler.getConnectionString());
                    System.out.println("Heartbeat service ready!");
                    try {
                        RMIChatRoomModule RMICRM = new RMIChatRoomModule("RMIC", "127.0.0.1");//TODO : Talvez receber estes parametros por linha de comandos?
                        System.out.println("ChatRoom service ready!");

                        ManagementServerMain Msm = new ManagementServerMain(DBHandler, RMIHS, RMICRM);
                        System.out.println("Server ready!");

                        //GUI.run maybe ?
                        //*******************************NOW TO DO ACTUAL SERVER THINGS*******************************\\

                        Scanner sIN = new Scanner(System.in);
                        while (true) {
                            System.out.print("Command : ");
                            String command = sIN.nextLine();
                            if (command.equals("exit")) {
                                Msm.finalize();
                                System.exit(0);
                                return;
                            }
                        }
                    } catch (RemoteException e) {
                        System.out.println("Remote error on chat service! Shutting down...");
                    } catch (MalformedURLException e) {
                        System.out.println("Wrong URL on chat service! Shutting down...");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } catch (RemoteException e) {
                    System.out.println("Error on heartbeat service! Shutting down...");
                }
            } else {
                System.out.println("Error connecting to DB server, shutting down...");
            }
        } else {
            usage();
            return;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Naming.unbind("//localhost/" + MANAGEMENT_SERVER_RMI);
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
        return DBHandler.CreateUser(name, password, realName);
    }

    @Override
    public boolean login(ClientsCallbackInterface CCI, String name, String password) throws RemoteException {
        try {
            if (DBHandler.LoginUser(name, password)) {
                ClientInterfaces.put(name, CCI);
                return true;
            } else return false;
        } catch (DuplicateLoginException e) {
            CCI.onDuplicateLogin();
            return false;
        } catch (AuthenticationSQLError authenticationSQLError) {
            CCI.onSQLError();
            return false;
        }
    }

    @Override
    public boolean logout(ClientsCallbackInterface CCI, String name) throws RemoteException {
        try {
            return DBHandler.LogoutUser(name);
        } catch (AuthenticationSQLError authenticationSQLError) {
            CCI.onSQLError();
            return false;
        } catch (DuplicateLogoutException e) {
            CCI.onDuplicateLogout();
            return false;
        }
    }

    @Override
    public Collection<String> getActivePlayers() {
        return DBHandler.RetrieveAllUsersNames();
    }

    @Override
    public Collection<PlayerInternalData> getActivePlayersStatus() {
        return DBHandler.RetrieveAllUsersQuick();
    }

    @Override
    public Collection<PlayerInternalData> getActivePlayersPaired() throws SQLException {
        return DBHandler.getPairedClients();
    }

    @Override
    public Collection<PlayerInternalData> getUnpairedActivePlayers() throws SQLException {
        return DBHandler.getUnpairedClients();
    }

    @Override
    public void requestPair(PlayerInternalData player, ClientsCallbackInterface requester) throws RemoteException {
        try {
            if (DBHandler.ClientLoggedIn(requester.getClientInfo().getName())) {//requester is logged in
                if (ClientInterfaces.containsKey(player.getName())) {//target is registred in the server interfaces
                    ClientsCallbackInterface cci = ClientInterfaces.get(player.getName());
                    if (cci.onPairRequested(requester.getClientInfo(), this)) {
                        try {
                            PairInternalData PID = DBHandler.createNewPair(cci.getClientInfo(), requester.getClientInfo(), StringUtils.RandomAlfa(32));
                            cci.onPairRequestAccepted(PID);
                            requester.onPairRequestAccepted(PID);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {//the client rejected
                        cci.onPairRequestRejected();
                    }
                } else {
                    requester.onInvalidPairRequest();
                }
            }
        } catch (AuthenticationSQLError authenticationSQLError) {
            authenticationSQLError.printStackTrace();
        }
    }

    @Override
    public PlayerInternalData getPlayerData(String name) {
        return DBHandler.RetrievePlayer(name);
    }
}
