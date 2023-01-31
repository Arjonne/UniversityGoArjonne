package com.nedap.go.game;

/**
 * Represents the computer player of the GO game. This player has a name and has a stone (either black or white),
 * and also a methods to determine a next move.
 */

public class ComputerPlayer extends Player {

    /**
     * Constructor to create a new computer player. Each player has a name and uses either a black or a white stone.
     *
     * @param username represents the name of the player;
     * @param stone    represents the stone this player uses.
     */
    public ComputerPlayer(String username, Stone stone) {
        super(username, stone);
    }

    /**
     * Determines the next move this player will make.
     *
     * @param game is the game this player is playing;
     * @return the move this player can make. If no valid move is available, return null.
     */
    @Override
    public Position determineMove(Game game) {
        return game.findRandomValidPosition();
    }
}
