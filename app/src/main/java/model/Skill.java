package model;

import static model.SkillUtils.getSkillBaseDamage;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Skill {
    private final String name;
    private final int MAX_COOLDOWN;
    private int currentCooldown;
    private final int distance;
    private final int damage;
    private final AttackPattern atkPattern;

    private final AttackPattern.AttackType atkType;
    private int timesUsed = 0;
    private int id;

    public Skill(){
        this.name = "Entity";
        this.damage = 10;
        this.atkType = AttackPattern.AttackType.DIAGONAL;
        this.distance = 2;
        this.atkPattern = new AttackPattern(atkType);
        this.MAX_COOLDOWN = getMaxCooldown();
        this.currentCooldown = 0;
    }

    public Skill(String name, String atkPattern, int level, int tier, int id) {
        this.name = name;
        this.atkType = determineAttackType(atkPattern);
        this.atkPattern = new AttackPattern(atkType);
        this.distance = calculateSkillDistance(level);
        int baseDamage = getSkillBaseDamage(tier);
        this.damage = calculateSkillDamage(level, tier, baseDamage);
        this.MAX_COOLDOWN = getMaxCooldown();
        this.currentCooldown = 0;
        this.id = id;
    }

    private AttackPattern.AttackType determineAttackType(String atkPattern) {
        switch (atkPattern.toUpperCase()) {
            case "DIAGONAL":
                return AttackPattern.AttackType.DIAGONAL;
            case "STRAIGHT":
                return AttackPattern.AttackType.STRAIGHT;
            case "STAR":
                return AttackPattern.AttackType.STAR;
            case "CONE":
                return AttackPattern.AttackType.CONE;
            case "HOURGLASS":
                return AttackPattern.AttackType.HOURGLASS;
            case "BUTTERFLY":
                return AttackPattern.AttackType.BUTTERFLY;
            default:
                return AttackPattern.AttackType.STRAIGHT;
        }
    }

    private int getMaxCooldown() {
        if (Objects.requireNonNull(atkType) == AttackPattern.AttackType.STAR) {
            return 2;
        }
        return 1;
    }

    private int calculateSkillDistance(int level) {
        //maybe calculate distance based on level and type in a different way later?
        //currently the distance is only effective up to a certain level
        switch (atkType) {
            case CONE:
                return Math.max(level, 2) + 1;
            default:
                return Math.max(level, 2);
        }
    }


    // NOTE: resistance should be multiplied in the getBaseDamage because player doesn't have type,
    // so would be dependent on skill beings used
    private int calculateSkillDamage(int level, int tier, int baseDamage) {
        //[(skillDmg * level /(tier * 2)] + random number between 1 and 3 ^ 2) + baseDamage
        Random random = new Random();
        int randomFactor = random.nextInt(2) + 4;
        return ((baseDamage * level)/(tier * 2)) + randomFactor;
    }

    public boolean canUseSkill() {
        //skill van only been used when cooldown is 0
        if (currentCooldown != 0){
            return false;
        }
        return true;
    }

    public void updateSkillCooldown() {
        //if cooldown is equal to zero than return, because skill not on a cooldown
        //else decrease cooldown by 1
        if (currentCooldown == 0){
            return;
        }
        currentCooldown--;
    }

    public void activateSkillCooldown() {
        // set current cooldown equal to max cooldown
        currentCooldown = MAX_COOLDOWN;
        timesUsed++;
    }

    public ArrayList<int[]> getAffectedTiles(int[] origin_pos){
        return atkPattern.getAttackPattern(origin_pos, distance);
    }//

    public ArrayList<int[]> getAffectedTilesForMaxDistance(int[] origin_pos){
        int MAX_DISTANCE = 10;
        return atkPattern.getAttackPattern(origin_pos, MAX_DISTANCE);
    }

    public int getDamage(){
        return damage;
    }
    public String getName() {
        return name;
    }
    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public AttackPattern.AttackType getAtkType() {
        return atkType;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public int getId() {
        return id;
    }
}
