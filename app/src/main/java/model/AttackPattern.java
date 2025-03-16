package model;
import static model.Utils.getRepeatingPattern;

import java.util.ArrayList;

public class AttackPattern {
    public enum AttackType {
        DIAGONAL,
        STAIGHT,
        STAR,
        CONE
    }

    private final AttackType attackType;
    private final int maxRows = 6;
    private final int maxCols = 8;

    AttackPattern(AttackType attackType) {
        this.attackType = attackType;
    }
    
    public ArrayList<int[]> getAttackPattern(int[] origin_pos, int distance) {
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
        return getRepeatingPattern(origin_pos, distance, positionVectors, maxRows, maxCols);
    }
    
    private ArrayList<int[]> getStraightPattern(int[] origin_pos, int distance) {
        int[][] positionVectors = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        return getRepeatingPattern(origin_pos, distance, positionVectors, maxRows, maxCols);
    }

    private ArrayList<int[]> getStarPattern(int[] origin_pos, int distance) {
        int[][] positionVectors = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        return getRepeatingPattern(origin_pos, distance, positionVectors, maxRows, maxCols);
    }

    private ArrayList<int[]> getConePattern(int[] origin_pos, int distance) {
        int[][] positionVectors = {{-1, 1}, {0, 1}, {1, 1}};
        return getRepeatingPattern(origin_pos, distance, positionVectors, maxRows, maxCols);
    }

}
