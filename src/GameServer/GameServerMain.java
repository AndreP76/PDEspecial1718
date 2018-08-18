package GameServer;

import Comunication.RMIHandlers.RMIGameServerHandler;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GameServerMain {
    public static final String DATAPATH = "./Data/";
    public static final String SAVEDGAMES_FOLDER = DATAPATH + "SavedGames";
    public static InetAddress ManagementServerIP;

    public static RMIGameServerHandler HeartbeatThread;

    public static void main(String args[]) {
        if (args.length > 0) {
            try {
                ManagementServerIP = Inet4Address.getByName(args[0]);
                HeartbeatThread = new RMIGameServerHandler(args[0]);
                HeartbeatThread.start();//There, Heartbeats done. Now piss off m8

                //Now I need to start doing server things
                //But what ?
                //Maybe listen to some kinda requests ?


            } catch (UnknownHostException e) {
                System.out.println("Cannot find management server. Shutting down");
            }
        }
    }
}
