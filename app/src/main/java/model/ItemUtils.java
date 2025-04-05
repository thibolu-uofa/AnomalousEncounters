package model;

import java.util.ArrayList;
import java.util.Random;

public class ItemUtils {
    public static void useHealthRune(PlayerState playerState, int runeNumber) {
        // increase player state health by 20% of max health
        int maxHealth = playerState.getPlayerMaxHealth();
        if (runeNumber == 1){
            int healthIncrease = (int) (maxHealth * 0.2); // 20% of max health
            playerState.modifyHealth(healthIncrease);
        } else if (runeNumber == 2) {
            int healthIncrease = (int) (maxHealth * 0.5); // 20% of max health
            playerState.modifyHealth(healthIncrease);
        }
    }

    public static int[] useExperienceBook(int volume, int level, int currentExperience) {
        //get experience gained from getSkillExpGainedForVolume
        // getUpdatedLevelAndExperience from skill utils
        int expGained = SkillUtils.getSkillExpGainedForVolume(volume);
        return SkillUtils.getUpdatedLevelAndExperience(level, currentExperience, expGained);
    }

    //TODO: What to do when the player already has all skills of that type, maybe send message that
    // You already have all the skills of that type
    public static void useSkillStone(SkillUtils.AnomalyTypes tomeType, PlayerState playerState) {
        int[] skillIds = SkillUtils.getSkillsByType(tomeType);
        ArrayList<Integer> newSkillIds = new ArrayList<>();

        for (int skillId : skillIds) {
            if (playerState.getLevelOfSkill(skillId) == -1) { // Check if the player doesn't have the skill
                newSkillIds.add(skillId);
            }
        }

        // player already has all skills of that type, return for now
        if (newSkillIds.isEmpty()) {
            return;
        }

        Random random = new Random();
        int randIndex = random.nextInt(newSkillIds.size());
        int newSkillId = newSkillIds.get(randIndex);
        playerState.addSkill(newSkillId, 1, 0); // Add skill at level 1
    }
}
