package GameClient.Forms;

import Comunication.ChatUtils.TCPChat.ChatPacket;

public interface ChatUI {
    void OnNewMessage(ChatPacket cp);

    void OnNewClient(String clientName);
}
