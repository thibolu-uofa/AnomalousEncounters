package model;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class SkillUtils {
    private int skillBook1 = 50;
    private int skillBook2 = 250;
    private int skillBook3 = 1000;
    public final static int SKILL_EXP_VOL_1_ID = 0;
    public final static int SKILL_EXP_VOL_2_ID = 1;
    public final static int SKILL_EXP_VOL_3_ID = 2;
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

        return (usage + phase) * tier;
    }

    private static int getMaxExperience(int level) {
        // Return max experience based on level, for example, level times 100
        double exp = Math.pow(2.25, level) + 100;
        int maxExperience = (int) exp;
        return maxExperience;
    }

    public static int getSkillExpGainedForVolume(int volume) {
        // return 50, 250, 1000 base on volume
        if (volume == 1){
            return 50;
        } else if (volume == 2) {
            return 250;
        }
        return 1000;
    }

    /*
        Forget Level 1 Skill, get 20 tokens
        Forget Level 2 Skill, get 1 Book of Skill I
        Forget Level 3 Skill, get 2 Book of Skill I
        Forget Level 4 Skill, get 3 Book of Skill I
        Forget Level 5 Skill, get 1 Book of Skill II
        Forget Level 6 Skill, get 1 Book of Skill II and 1 Book of Skill I
        LV 7, get 2 Book of Skill II
        LV8, get 3 Book of Skill II
        LV9, get 1 Book of Skill II and get 1 Book of Skill III
        LV10, get 2 Book of Skill III
     */
    public static void getSkillCompensation(int level, PlayerState playerState) {
        ArrayList<int[]> compensationItemIds = new ArrayList<>();
        int LV_1_TOKEN_COMPENSATION = 20;

        switch (level) {
            case 1:
                playerState.updateTokens(LV_1_TOKEN_COMPENSATION);
                break;
            case 2:
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_1_ID, 1});
                break;
            case 3:
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_1_ID, 2});
                break;
            case 4:
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_1_ID, 3});
                break;
            case 5:
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_2_ID, 1});
                break;
            case 6:
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_1_ID, 1});
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_2_ID, 1});
                break;
            case 7:
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_2_ID, 2});
            case 8:
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_2_ID, 3});
            case 9:
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_2_ID, 1});
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_3_ID, 1});
            case 10:
                compensationItemIds.add(new int[]{SKILL_EXP_VOL_3_ID, 2});
        }

        for (int[] item: compensationItemIds) {
            playerState.addItem(item[0], item[1]);
        }
    }



        private static Map<Types, List<Integer>> skillData; // Assume this is initialized elsewhere

        public static void setSkillData(Map<Types, List<Integer>> data) {
            skillData = data; // Load JSON into this structure
        }

        public static List<Integer> getSkillsByType(Types type) {
            return skillData.getOrDefault(type, List.of()); // Return list or empty list if type not found
        }

        public static int calculateEnemySkillLevel(int tier) {
            Random rand = new Random();
            int level = 1;
            switch (tier) {
                // Tier 4, 1-2
                case 4:
                    level = rand.nextInt(2) + 1;
                    break;
                // Tier 3, 3-4
                case 3:
                    level = rand.nextInt(2) + 3;
                    break;
                // Tier 2, 5-6
                case 2:
                    level = rand.nextInt(2) + 5;
                    break;
                //Tier 1, 7-10
                case 1:
                    level = rand.nextInt(4) + 7;
                    break;
            }
            return level;
        }
    }

