package Comunication.ChatUtils;

import java.io.Serializable;

public class ChatPacket implements Serializable {
    private static final long serialVersionUID = 841634384L;
    public static final String GENERAL_STRING = "<!3V3RY0N3!>";
    public static final String GAME_SERVER_ID = "CHAT_SERVER";
    public static final String HELLO_STRING = "8101762ade92ec9ed331677efe04cdfd";
    private String sender;
    private String target;
    private String messageContents;

    ChatPacket(String sender, String messageContents) {
        this(sender, GENERAL_STRING, messageContents);
    }

    ChatPacket(String sender, String target, String messageContents) {
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
