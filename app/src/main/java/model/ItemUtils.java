package model;

public class ItemUtils {
    public static void useHealthPotion(PlayerState playerState) {
        // increase player state health by 20% of max health
        int maxHealth = playerState.getPlayerMaxHealth();
        int healthIncrease = (int) (maxHealth * 0.2); // 20% of max health
        playerState.modifyHealth(healthIncrease);
    }


    public static int[] useExperienceBook(int volume, int level, int currentExperience) {
        //get experience gained from getSkillExpGainedForVolume
        // getUpdatedLevelAndExperience from skill utils
        int expGained = SkillUtils.getSkillExpGainedForVolume(volume);
        return SkillUtils.getUpdatedLevelAndExperience(level, currentExperience, expGained);
    }

    public static void useSkillTome(SkillUtils. Types tomeType,PlayerState playerState) {
        //switch case on tomeType, and add a skill id of that type that the player
        //does not already have
        for (int skillId : SkillUtils.getSkillsByType(tomeType)) {
            if (playerState.getLevelOfSkill(skillId) == -1) { // Check if the player doesn't have the skill
                playerState.addSkill(skillId, 1); // Add skill at level 1
                break; // Add only one new skill
            }
        }
    }
}
