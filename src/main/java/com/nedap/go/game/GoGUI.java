package com.nedap.go.game;

import com.nedap.go.gui.GoGuiIntegrator;

public class GoGUI {
    private final GoGuiIntegrator gogui;

    /**
     * Creates the GUI of the go board.
     * @param boardSize is the predefined size of the board
     */
    public GoGUI(int boardSize) {
        gogui = new GoGuiIntegrator(true, true, boardSize);
        gogui.startGUI();
        gogui.setBoardSize(boardSize);
    }

    /**
     * Places a stone on the GUI board.
     *
     * @param row is the row on which the stone is placed
     * @param column is the column on which the stone is placed
     * @param stone is the stone type that is placed
     */
    public void placeStone(int row, int column, Stone stone) {
        if (stone == Stone.BLACK) {
            gogui.addStone(row, column, false);
        } else {
            gogui.addStone(row, column, true);
        }
    }

    /**
     * Removes a stone from the GUI board.
     *
     * @param row is the row on which a stone is removed
     * @param column is the column on which a stone is removed
     */
    public void removeStone(int row, int column) {
        gogui.removeStone(row, column);
    }

    /**
     * Clears the GUI board.
     */
    public void clearBoard() {
        gogui.clearBoard();
    }
}
