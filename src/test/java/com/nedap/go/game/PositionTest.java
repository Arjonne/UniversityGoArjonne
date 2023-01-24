package com.nedap.go.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    /**
     * Test whether equals() now correctly compares the rows and columns of two Positions. As the hashCode is based on
     * the row/column combination of a Position, these need to be the same too.
     */
    @Test
    void testEquals() {
        Position one = new Position(1, 0);
        Position two = new Position(1, 0);
        assertEquals(one.hashCode(), two.hashCode());
        assertEquals(one, two);
    }
}