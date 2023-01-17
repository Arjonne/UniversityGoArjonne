package com.nedap.go.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private Player playerBlack;
    private Player playerWhite;
    private Board board;
    private Player currentPlayer;

    /**
     * Before tests can be done, the game should be made using the constructor of the Game class.
     */
    @BeforeEach
    public void setUp() {
        game = new Game(playerBlack, playerWhite, board);
    }

    /**
     * Test to check whether switchTurn() does correctly switch the turn from player with Black to player with White and other way around.
     */
    @Test
    public void testSwitchTurn() {
        assertEquals(playerBlack, currentPlayer);
        game.switchTurn();
        assertEquals(playerWhite, currentPlayer);
        game.switchTurn();
        assertEquals(playerBlack, currentPlayer);
    }



}
