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
        assertTrue(board.isValidPosition(3, Board.SIZE - 1));
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
        board.placeStone(0, 1, Stone.WHITE);
        assertEquals(Stone.WHITE, board.getStone(0, 1));
        // no checks for placing a stone on a position out of boundaries of the board OR placing a BLACK/WHITE stone on
        // a non-empty position as this has been checked by other tests before.
    }

    /**
     * Test to check whether a stone can be removed using removeStone() and check if getStone() does correctly return EMPTY after removal.
     */
    @Test
    public void testRemoveAndGetStone() {
        board.placeStone(1,1,Stone.BLACK);
        assertEquals(Stone.BLACK, board.getStone(1,1));
        board.removeStone(1,1);
        assertEquals(Stone.EMPTY, board.getStone(1,1));
    }

    /**
     * Test to check whether copyBoard() does correctly make a copy of the current state of the board.
     */
    @Test
    public void testCopyBoard() {
        // First, place stones on the board. After copying the board, check whether these placed stones are on the copy
        // of the board as well.
        board.placeStone(2, 2, Stone.WHITE);
        board.placeStone(2, 3, Stone.WHITE);
        Board copyBoard = board.copyBoard();
        assertEquals(Stone.WHITE, copyBoard.getStone(2, 2));
        assertEquals(Stone.WHITE, copyBoard.getStone(2, 3));
        // Another test, to confirm that after placing the stone on the board, it is not directly visible on the copy
        // of the board too. However, it is after copying the board.
        board.placeStone(2, 4, Stone.WHITE);
        assertNotEquals(Stone.WHITE, copyBoard.getStone(2, 4));
        copyBoard = board.copyBoard();
        assertEquals(Stone.WHITE, copyBoard.getStone(2, 4));
    }

    /**
     * Test to check whether isEmptyPosition() returns true if no stone is placed on that position, and false if there is a stone on that position.
     */
    @Test
    public void testIsEmptyPosition() {
        assertTrue(board.isEmptyPosition(3, 3));
        board.placeStone(3, 3, Stone.BLACK);
        assertFalse(board.isEmptyPosition(3, 3));
        // no checks for checking the position that is out of boundaries of the board as this has been checked by
        // other tests before.
    }

    @Test
    public void testIsFull() {
        assertFalse(board.isFull());
        // place black stones on all positions of the board:
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                board.placeStone(row, column, Stone.BLACK);
            }
        }
        assertTrue(board.isFull());
    }
}