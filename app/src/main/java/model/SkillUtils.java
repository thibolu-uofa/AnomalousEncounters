package model;

public class SkillUtils {
    public static int[] getUpdatedLevelAndExperience(int level, int currentExperience, int experienceGiven) {
        //this functions increases skill experience (use some math, can just increase experience by 10 for now),
        // then add the calculated experience to currentExperience, if greater than maxExperience (which you get from getMaxExperience)
        // find out the extra amount and setCurrent experience to the extra amount and increase skill level by one
        // return the updated currentExperience and updated level
        int maxExperience = getMaxExperience(level);
        currentExperience += experienceGiven;
        if (currentExperience >= maxExperience){
            currentExperience = currentExperience - maxExperience;
            level++;
        }
        return new int[]{level, currentExperience};
    }

    private static int getMaxExperience(int level) {
        //return max experience based on level, for example level times 100

        return (2 ^ level) +  100;
    }
}
