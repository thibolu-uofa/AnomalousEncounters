package model;

public class ItemUtils {
    public static void useHealthPotion(PlayerState playerState) {
        // increase player state health by 20% of max health
    }

    public static int[] useExperienceBook(int volume, int level, int currentExperience) {
        //get experience gained from getSkillExpGainedForVolume
        // getUpdatedLevelAndExperience from skill utils
        return new int[2];
    }

    public static void useSkillTome(SkillUtils.Types tomeType,PlayerState playerState) {
        //switch case on tomeType, and add a skill id of that type that the player
        //does not already have
    }
}
