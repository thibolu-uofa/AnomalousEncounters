package model;

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
}
