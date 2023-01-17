package com.nedap.go.game;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private Board board;

    /**
     * Before tests can be done, the board should be made using the constructor of the Board class.
     */
    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    /**
     * Test to check if isValidPosition() really finds the actual positions on the board (rows and columns ranging between 0 and SIZE).
     */
    @Test
    public void testIsValidPosition() {
        assertFalse(board.isValidPosition(-1, 0));
        assertTrue(board.isValidPosition(0, 0));
        assertTrue(board.isValidPosition(3, Board.SIZE - 2));
        assertFalse(board.isValidPosition(4, Board.SIZE));
    }

    /**
     * Test to check whether a stone can be placed on a position using placeStone() and to check if the correct stone is returned when using getStone().
     */
    @Test
    public void testPlaceAndGetStone() {
        board.placeStone(0, 0, Stone.BLACK);
        assertEquals(Stone.BLACK, board.getStone(0, 0));
        assertEquals(Stone.EMPTY, board.getStone(0, 1));
        // no checks for setting a stone on / getting a stone from a position out of boundaries of the board OR setting
        // a stone on a non-empty position as this has been checked by other tests before.
    }

    /**
     * Test to check whether copyBoard() does correctly make a copy of the current state of the board.
     */
    @Test
    public void testCopyBoard() {
        // First, place stones on the board. After copying the board, check whether these placed stones are on the copy
        // of the board as well.
        board.placeStone(1, 3, Stone.WHITE);
        board.placeStone(1, 4, Stone.WHITE);
        Board copyBoard = board.copyBoard();
        assertEquals(Stone.WHITE, copyBoard.getStone(1, 3));
        assertEquals(Stone.WHITE, copyBoard.getStone(1, 4));
        // Another test, to confirm that after placing the stone on the board, it is not directly visible on the copy
        // of the board too. It is, however, after copying the board.
        board.placeStone(1, 5, Stone.WHITE);
        assertNotEquals(Stone.WHITE, copyBoard.getStone(1, 5));
        copyBoard = board.copyBoard();
        assertEquals(Stone.WHITE, copyBoard.getStone(1, 5));
    }

    /**
     * Test to check whether isEmptyPosition() returns true if no stone is placed on that position, and false if there is a stone on that position.
     */
    @Test
    public void testIsEmptyPosition() {
        assertTrue(board.isEmptyPosition(2, 2));
        board.placeStone(2, 2, Stone.BLACK);
        assertFalse(board.isEmptyPosition(2, 2));
        assertFalse(board.isEmptyPosition(-1,2));
    }
}
