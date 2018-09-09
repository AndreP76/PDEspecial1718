package Comunication.ChatUtils.DataPackets;

public enum GameCommand {
    UPDATED, //Send new game view
    PLAYER_LEFT,
    STARTED, //Send game view
    STOPPED, //Send nothing, the client closes the form
    WINNER_DECIDED, //I think this will be removed
    START_GAME, //Client to server
    MAKE_PLAY, PLAYER_LEAVING, //Client to server, send a move
}
