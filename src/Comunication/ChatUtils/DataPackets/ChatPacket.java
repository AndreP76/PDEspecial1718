package Comunication.ChatUtils.DataPackets;

import java.io.Serializable;

public class ChatPacket implements Serializable {
    public static final String GENERAL_STRING = "<!3V3RY0N3!>";
    public static final String CHAT_SERVER_ID = "CHAT_SERVER";
    public static final String HELLO_STRING = "8101762ade92ec9ed331677efe04cdfd";
    public static final String DUPLICATE_CLIENT = "DUP";
    public static final String UNKNOWN_CLIENT = "UNKNOWN";
    private static final long serialVersionUID = 841634384L;
    private String sender;
    private String target;
    private String messageContents;

    public ChatPacket(String sender, String messageContents) {
        this(sender, GENERAL_STRING, messageContents);
    }

    public ChatPacket(String sender, String target, String messageContents) {
        this.sender = sender;
        this.target = target;
        this.messageContents = messageContents;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageContents() {
        return messageContents;
    }

    public String getTarget() {
        return target;
    }
}
