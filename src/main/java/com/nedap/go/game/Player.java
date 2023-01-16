package com.nedap.go.game;

/**
 * Represents the player of the GO game.
 * Each game has two players; either two human players, one human player and one computer player or two computer players.
 * Each player has a name and has a stone (either black or white).
 */

public class Player {
    private String username;
    private Stone stone;

    /**
     * Constructor to create a new player. Each player has a name and uses either a black or a white stone.
     * @param username represents the name of the player
     * @param stone represents the stone this player uses.
     */
    public Player(String username, Stone stone) {
        this.username = username;
        this.stone = stone;
    }

    /**
     * Get the name of this player.
     * @return the name of the player.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the stone this player uses.
     * @return the stone this player uses.
     */
    public Stone getStone() {
        return stone;
    }
}
