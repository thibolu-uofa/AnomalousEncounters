package model;

import static model.Utils.getSingleDataProperty;

import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

public class EnemyUtils {
    public final static String ESSENCE_NAME = "Anomalous Essence";
    public final static String SHARD_NAME = "Anomalous Shard";

    public static int getEnemyMaxHealth(int tier) {
        Random rand = new Random();
        switch (tier) {
            case 4:
                // Tier 4: random value from 30 to 50 (inclusive)
                return rand.nextInt(21) + 30; // [0, 20] + 30
            case 3:
                // Tier 3: random value from 100 to 150
                return rand.nextInt(51) + 100; // [0, 50] + 100
            case 2:
                // Tier 2: random value from 200 to 500
                return rand.nextInt(301) + 200; // [0, 300] + 200
            default:
                // Tier 1 or default: random value from 50 to 300
                return rand.nextInt(251) + 50; // [0, 250] + 50
        }
    }

    public static int[] getEnemyDropsFromTier(int tier) {
        Random rand = new Random();
        int[] amountOfEssenceAndShards = {0, 0}; // [essence, shard]
        int randInt;

        switch (tier) {
            case 4:
                // Tier 4: 2–3 essence (weighted probability by using rand between 2 and 3)
                randInt = rand.nextInt(2) + 2; // [0,1] + 2 = 2 or 3
                amountOfEssenceAndShards[0] = randInt;
                break;

            case 3:
                // Tier 3: 5–8 essence
                randInt = rand.nextInt(4) + 5; // [0,3] + 5 = 5 to 8
                amountOfEssenceAndShards[0] = randInt;

                // 50% chance of getting 1 shard (either 0 or 1)
                randInt = rand.nextInt(2); // [0,1]
                amountOfEssenceAndShards[1] = randInt;
                break;

            case 2:
                // Tier 2: 10–15 essence
                randInt = rand.nextInt(6) + 10; // [0,5] + 10 = 10 to 15
                amountOfEssenceAndShards[0] = randInt;

                // 2–3 shards
                randInt = rand.nextInt(2) + 2; // [0,1] + 2 = 2 or 3
                amountOfEssenceAndShards[1] = randInt;
                break;
        }
        return amountOfEssenceAndShards;
    }

    public static SkillUtils.AnomalyTypes getEnemyTypeFromId(int id, Context context) {
        String typeString = (String) getSingleDataProperty("enemies.json", "type", id, context);
        SkillUtils.AnomalyTypes type = SkillUtils.AnomalyTypes.DEATH;
        switch (typeString) {
            case "Life":
                type = SkillUtils.AnomalyTypes.LIFE;
                break;
            case "Nothingness":
                type = SkillUtils.AnomalyTypes.NOTHINGNESS;
                break;
        }
        return type;
    }
}