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
     * TODO
     */
    @Test
    public void testIsPosition() {
        assertFalse(board.isPosition(-1,0));
        assertTrue(board.isPosition(0,0));
        assertTrue(board.isPosition(3, Board.DIMENSION - 2));
        assertFalse(board.isPosition(4, Board.DIMENSION));
    }

    /**
     * TODO
     */
    @Test
    public void testSetAndGetPosition() {
        board.setPosition(3,3,Stone.BLACK);
        assertEquals(Stone.BLACK, board.getPosition(3,3));
        assertEquals(Stone.EMPTY, board.getPosition(2,2));
    }

    /**
     * TODO
     */
    @Test
    public void testDeepCopy() {
        board.setPosition(2,3, Stone.WHITE);
        board.setPosition(2,4,Stone.WHITE);
        board.setPosition(2,5,Stone.WHITE);
        assertEquals(Stone.WHITE, board.getPosition(2,3));
        assertEquals(Stone.WHITE, board.getPosition(2,4));
        assertEquals(Stone.WHITE, board.getPosition(2,5));
        Board copyBoard = board.deepCopy();
        assertEquals(Stone.WHITE, copyBoard.getPosition(2,3));
        assertEquals(Stone.WHITE, copyBoard.getPosition(2,4));
        assertEquals(Stone.WHITE, copyBoard.getPosition(2,5));
        board.setPosition(2,6,Stone.WHITE);
        assertNotEquals(Stone.WHITE, copyBoard.getPosition(2,6));
        copyBoard = board.deepCopy();
        assertEquals(Stone.WHITE, copyBoard.getPosition(2,6));
    }

    /**
     * TODO
     */
    @Test
    public void testIsEmptyPosition() {
//TODO
    }

    /**
     * TODO
     */
    @Test
    public void testReset() {
//TODO
    }



}
