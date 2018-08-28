package Comunication.ChatUtils.TCPChat;

public enum GameCommand {
    UPDATED, //Send new game view
    PLAYER_LEFT, //TODO
    STARTED, //Send game view
    STOPPED, //Send nothing, the client closes the form
    ENDED,   //TODO
    WINNER_DECIDED, //I think this will be removed
    START_GAME, //Client to server
    MAKE_PLAY, //Client to server, send a move
}
