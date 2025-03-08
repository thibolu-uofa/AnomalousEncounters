package model;
import java.util.ArrayList;

public class AttackPattern {
    private enum AttackType {
        DIAGONAL,
        STAIGHT,
        STAR
    }

    private AttackType attackType;
    private final int maxRows = 5;
    private final int maxCols = 5;

    AttackPattern(AttackType attackType) {
        this.attackType = attackType;
    }
    
    public ArrayList<int[]> getAttackPattern(int[] origin_pos, int distance) {
        int[][] attackPattern;
        switch (attackType) {
            case DIAGONAL:
                return getDiagonalPattern(origin_pos, distance);
            case STAIGHT:
                return getStraightPattern(origin_pos, distance);
            case STAR:
                return getStarPattern(origin_pos);
            default:
                return new ArrayList<int[]>();
        }
    }

    //TODO: Prevent tiles from going out of bounds.
    private ArrayList<int[]> getDiagonalPattern(int[] origin_pos, int distance) {
        ArrayList<int[]> attackPattern = new ArrayList<int[]>();

        int[] currentTile = origin_pos;
        int[][] positionVectors = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] vector: positionVectors) {
            currentTile = origin_pos;

            for (int i = 0; i < distance; i++) {
                int[] newTile = new int[2];
                newTile[0] = currentTile[0] + vector[0];
                newTile[1] = currentTile[1] + vector[1];
                attackPattern.add(newTile);

                currentTile = newTile;
            }
        }
        return attackPattern;
    }

    //TODO: Abstract to reduce repeated code.
    private ArrayList<int[]> getStraightPattern(int[] origin_pos, int distance) {
        ArrayList<int[]> attackPattern = new ArrayList<int[]>();

        int[] currentTile = origin_pos;
        int[][] positionVectors = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

        for (int[] vector: positionVectors) {
            currentTile = origin_pos;

            for (int i = 0; i < distance; i++) {
                int[] newTile = new int[2];
                newTile[0] = currentTile[0] + vector[0];
                newTile[1] = currentTile[1] + vector[1];
                attackPattern.add(newTile);

                currentTile = newTile;
            }
        }
        return attackPattern;
    }

    private ArrayList<int[]> getStarPattern(int[] origin_pos) {
        return new ArrayList<int[]>();
    }

}
