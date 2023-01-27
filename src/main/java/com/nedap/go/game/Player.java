package com.nedap.go.game;

import javafx.geometry.Pos;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents the player of the GO game.
 * Each game has two players; either two human players, one human player and one computer player or two computer players.
 * Each player has a name and has a stone (either black or white).
 */

public class Player {
    private String username;
    private Stone stone;

    /**
     * Constructor to create a new player. Each player has a name and uses either a black or a white stone.
     *
     * @param username represents the name of the player
     * @param stone    represents the stone this player uses.
     */
    public Player(String username, Stone stone) {
        this.username = username;
        this.stone = stone;
    }

    /**
     * Get the name of this player.
     *
     * @return the name of the player.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the stone this player uses.
     *
     * @return the stone this player uses.
     */
    public Stone getStone() {
        return stone;
    }

    /**
     * Determines the next move this player will make.
     *
     * @param game is the game this player is playing
     * @return the move this player can make. If no valid move is available, return null and pass
     */
    public Position determineMove(Game game) {
        System.out.println("Do you want to make a move or pass? Type either MOVE or PASS:");
        Scanner input = new Scanner(System.in);
        int row = -1;
        int column = -1;
        if (input.nextLine().toUpperCase().equals("PASS")) {
            game.pass();
        } else if (input.nextLine().toUpperCase().equals("MOVE")) {
            System.out.println("On what intersection do you want to place your stone?");
            boolean correctInput = false;
            while (!correctInput) {
                System.out.print("First, enter the row number (ranging between 1 and " + Board.SIZE + ": ");
                try {
                    row = (input.nextInt() - 1);
                } catch (InputMismatchException e) {
                    System.out.println("Input must be a number between 1 and " + Board.SIZE + "!");
                }
                System.out.print("Now, enter the column number (ranging between 1 and " + (Board.SIZE) + ": ");
                try {
                    column = (input.nextInt() - 1);
                    correctInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Input must be a number between 1 and " + Board.SIZE + "!");
                }
            }
        }
        return new Position(row, column);
    }
}
