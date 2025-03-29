package model;

import java.util.Random;

public class    EnemyUtils {
    public static int getEnemyMaxHealth(int tier) {
        Random rand = new Random();
        switch (tier) {
            case 4:
                return rand.nextInt(51) + 50; // 50-100
            case 3:
                return rand.nextInt(101) + 150; // 150-250
            case 2:
                return rand.nextInt(201) + 300; // 300-500
            default:
                return rand.nextInt(251) + 50; // 50-300
        }
    }
}
