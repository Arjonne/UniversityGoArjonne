package com.nedap.go.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private Player playerBlack;
    private Player playerWhite;
    private Board board;


    /**
     * Before tests can be done, the game should be made using the constructor of the Game class.
     */
    @BeforeEach
    public void setUp() {
        playerBlack = new Player("Black", Stone.BLACK);
        playerWhite = new Player("White", Stone.WHITE);
        board = new Board();
        game = new Game(playerBlack, playerWhite, board);
    }

    /**
     * Test to check whether switchTurn() does correctly switch the turn from player with Black to player with White and other way around.
     */
    @Test
    public void testSwitchTurn() {
        assertEquals(playerBlack, game.getCurrentPlayer());
        game.switchTurn();
        assertEquals(playerWhite, game.getCurrentPlayer());
        game.switchTurn();
        assertEquals(playerBlack, game.getCurrentPlayer());
    }

    /**
     * Test whether getStone() does correctly return the stone that player uses.
     */
    @Test
    public void testGetStone() {
        assertEquals(game.getStone(playerBlack), Stone.BLACK);
        assertEquals(game.getStone(playerWhite), Stone.WHITE);
    }

    /**
     * Test whether getStoneOpponent() does correctly return the stone the opponent of that player uses.
     */
    @Test
    public void testGetStoneOpponent() {
        assertEquals(game.getStoneOpponent(playerBlack), Stone.WHITE);
        assertEquals(game.getStoneOpponent(playerWhite), Stone.BLACK);
    }

    /**
     * Test to check whether a stone can be placed on a position using doMove() and to check if the correct stone is
     * returned when using getStone().
     */
    @Test
    public void testDoMoveAndRemoveStone() {
        game.doMove(0, 0);
        assertEquals(Stone.BLACK, board.getStone(0, 0));
        assertEquals(Stone.EMPTY, board.getStone(1, 0));
        game.doMove(1, 0);
        assertEquals(Stone.WHITE, board.getStone(1, 0));
        game.removeStone(0, 0);
        assertEquals(game.getBoard().getStone(0, 0), Stone.EMPTY);
        game.doMove(0, 0);
        // Black stone cannot be placed on this position again after removing this stone (due to KO rule).
        assertEquals(game.getBoard().getStone(0, 0), Stone.EMPTY);
        // no checks for placing a stone on a position out of boundaries of the board OR placing a BLACK/WHITE stone on
        // a non-empty position OR switchTurn() which is implemented in doMove as this has been checked by other tests before.
    }

    /**
     * Test to pass moves, the counter of passes and whether the game is over after two passes.
     */
    @Test
    public void testPassAndPassCountAndGameOver() {
        game.pass();
        assertEquals(1, game.getPassCount());
        game.doMove(1, 0);
        assertEquals(0, game.getPassCount());
        game.pass();
        assertEquals(1, game.getPassCount());
        assertFalse(game.isGameOver());
        game.pass();
        assertEquals(2, game.getPassCount());
        assertTrue(game.isGameOver());
    }

    /**
     * GameOver was tested with two consecutive passes before; in this test also the option of game over when a board
     * is full is tested.
     */
    @Test
    public void testGameOver() {
        assertFalse(game.isGameOver());
        // place black stones on all positions of the board:
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                board.placeStone(row, column, Stone.BLACK);
            }
        }
        assertTrue(board.isFull());
        assertTrue(game.isGameOver());
    }

    /**
     * Test whether getNeighbourPositions() does get the correct number of neighbours in several positions.
     */
    @Test
    public void testGetNeighbourPositions() {
        // at position (row 1, column 1), the number of neighbours should be 4
        game.getNeighbourPositions(1, 1);
        assertEquals(4, game.neighbourPositions.size());
        // at position (row 0, column 0), the number of neighbours should be 2 as this position is located in the left upper corner
        game.getNeighbourPositions(0, 0);
        assertEquals(2, game.neighbourPositions.size());
        // at position (row SIZE-1, column 1), the number of neighbours should be 3 as this position is located on the right edge of the board
        game.getNeighbourPositions((Board.SIZE - 1), 1);
        assertEquals(3, game.neighbourPositions.size());
        // at position (row 1, column SIZE-1), the number of neighbours should be 3 as this position is located on the lower edge of the board
        game.getNeighbourPositions(1, (Board.SIZE - 1));
        assertEquals(3, game.neighbourPositions.size());
    }

    /**
     * Test whether getNeighbourStones() does get the correct stones that are neighbours in several positions.
     */
    @Test
    public void testGetNeighbourStones() {
        //TODO
    }
}



