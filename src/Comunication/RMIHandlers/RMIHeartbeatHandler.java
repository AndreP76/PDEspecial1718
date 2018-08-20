package Comunication.RMIHandlers;

import Comunication.JDBCUtils.JDBCHandler;
import Comunication.RMIInterfaces.RMIHeartbeatInterface;

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
    private String ServiceName = "ManagementServerRMI";
    private String ID;

    public RMIHeartbeatHandler(String IP) {
        this.ManagementServerIPAddressString = IP;
        RMIConnectionString = "//" + ManagementServerIPAddressString + "/" + ServiceName;
        ID = this.generateID();
    }

    private String generateID() {//an MD5 hash or something, so that the management server can keep tabs
        //TODO : Generate unique ID
        return "TESTING_ID_CODE_31235258598978456486";
    }

    @Override
    public synchronized void start() {
        super.start();
        this.run();
    }

    @Override
    public void run() {
        try {
            managementServer = (RMIHeartbeatInterface) Naming.lookup(RMIConnectionString);
            String IPString = managementServer.hearbeatMethod(this.ID);//The initial update
            DBHandler.setDatabaseServerAddressString(IPString);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        while (!isInterrupted()) {
            try {
                Thread.sleep(HearbeatMiliseconds);
                String IPString = managementServer.hearbeatMethod(this.ID);
                DBHandler.setDatabaseServerAddressString(IPString);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
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
}
