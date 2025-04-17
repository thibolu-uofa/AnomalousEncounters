package model;

import static model.Utils.getRepeatingPattern;

import java.util.ArrayList;

public class AttackPattern {
    public enum AttackType {
        DIAGONAL,
        STRAIGHT,
        STAR,
        HOURGLASS,
        BUTTERFLY,
    }

    private final AttackType attackType;
    private final int MAX_ROWS = 6;
    private final int MAX_COLS = 8;

    public AttackPattern(AttackType attackType) {
        this.attackType = attackType;
    }

    public ArrayList<int[]> getAttackPattern(int[] origin_pos, int distance) {
        switch (attackType) {
            case DIAGONAL:
                return getDiagonalPattern(origin_pos, distance);
            case STRAIGHT:
                return getStraightPattern(origin_pos, distance);
            case STAR:
                return getStarPattern(origin_pos, distance);
            case HOURGLASS:
                return getHourglassPattern(origin_pos, distance);
            case BUTTERFLY:
                return getButterflyPattern(origin_pos, distance);
            default:
                return new ArrayList<int[]>();
        }
    }

    private ArrayList<int[]> getDiagonalPattern(int[] origin_pos, int distance) {
        // Diagonal movement vectors: up-left, up-right, down-left, down-right
        int[][] positionVectors = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        return getRepeatingPattern(origin_pos, distance, positionVectors, MAX_ROWS, MAX_COLS);
    }

    private ArrayList<int[]> getStraightPattern(int[] origin_pos, int distance) {
        // Straight movement vectors: up, down, right, left
        int[][] positionVectors = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        return getRepeatingPattern(origin_pos, distance, positionVectors, MAX_ROWS, MAX_COLS);
    }

    private ArrayList<int[]> getStarPattern(int[] origin_pos, int distance) {
        // Combines diagonal and straight directions for a star pattern
        int[][] positionVectors = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}, // diagonals
                {-1, 0}, {1, 0}, {0, 1}, {0, -1}    // straights
        };
        return getRepeatingPattern(origin_pos, distance, positionVectors, MAX_ROWS, MAX_COLS);
    }

    private ArrayList<int[]> getHourglassPattern(int[] origin_pos, int distance) {
        // Hourglass shape uses cross diagonals and some straight directions
        int[][] positionVectors = {
                {-1, 1}, {1, -1}, // top-right to bottom-left diagonal
                {0, 1}, {0, -1},  // horizontal line (right/left)
                {1, 1}, {-1, -1}  // bottom-right and top-left
        };
        return getRepeatingPattern(origin_pos, distance, positionVectors, MAX_ROWS, MAX_COLS);
    }

    private ArrayList<int[]> getButterflyPattern(int[] origin_pos, int distance) {
        // Butterfly shape includes:
        // - Vertical straight lines: up and down ({-1, 0}, {1, 0})
        // - Diagonals around the vertical axis: top-left, top-right, bottom-left, bottom-right
        //   ({-1, -1}, {-1, 1}, {1, -1}, {1, 1})
        int[][] positionVectors = {
                {-1, 0}, {1, 0},       // up, down
                {-1, -1}, {-1, 1},     // top-left, top-right
                {1, -1}, {1, 1}        // bottom-left, bottom-right
        };

        // Passes the origin position, distance, and direction vectors to the utility function
        // along with the map bounds to calculate all valid affected positions
        return getRepeatingPattern(origin_pos, distance, positionVectors, MAX_ROWS, MAX_COLS);
    }
}