package com.nedap.go.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private Player playerBlack;
    private Player playerWhite;
    private Board board;
//    Map<Position, Stone> movesMap = new HashMap<>();


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
        game.doMove(0,0);
        assertEquals(Stone.BLACK, board.getStone(0,0));
        assertEquals(Stone.EMPTY, board.getStone(1,0));
        game.doMove(1,0);
        assertEquals(Stone.WHITE, board.getStone(1,0));
        game.removeStone(0,0);
        assertEquals(game.getBoard().getStone(0,0),Stone.EMPTY);
        game.doMove(0,0);
        // Black stone cannot be placed on this position again after removing this stone (due to KO rule).
        assertEquals(game.getBoard().getStone(0,0),Stone.EMPTY);
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
        game.doMove(1,0);
        assertEquals(0, game.getPassCount());
        game.pass();
        assertEquals(1, game.getPassCount());
        assertFalse(game.isGameOver());
        game.pass();
        assertEquals(2, game.getPassCount());
        assertTrue(game.isGameOver());
    }

    // does work, but not sure whether it is necessary to do...?
//    /**
//     * Test whether createMovesMap() does correctly create a map with all placed stones on the field. As the map is just
//     * created, all fields are empty.
//     */
//    @Test
//    public void testCreateMovesMap() {
//        game.createMovesMap();
//        // as all possible positions of the board are represented in this map, the size of this map must be board.SIZE*board.SIZE.
//        assertEquals(game.movesMap.size(), board.SIZE * board.SIZE);
//        // check whether all Stones for each position are set on EMPTY by default
//        for (Position positions : game.movesMap.keySet()) {
//            assertTrue(game.movesMap.containsValue(Stone.EMPTY));
//        }
//    }
//
//    /**
//     * Test whether updateMovesMap() does correctly update the map after placing or removing a stone.
//     */
//    @Test
//    public void testUpdateMovesMap() {
//        game.createMovesMap();
//        // first, confirm that position (row 0, column 0) is empty:
//        for (Position positions : game.movesMap.keySet()) {
//            if (positions.getRow() == 0 && positions.getColumn() == 0) {
//                assertTrue(game.movesMap.containsValue(Stone.EMPTY));
//            }
//        }
//        // current player, which is Black by default, plays a move on position row 0, column 0:
//        game.doMove(0, 0);
//        // find this position in the map again and confirm that the value (=stone) is BLACK instead of EMPTY now.
//        for (Position positions : game.movesMap.keySet()) {
//            if (positions.getRow() == 0 && positions.getColumn() == 0) {
//                assertTrue(game.movesMap.containsValue(Stone.BLACK));
//            }
//            // try some random positions to see that those are still EMPTY:
//            if (positions.getRow() == 1 && positions.getColumn() == 0) {
//                assertTrue(game.movesMap.containsValue(Stone.EMPTY));
//            }
//            if (positions.getRow() == (board.SIZE - 1) && positions.getColumn() == (board.SIZE - 1)) {
//                assertTrue(game.movesMap.containsValue(Stone.EMPTY));
//            }
//        }
//    }

    @Test
    public void testIsValidMove() {
        //TODO implement + javaDoc

    }

}



