package model;

import java.util.ArrayList;
import java.util.Random;

public class    EnemyUtils {
    public final static String ESSENCE_NAME = "Anomalous Essence";
    public final static String SHARD_NAME = "Anomalous Shard";
    public static int getEnemyMaxHealth(int tier) {
        Random rand = new Random();
        switch (tier) {
            case 4:
                return rand.nextInt(21) + 30; // 30-50
            case 3:
                return rand.nextInt(51) + 100; // 100-150
            case 2:
                return rand.nextInt(301) + 200; // 200-500
            default:
                return rand.nextInt(251) + 50; // 50-300
        }
    }

    public static int[] getEnemyDropsFromTier(int tier) {
        Random rand = new Random();
        int[] amountOfEssenceAndShards = {0, 0};
        int randInt;
        switch (tier) {
            // When killing a Tier 4 enemy, get 2-3 anomalous essence (weighted probability)
            case 4:
                randInt = rand.nextInt(2) + 2;
                amountOfEssenceAndShards[0] = randInt;
                break;
            // When killing a Tier 3 enemy, get 5-8 anomalous essence, chance to get 1 shard (50%)
            case 3:
                randInt = rand.nextInt(4) + 5;
                amountOfEssenceAndShards[0] = randInt;

                randInt = rand.nextInt(2);
                amountOfEssenceAndShards[1] = randInt;
                break;
            // When killing a Tier 2 enemy, get 3 anomalous shards 10-15 anomalous shards, 2-3 shards (equal chance)
            case 2:
                randInt = rand.nextInt(6) + 10;
                amountOfEssenceAndShards[0] = randInt;

                randInt = rand.nextInt(2) + 2;
                amountOfEssenceAndShards[1] = randInt;
                break;
        }
        return amountOfEssenceAndShards;
    }
}
