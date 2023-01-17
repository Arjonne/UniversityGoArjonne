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
     * Test to check if isPosition() really finds the actual positions on the board (rows and columns ranging between 0 and DIMENSION).
     */
    @Test
    public void testIsPosition() {
        assertFalse(board.isPosition(-1, 0));
        assertTrue(board.isPosition(0, 0));
        assertTrue(board.isPosition(3, Board.DIMENSION - 2));
        assertFalse(board.isPosition(4, Board.DIMENSION));
    }

    /**
     * Test to check whether a stone can be placed on a position using setPosition() and to check if the correct stone is returned when using getPosition().
     */
    @Test
    public void testSetAndGetPosition() {
        board.setPosition(0, 0, Stone.BLACK);
        assertEquals(Stone.BLACK, board.getPosition(0, 0));
        assertEquals(Stone.EMPTY, board.getPosition(0, 1));
    }

    /**
     * Test to check whether deepCopy() does correctly make a copy of the current state of the board.
     */
    @Test
    public void testDeepCopy() {
        board.setPosition(1, 3, Stone.WHITE);
        board.setPosition(1, 4, Stone.WHITE);
        board.setPosition(1, 5, Stone.WHITE);
        assertEquals(Stone.WHITE, board.getPosition(1, 3));
        assertEquals(Stone.WHITE, board.getPosition(1, 4));
        assertEquals(Stone.WHITE, board.getPosition(1, 5));
        Board copyBoard = board.deepCopy();
        assertEquals(Stone.WHITE, copyBoard.getPosition(1, 3));
        assertEquals(Stone.WHITE, copyBoard.getPosition(1, 4));
        assertEquals(Stone.WHITE, copyBoard.getPosition(1, 5));
        board.setPosition(1, 6, Stone.WHITE);
        assertNotEquals(Stone.WHITE, copyBoard.getPosition(1, 6));
        copyBoard = board.deepCopy();
        assertEquals(Stone.WHITE, copyBoard.getPosition(1, 6));
    }

    /**
     * Test to check whether isEmptyPosition() does return true if no stone is placed on that position, and false if there is a stone on that position.
     */
    @Test
    public void testIsEmptyPosition() {
        assertTrue(board.isEmptyPosition(2, 2));
        board.setPosition(2, 2, Stone.BLACK);
        assertFalse(board.isEmptyPosition(2, 2));
    }
}
