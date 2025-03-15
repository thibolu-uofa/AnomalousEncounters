package model;

import java.util.ArrayList;

public class Skill {
    private String name;
    private int level;
    private int maxExperience;
    private int currentExperience;
    private int maxCooldown;
    private int currentCooldown;
    private int length;

    private int baseDamage;
    private AttackPattern atkPattern;

    private AttackPattern.AttackType atkType;

    public Skill(){
        this.name = "Entity";
        this.baseDamage = 10;
        this.atkType = AttackPattern.AttackType.DIAGONAL;
        this.length = 2;
    }

    public Skill(String name, String atkPattern) {
        this.name = name;
        this.baseDamage = 10;
        this.length = 2;
        switch (atkPattern.toUpperCase()) {
            case "DIAGONAL":
                this.atkType = AttackPattern.AttackType.DIAGONAL;
                break;
            case "STAIGHT":
                this.atkType = AttackPattern.AttackType.STAIGHT;
                break;
            case "STAR":
                this.atkType = AttackPattern.AttackType.STAR;
                break;
            case "CONE":
                this.atkType = AttackPattern.AttackType.CONE;
                break;
            default:
                this.atkType = AttackPattern.AttackType.STAIGHT;
        }
    }
    public ArrayList<int[]> getAffectedTiles(int[] origin_pos){
        return atkPattern.getAttackPattern(origin_pos, length);
    }
    public int getDamage(){
        return baseDamage;
    }

}
