package Comunication.ChatUtils.DataPackets;

import java.io.Serializable;

public class GamePacket implements Serializable {
    private static final long serialVersionUID = 576834683L;
    String sender;
    String target;
    GameCommand command;

    public GamePacket(String sender, String target, GameCommand command) {
        this.command = command;
        this.target = target;
        this.sender = sender;
    }

    public String getTarget() {
        return target;
    }

    public String getSender() {
        return sender;
    }

    public GameCommand getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return sender + "->" + target + " :: " + command.toString();
    }
}
