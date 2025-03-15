package model;
import java.util.ArrayList;

public class AttackPattern {
    public enum AttackType {
        DIAGONAL,
        STAIGHT,
        STAR,
        CONE
    }

    private final AttackType attackType;
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
                return getStarPattern(origin_pos, distance);
            case CONE:
                return getConePattern(origin_pos, distance);
            default:
                return new ArrayList<int[]>();
        }
    }

    private ArrayList<int[]> getDiagonalPattern(int[] origin_pos, int distance) {
        int[][] positionVectors = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        return getRepeatingPattern(origin_pos, distance, positionVectors);
    }
    
    private ArrayList<int[]> getStraightPattern(int[] origin_pos, int distance) {
        int[][] positionVectors = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        return getRepeatingPattern(origin_pos, distance, positionVectors);
    }

    private ArrayList<int[]> getStarPattern(int[] origin_pos, int distance) {
        int[][] positionVectors = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        return getRepeatingPattern(origin_pos, distance, positionVectors);
    }

    private ArrayList<int[]> getConePattern(int[] origin_pos, int distance) {
        int[][] positionVectors = {{-1, 1}, {0, 1}, {1, 1}};
        return getRepeatingPattern(origin_pos, distance, positionVectors);
    }

    private ArrayList<int[]> getRepeatingPattern(int[] origin_pos, int distance, int[][] positionVectors) {
        ArrayList<int[]> attackPattern = new ArrayList<int[]>();
        
        for (int[] vector: positionVectors) {
            int[] currentTile = origin_pos;

            for (int i = 0; i < distance; i++) {
                int[] newTile = new int[2];
                newTile[0] = currentTile[0] + vector[0];
                newTile[1] = currentTile[1] + vector[1];
                if (!(newTile[0] < 0 || newTile[0] >= maxRows || newTile[1] < 0 || newTile[1] >= maxCols)) {
                    attackPattern.add(newTile);
                }
                currentTile = newTile;
            }
        }
        return attackPattern;
    }

}
