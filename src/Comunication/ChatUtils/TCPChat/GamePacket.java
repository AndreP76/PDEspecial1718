package Comunication.ChatUtils.TCPChat;

public class GamePacket {
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
