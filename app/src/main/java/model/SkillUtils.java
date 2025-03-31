package model;

public class SkillUtils {
    private int skillBook1 = 50;
    private int skillBook2 = 250;
    private int skillBook3 = 1000;

    public enum Types {
        LIFE,
        DEATH,
        NOTHINGNESS
    }

    public static int getSkillBaseDamage(int tier) {
        return 50 - (10 * tier);
    }

    // NOTHINGNESS > DEATH > LIFE > NOTHINGNESS (weak the other way around)
    public static double getResistanceFactor(Types activeType, Types receivingType) {
        if ((activeType == Types.NOTHINGNESS && receivingType == Types.DEATH )
                || (activeType == Types.DEATH && receivingType == Types.LIFE)
                || (activeType == Types.LIFE && receivingType == Types.NOTHINGNESS)) {
            return 1.25;
        } else if ((activeType == Types.DEATH && receivingType == Types.NOTHINGNESS )
                || (activeType == Types.LIFE && receivingType == Types.DEATH)
                || (activeType == Types.NOTHINGNESS && receivingType == Types.LIFE) ) {
            return 0.75;
        }
        return 1;
    }

    public static int[] getUpdatedLevelAndExperience(int level, int currentExperience, int experienceGain) {
        int maxExperience = getMaxExperience(level);
        currentExperience += experienceGain;
        if (currentExperience >= maxExperience){
            currentExperience = currentExperience - maxExperience;
            level++;
        }
        return new int[]{level, currentExperience};
    }

    private int getExperienceGained(int tier, int phase, int usage) {
        //return amount of experienced gained based on tier phase and skill usage

        return (usage + phase)*  tier;
    }

    private static int getMaxExperience(int level) {
        // Return max experience based on level, for example, level times 100
        double exp = Math.pow(2.25, level) + 100;
        int maxExperience = (int) exp;
        return maxExperience;
    }

    public static int getSkillExpGainedForVolume(int volume) {
        // return 50, 250, 1000 base on volume
        return -1;
    }
}
