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
        GoGUI goGUI = new GoGUI(Board.SIZE);
        game = new Game(playerBlack, playerWhite, board, goGUI);
    }

    /**
     * Test to check whether switchTurn() does correctly switch the turn from player with Black to player with White
     * and other way around.
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

    @Test
    public void testGetRandomValidMove() {
        game.createEmptyPositionSet();
        // on an empty board, there must be a random valid position to make a move:
        Position randomValidPosition = game.findRandomValidPosition();
        assertFalse(randomValidPosition == null);

        // on a full board, there is no random valid position available and therefore the outcome should be equal to
        // null:
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                board.placeStone(row, column, Stone.BLACK);
            }
        }
        Position randomValidPosition1 = game.findRandomValidPosition();
        assertTrue(randomValidPosition1 == null);
    }

    /**
     * Test to check a move on being a valid move (placed within the boundaries of the board, on an empty position
     * and not resulting in ko rule violation).
     */
    @Test
    public void testValidMove() {
        // stone placed outside board boundaries:
        assertFalse(game.isValidMove(1, Board.SIZE));
        // after placing a move on position (1,1), no second stone can be placed here:
        game.doMove(1, 1);
        assertFalse(game.isValidMove(1, 1));

        // after removing the stone on position (1,1) and switching the turn (as then again BLACK can make the move),
        // the ko rule is violated when again a stone on position (1,1) will be placed.
        game.removeStone(1, 1);
        game.switchTurn();
        assertFalse(game.isValidMove(1, 1));
    }

    /**
     * Test to check whether a random valid position for a next move can be found.
     */
    @Test
    public void testFindRandomValidPositions() {
        // on an empty board, all positions should be valid positions, so both the size of the list of empty positions
        // and the size of the list of valid positions must be (board.SIZE * board.SIZE)
        game.createEmptyPositionSet();
        assertTrue((game.getEmptyPositions().size() == (Board.SIZE * Board.SIZE)));
        assertEquals((game.getListOfValidPositions()).size(), (Board.SIZE * Board.SIZE));

        // after placing a stone on the board, the size of the list of empty positions and the size of list of valid
        // positions must be ((board.SIZE * board.SIZE) -1)
        game.doMove(2, 2);
        assertTrue((game.getEmptyPositions().size() == ((Board.SIZE * Board.SIZE) - 1)));
        assertEquals((game.getListOfValidPositions()).size(),((Board.SIZE * Board.SIZE) - 1));

        // after removing the previously placed stone from the board, the list of empty positions and the size of the
        // list of valid positions mus be (board.SIZE * board.SIZE) again.
        game.removeStone(2,2);
        // (manually adding this position to the set of empty positions again, as removal only occurs after capture in
        // the game class!)
        game.getEmptyPositions().add(new Position (2,2));
        assertTrue((game.getEmptyPositions().size() == (Board.SIZE * Board.SIZE)));
        assertEquals((game.getListOfValidPositions()).size(), (Board.SIZE * Board.SIZE));

        // after switching the turn, the list of empty positions should still be (board.SIZE * board.SIZE), but the
        // size of the list of valid positions should be ((board.SIZE * board.SIZE) -1) again, as doing the move (2,2)
        // would result in ko rule violation!
        game.switchTurn();
        assertTrue((game.getEmptyPositions().size() == (Board.SIZE * Board.SIZE)));
        assertEquals((game.getListOfValidPositions()).size(), ((Board.SIZE * Board.SIZE) - 1));
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
        // at position (row 1, column 1), the number of neighbours should be 4:
        game.getNeighbourPositions(1, 1);
        assertEquals(4, game.getNeighbourPositions(1, 1).size());
        // at position (row 0, column 0), the number of neighbours should be 2 as this position is located in the left
        // upper corner:
        game.getNeighbourPositions(0, 0);
        assertEquals(2, game.getNeighbourPositions(0, 0).size());
        // at position (row SIZE-1, column 1), the number of neighbours should be 3 as this position is located on the
        // right edge of the board:
        game.getNeighbourPositions((Board.SIZE - 1), 1);
        assertEquals(3, game.getNeighbourPositions((Board.SIZE - 1), 1).size());
        // at position (row 1, column SIZE-1), the number of neighbours should be 3 as this position is located on the
        // lower edge of the board:
        game.getNeighbourPositions(1, (Board.SIZE - 1));
        assertEquals(3, game.getNeighbourPositions(1, (Board.SIZE - 1)).size());
    }

    /**
     * Test whether getNeighbourStones() does get the correct type of stones that are neighbours in several positions.
     */
    @Test
    public void testGetNeighbourStones() {
        // after placing the first stone (which is BLACK), all neighbours should be EMPTY:
        board.placeStone(1, 1, Stone.BLACK);
        game.getNeighbourStones(game.getNeighbourPositions(1, 1));
        assertTrue(game.getNeighbourStones(game.getNeighbourPositions(1, 1)).contains(Stone.EMPTY));
        assertFalse(game.getNeighbourStones(game.getNeighbourPositions(1, 1)).contains(Stone.WHITE));
        assertFalse(game.getNeighbourStones(game.getNeighbourPositions(1, 1)).contains(Stone.BLACK));
        assertTrue(game.hasNeighbourThatEnsuresNoCapture(game.getNeighbourStones(game.getNeighbourPositions(1, 1)), Stone.EMPTY));
        assertFalse(game.hasNeighbourOfSameColor(game.getNeighbourStones(game.getNeighbourPositions(1, 1)), Stone.BLACK));

        // after placing the second stone (which is WHITE) right next to the first stone, the placed stone should have
        // a BLACK stone as neighbour:
        board.placeStone(1, 2, Stone.WHITE);
        game.getNeighbourStones(game.getNeighbourPositions(1, 2));
        assertTrue(game.getNeighbourStones(game.getNeighbourPositions(1, 2)).contains(Stone.EMPTY));
        assertTrue(game.getNeighbourStones(game.getNeighbourPositions(1, 2)).contains(Stone.BLACK));
        assertFalse(game.getNeighbourStones(game.getNeighbourPositions(1, 2)).contains(Stone.WHITE));
        assertTrue(game.hasNeighbourThatEnsuresNoCapture(game.getNeighbourStones(game.getNeighbourPositions(1, 2)), Stone.EMPTY));
        assertFalse(game.hasNeighbourOfSameColor(game.getNeighbourStones(game.getNeighbourPositions(1, 2)), Stone.WHITE));
    }

    /**
     * Test whether several ways of capturing stones and self-capturing correctly results in removing stones, and also
     * whether the final score is calculated correctly.
     */
    @Test
    public void testCapturesAndFinalScore() {
        game.createEmptyPositionSet();

        // test capturing a group of stones:
        game.doMove(0, 0); // BLACK
        game.doMove(0, 2); // WHITE
        game.doMove(0, 1); // BLACK
        game.doMove(1, 1); // WHITE
        game.doMove(1, 0); // BLACK
        // next move captures 3 black stones, so positions (0,0), (0,1) and (1,0) should be empty afterwards:
        game.doMove(2, 0); // WHITE
        assertTrue(board.isEmptyPosition(0, 0));
        assertTrue(board.isEmptyPosition(1, 0));
        assertTrue(board.isEmptyPosition(0, 1));

        // test suicide move:
        game.doMove(6, 8); // BLACK
        game.doMove(5, 8); // WHITE
        game.doMove(4, 0); // BLACK
        game.doMove(5, 7); // WHITE
        game.doMove(5, 0); // BLACK
        game.doMove(6, 6); // WHITE
        game.doMove(3, 1); // BLACK
        game.doMove(7, 8); // WHITE
        game.doMove(4, 2); // BLACK
        game.doMove(7, 7); // WHITE
        // next move is suicide move of black, so positions (6,7) and (6,8) should be empty afterwards:
        game.doMove(6, 7); // BLACK
        assertTrue(board.isEmptyPosition(6, 7));
        assertTrue(board.isEmptyPosition(6, 8));

        // test whether suicide move resulting in capture removes the correct stone:
        game.doMove(4, 1); // WHITE
        game.doMove(5, 2); // BLACK
        game.doMove(5, 1); // WHITE
        game.doMove(2, 4); // BLACK
        game.doMove(6, 0); // WHITE
        game.doMove(3, 5); // BLACK
        game.doMove(6, 2); // WHITE
        game.doMove(0, 8); // BLACK
        game.doMove(7, 1); // WHITE
        // next move is suicide move which captures white stones on positions (4,1) and (5,1), so these should be
        // empty afterwards, and black stone on (6,1) should still be there:
        game.doMove(6, 1); // BLACK
        assertTrue(board.isEmptyPosition(4, 1));
        assertTrue(board.isEmptyPosition(5, 1));
        assertEquals(board.getStone(6, 1), Stone.BLACK);

        // test whether placing a stone which results in capturing two groups correctly removes both groups:
        game.doMove(1, 4); // WHITE
        game.doMove(8, 2); // BLACK
        game.doMove(2, 3); // WHITE
        game.doMove(2, 8); // BLACK
        game.doMove(3, 4); // WHITE
        game.doMove(6, 5); // BLACK
        game.doMove(4, 5); // WHITE
        game.doMove(1, 7); // BLACK
        game.doMove(3, 6); // WHITE
        game.doMove(0, 6); // BLACK
        // next move captures both the BLACK stone on position (2,4) and the BLACK stone on position (3,5), which
        // are separate groups. Both positions should be empty afterwards:
        game.doMove(2, 5); // WHITE
        assertTrue(board.isValidPosition(2, 4));
        assertTrue(board.isValidPosition(3, 5));

        board.printBoard();
        // based on all placed stones as above, the score can be calculated. The first part of the final score is
        // calculated as the number of stones that is placed on the board per player:
        int blackPlacedStones = game.getNumberOfStones(playerBlack, board.toString());
        int whitePlacedStones = game.getNumberOfStones(playerWhite, board.toString());
        assertEquals(blackPlacedStones, 12);
        assertEquals(whitePlacedStones, 17);

        // The second part of the final score is calculated as the number of captured positions on the board per player:
        int blackCapturedStones = game.getFinalCapturedPositions(playerBlack);
        int whiteCapturedStones = game.getFinalCapturedPositions(playerWhite);
        assertEquals(blackCapturedStones, 4);
        assertEquals(whiteCapturedStones, 7);

        // The final score is the sum of the both numbers as calculated above:
        int finalScoreBlack = game.finalScore(playerBlack);
        int finalScoreWhite = game.finalScore(playerWhite);
        assertEquals(finalScoreBlack, 16);
        assertEquals(finalScoreWhite, 24);

        // In this case, playerWhite should be the winner of the game:
        assertEquals(game.getWinner(), playerWhite.getUsername());
        assertNotEquals(game.getWinner(), playerBlack.getUsername());
        assertNotEquals(game.getWinner(), null);
    }
}



