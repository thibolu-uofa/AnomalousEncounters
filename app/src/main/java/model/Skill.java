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
    }

    public Skill(String name, String atkPatter) {
        this.name = name;
        this.baseDamage = 10;
        switch (atkPatter.toUpperCase()) {
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
    //getAffectedTiles(){}
    public int getDamage(){
        return baseDamage;
    }
}
