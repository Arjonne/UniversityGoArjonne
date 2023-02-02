package com.nedap.go.game;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents the player of the GO game. Each game has two players; either two human players, one human player and one
 * computer player or two computer players. Each This player has a name and has a stone (either black or white), and
 * also a methods to determine a next move.
 */
public class Player {
    private final String username;
    private final Stone stone;
    private int row;
    private int column;

    /**
     * Constructor to create a new player. Each player has a name and uses either a black or a white stone.
     *
     * @param username represents the name of the player;
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
     * @param game is the game this player is playing;
     * @return the move this player can make. If no valid move is available, return null and pass.
     */
    public Position determineMove(Game game) {
        System.out.println("Do you want to make a move or pass? Type either MOVE or PASS:");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().toUpperCase();
        if (input.equals("PASS")) {
            return null;
        } else if (input.equals("MOVE")) {
            System.out.println("On what intersection do you want to place your stone?");
            boolean correctInput = false;
            while (!correctInput) {
                System.out.print("First, enter the row number (ranging between 1 and " + Board.SIZE + ": ");
                try {
                    row = (scanner.nextInt() - 1);
                } catch (InputMismatchException e) {
                    System.out.println("Input must be a number between 1 and " + Board.SIZE + "!");
                }
                System.out.print("Now, enter the column number (ranging between 1 and " + (Board.SIZE) + ": ");
                try {
                    column = (scanner.nextInt() - 1);
                    correctInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Input must be a number between 1 and " + Board.SIZE + "!");
                }
            }
        }
        return new Position(row, column);
    }
}
