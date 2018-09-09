package Comunication.RMIHandlers;

import Comunication.JDBCUtils.JDBCHandler;
import Comunication.RMIInterfaces.RMIHeartbeatInterface;
import Utils.Logger;
import Utils.StringUtils;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIHeartbeatHandler extends Thread {
    private JDBCHandler DBHandler = null;//here because the heartbeats update this
    public static final int HEARTBEAT_INTERVAL_MILIS = 3000; //3 seconds between beats

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
            Logger.logInfo("Heartbeat Thread", "Heartbeat thread starting");
            Logger.logDebug("Heartbeat Thread", "Server ID : " + ID);
            managementServer = (RMIHeartbeatInterface) Naming.lookup(RMIConnectionString);
            String[] DBCredencials = managementServer.hearbeatMethod(this.ID, this.socketAddress);//The initial update
            Logger.logVerbose("Heartbeat Thread", "Creating DBHandler");
            if (DBHandler == null)
                DBHandler = new JDBCHandler(DBCredencials[0], DBCredencials[1], DBCredencials[2], DBCredencials[3]);
            else {
                DBHandler.setDatabaseServerAddressString(DBCredencials[0]);
                DBHandler.setDatabasePortString(DBCredencials[1]);
                DBHandler.setPassword(DBCredencials[3]);
                DBHandler.setUsername(DBCredencials[2]);
            }
            Logger.logVerbose("Heartbeat Thread", "DBHandler created");
        } catch (NotBoundException e) {
            Logger.logError("Heartbeat Thread", "Heartbeat Server not found! Exiting");
            System.exit(-1);
        } catch (MalformedURLException e) {
            Logger.logError("Heartbeat Thread", "Heartbeat Server URL invalid! Exiting");
            System.exit(-2);
        } catch (RemoteException e) {
            Logger.logError("Heartbeat Thread", "Remote error on Heartbeat service! Exiting");
            //maybe log exception too?
            System.exit(-3);
        }
        while (!isInterrupted()) {
            try {
                try {
                    Thread.sleep(HEARTBEAT_INTERVAL_MILIS);
                } catch (InterruptedException e) {
                    Logger.logWarning("Heartbeat Thread", "Thread.sleep interrupted");
                    //maybe log exception too?
                }
                Logger.logVerbose("Heartbeat Thread", "Calling heartbeat method");
                String[] DBCredencials = managementServer.hearbeatMethod(this.ID, this.socketAddress);
                DBHandler.setDatabaseServerAddressString(DBCredencials[0]);
                DBHandler.setDatabasePortString(DBCredencials[1]);
                DBHandler.setPassword(DBCredencials[3]);
                DBHandler.setUsername(DBCredencials[2]);
                Logger.logVerbose("Heartbeat Thread", "DBHandler refreshed");
            } catch (RemoteException e) {
                Logger.logWarning("Heartbeat Thread", "Remote error on heartbeat service");
                //maybe log exception
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
