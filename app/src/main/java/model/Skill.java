package model;

import java.util.ArrayList;

public class Skill {
    private final String name;
    private final int MAX_COOLDOWN;
    private int currentCooldown;
    private final int length;
    private final int baseDamage;
    private final AttackPattern atkPattern;

    private final AttackPattern.AttackType atkType;

    public Skill(){
        this.name = "Entity";
        this.baseDamage = 10;
        this.atkType = AttackPattern.AttackType.DIAGONAL;
        this.length = 2;
        this.atkPattern = new AttackPattern(atkType);
        this.MAX_COOLDOWN = 1;
        this.currentCooldown = 0;
    }

    public Skill(String name, String atkPattern, int level, int MAX_COOLDOWN) {
        this.name = name;
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
        this.atkPattern = new AttackPattern(atkType);
        this.length = calculateSkillDistance(level);
        this.baseDamage = calculateSkillBaseDamage(level);
        this.MAX_COOLDOWN = MAX_COOLDOWN;
        this.currentCooldown = 0;
    }

    private int calculateSkillDistance(int level) {
        //return level for STAR and CONE and level + 1 for Diagonal and Straight
        //maybe calculate distance based on level and type in a different way later?
        return level;
    }

    private int calculateSkillBaseDamage(int level) {
        //come up with an equation to calculate skill base damage based off of level and atk type
        //maybe also have a degree of randomness

        return level * 2;
    }

    public boolean canUseSkill() {
        //if skill cooldown is greater than 0 returns false
        return true;
    }

    public void updateSkillCooldown() {
        //if cooldown is equal to zero than return, because skill not on a cooldown
        //else decrease cooldown by 1
    }

    public ArrayList<int[]> getAffectedTiles(int[] origin_pos){
        return atkPattern.getAttackPattern(origin_pos, length);
    }//
    public int getDamage(){
        return baseDamage;
    }
    public String getName() {
        return name;
    }
    public int getCurrentCooldown() {
        return currentCooldown;
    }
}
