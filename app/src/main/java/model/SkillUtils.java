package model;

public class SkillUtils {
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
        //this functions increases skill experience (use some math, can just increase experience by 10 for now),
        // then add the calculated experience to currentExperience, if greater than maxExperience (which you get from getMaxExperience)
        // find out the extra amount and setCurrent experience to the extra amount and increase skill level by one
        // return the updated currentExperience and updated level
        int maxExperience = getMaxExperience(level);
        currentExperience += experienceGain;
        if (currentExperience >= maxExperience){
            currentExperience = currentExperience - maxExperience;
            level++;
        }
        return new int[]{level, currentExperience};
    }

    private int getExperienceGained(int tier, int phase, int usage) {
        //return amount of expereinced gained based on tier phase and skill usage
        return -1;
    }

    private static int getMaxExperience(int level) {
        // Return max experience based on level, for example, level times 100
        double exp = Math.pow(2.25, level) + 100;
        int maxExperience = (int) exp;
        return maxExperience;
    }
}
