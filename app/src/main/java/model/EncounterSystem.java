package model;
import static java.lang.Math.abs;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class EncounterSystem {
    private final ArrayList<int[][]> startAndEndPointsForTypes = new ArrayList<>();
    private final int[][] enemyTypeIds = {{0, 3, 6}, {1, 4, 8}, {2, 5, 7}};
    private int encounterType;
    private int previousEnemyId = -1;
    int numberOfEnemies = 9;

    public EncounterSystem() {
        int[][] coordsType0 = {{-927, -1172}, {-3095, -3369}, {-6060, -6308}};
        startAndEndPointsForTypes.add(coordsType0);

        int[][] coordsType1 = {{-1826, -2003}, {-7003, -7107}, {530, 339}};
        startAndEndPointsForTypes.add(coordsType1);

        int[][] coordsType2 = {{-2610, -2810}};
        startAndEndPointsForTypes.add(coordsType2);
    }
    public boolean hasEncounteredEnemy(int x) {
        if (!canEncounterEnemy(x)) {
            return false;
        }

        Random rand = new Random();
        int diceThrow = rand.nextInt(6) + 1;
        int ENCOUNTER_PROBABILITY = 4;
        return ENCOUNTER_PROBABILITY >= diceThrow;
    }

    private boolean canEncounterEnemy(int x) {
        for (int i = 0; i < startAndEndPointsForTypes.size(); i++) {
            int[][] encounterTypeCoords = startAndEndPointsForTypes.get(i);
            for (int[] startAndEndPoint : encounterTypeCoords) {
                if (x <= startAndEndPoint[0] && x >= startAndEndPoint[1]) {
                    encounterType = i;
                    return true;
                }
            }
        }
        return false;
    }

    public int getRandomEnemyId() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(enemyTypeIds[encounterType].length);
        int enemyId = enemyTypeIds[encounterType][randomIndex];

        while (enemyId == previousEnemyId){
            enemyId = enemyTypeIds[encounterType][randomIndex];
            randomIndex = rand.nextInt(enemyTypeIds[encounterType].length);
        }

        // Update previousEnemyId with the current value
        previousEnemyId = enemyId;

        return enemyId;
    }

    /**
     * Returns an enemy tier based on the current game phase following these probabilities:
     * Phase 1: Tier 1 (85%), Tier 2 (15%)
     * Phase 2: Tier 1 (10%), Tier 2 (80%), Tier 3 (10%)
     * Phase 3: Tier 1 (5%), Tier 2 (25%), Tier 3 (70%)
     * Default: Equal chance of any tier
     *
     * @param phase the current game phase (1, 2, 3, or 4)
     * @return The selected enemy tier (1, 2, or 3)
     */
    public int getEnemyTier(int phase) {
        Random random = new Random();
        float randomProportion = random.nextFloat(); // value between 0.0 and 1.0

        switch (phase) {
            case 1:
                return getPhaseOneTier(randomProportion);
            case 2:
                return getPhaseTwoTier(randomProportion);
            case 3:
                return getPhaseThreeTier(randomProportion);
            default:
                return getDefaultTier();
        }
    }

    private int getPhaseOneTier(float randomProportion) {
        return 4;

        // Phase 1: 85% chance of Tier 4, 15% chance of Tier 3
//        if (randomProportion < 0.85) {
//            return 4;
//        } else {
//            return 3;
//        }
    }

    private int getPhaseTwoTier(float randomProportion) {
        // Phase 2: 10% chance of Tier 4, 80% chance of Tier 3, 10% chance of Tier 2
        if (randomProportion < 0.30) {
            return 4;
        } else  { // 0.10 + 0.80 = 0.90
            return 3;
        }
    }

    private int getPhaseThreeTier(float randomProportion) {
        // Phase 3: 5% chance of Tier 4, 25% chance of Tier 3, 70% chance of Tier 2
        if (randomProportion < 0.05) {
            return 4;
        } else if (randomProportion < 0.30) { // 0.05 + 0.25 = 0.30
            return 3;
        } else {
            return 2;
        }
    }

    private int getDefaultTier() {
        // Equal chance of any tier
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    public int getNumberOfEnemies() {
        return numberOfEnemies;
    }
}
