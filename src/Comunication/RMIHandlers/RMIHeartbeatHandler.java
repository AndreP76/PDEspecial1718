package Comunication.RMIHandlers;

import Comunication.JDBCUtils.JDBCHandler;
import Comunication.RMIInterfaces.RMIHeartbeatInterface;
import Utils.StringUtils;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIHeartbeatHandler extends Thread {
    private JDBCHandler DBHandler = null;//here because the heartbeats update this
    private int HearbeatMiliseconds = 3000; //3 seconds between beats
    private int HearbeatMaxConsecutiveFailures = 3;//stops on this number of failed heartbeats
    private int CurrentConsecutiveHeatbeatFailures = 0;
    private int CumulativeHeatbeatFailures = 0;//stats only

    private RMIHeartbeatInterface managementServer;
    private String ManagementServerIPAddressString;
    private String RMIConnectionString;
    private String ServiceName = RMIHeartbeatService.RMI_HEARTBEAT_SERVICE_NAME;
    private String ID;

    private String socketAddress;

    public RMIHeartbeatHandler(String IP, String gameSocketAddress) {
        this.ManagementServerIPAddressString = IP;
        RMIConnectionString = "rmi://" + ManagementServerIPAddressString + "/" + ServiceName;
        ID = this.generateID();
        socketAddress = gameSocketAddress;
    }

    private String generateID() {//an MD5 hash or something, so that the management server can keep tabs
        return StringUtils.RandomAlfa(32);
    }

    @Override
    public void run() {
        try {
            System.out.println("[Heartbeat Thread][VERBOSE] :: Thread starting");
            System.out.println("[Heartbeat Thread][INFO] :: ServerID : " + ID);
            managementServer = (RMIHeartbeatInterface) Naming.lookup(RMIConnectionString);
            String[] DBCredencials = managementServer.hearbeatMethod(this.ID, this.socketAddress);//The initial update
            System.out.println("[Heartbeat Thread][VERBOSE] :: Creating DBHandler");
            if (DBHandler == null)
                DBHandler = new JDBCHandler(DBCredencials[0], DBCredencials[1], DBCredencials[2], DBCredencials[3]);
            else {
                DBHandler.setDatabaseServerAddressString(DBCredencials[0]);
                DBHandler.setDatabasePortString(DBCredencials[1]);
                DBHandler.setPassword(DBCredencials[3]);
                DBHandler.setUsername(DBCredencials[2]);
            }
            System.out.println("[Heartbeat Thread][VERBOSE] :: DBHandler created");
        } catch (NotBoundException e) {
            System.out.println("[Heartbeat Thread][ERROR] :: Heartbeat Server not found! Exiting!");
            System.exit(-1);
        } catch (MalformedURLException e) {
            System.out.println("[Heartbeat Thread][ERROR] :: Heartbeat Server URL invalid! Exiting!");
            System.exit(-2);
        } catch (RemoteException e) {
            System.out.println("[Heartbeat Thread][ERROR] :: Remote error on heartbeat service!");
            System.err.println("[Heartbeat Thread][ERROR] :: Remote error on heartbeat service. Error message : " + e.getMessage() + "\nCaused by : " + e.getCause() + "\nStacktrace : ");
            for (StackTraceElement ste : e.getStackTrace()) {
                System.err.println(ste.toString());
            }
            System.exit(-3);
        }
        while (!isInterrupted()) {
            try {
                try {
                    Thread.sleep(HearbeatMiliseconds);
                } catch (InterruptedException e) {
                    System.out.println("[Heartbeat Thread][WARNING] :: Thread.sleep interrupted");
                    System.err.println("[Heartbeat Thread][WARNING] :: Thread.sleep interrupted. Error : " + e.getMessage() + "\nCaused by : " + e.getCause() + "\nStackTrace : ");
                    for (StackTraceElement ste : e.getStackTrace()) {
                        System.err.println(ste.toString());
                    }
                }
                System.out.println("[Heartbeat Thread][VERBOSE] :: Calling heartbeat method");
                String[] DBCredencials = managementServer.hearbeatMethod(this.ID, this.socketAddress);
                System.out.println("[Heartbeat Thread][VERBOSE] :: DBHandler updated");
                DBHandler.setDatabaseServerAddressString(DBCredencials[0]);
                DBHandler.setDatabasePortString(DBCredencials[1]);
                DBHandler.setPassword(DBCredencials[3]);
                DBHandler.setUsername(DBCredencials[2]);
            } catch (RemoteException e) {
                System.out.println("[Heartbeat Thread][WARNING] :: Remote error on heartbeat service!");
                System.err.println("[Heartbeat Thread][WARNING] :: Remote error on heartbeat service. Error message : " + e.getMessage() + "\nCaused by : " + e.getCause() + "\nStacktrace : ");
                for (StackTraceElement ste : e.getStackTrace()) {
                    System.err.println(ste.toString());
                }
                System.exit(-3);
            }
        }
    }

    @Override
    public boolean isInterrupted() {
        return super.isInterrupted();
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    public JDBCHandler getDBHandler() {
        return DBHandler;
    }
}
