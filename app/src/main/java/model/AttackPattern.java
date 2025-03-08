package model;

public class AttackPattern {
    private enum AttackType {
        DIAGONAL,
        STAIGHT,
        STAR
    }

    private AttackType attackType;

    AttackPattern(AttackType attackType) {
        this.attackType = attackType;
    }
    
    public int[][] getAttackPattern(int[] origin_pos) {
        int[][] attackPattern;
        switch (attackType) {
            case DIAGONAL:
                return getDiagonalPattern(origin_pos);
            case STAIGHT:
                return getStraightPattern(origin_pos);
            case STAR:
                return getStarPattern(origin_pos);
            default:
                return new int[0][0];
        }
    }

    private int [][] getDiagonalPattern(int[] origin_pos) {
        return new int[0][0];
    }

    private int [][] getStraightPattern(int[] origin_pos) {
        return new int[0][0];
    }

    private int [][] getStarPattern(int[] origin_pos) {
        return new int[0][0];
    }
}
