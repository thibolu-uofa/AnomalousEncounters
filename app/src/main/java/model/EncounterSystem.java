package model;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Random;

public class EncounterSystem {
    private final ArrayList<int[][]> startAndEndPointsForTypes = new ArrayList<>();
    private final int[][] enemyTypeIds = {{0, 3, 6}, {1, 4, 8}, {2, 5, 7}};
    private int encounterType;
    private int previousEnemyId = -1;
    int NUMBER_OF_ENEMIES = 9;

    public EncounterSystem() {
        // Each sub-array holds coordinate ranges for a type of encounter
        // Each pair is {startX, endX}
        int[][] coordsType0 = {{36, 38}, {51, 54}, {85, 88}};
        startAndEndPointsForTypes.add(coordsType0);

        int[][] coordsType1 = {{7, 9}, {25, 28}, {66, 68}};
        startAndEndPointsForTypes.add(coordsType1);

        int[][] coordsType2 = {{15, 17}, {45, 47}};
        startAndEndPointsForTypes.add(coordsType2);
    }

    public boolean hasEncounteredEnemy(int x) {
        if (!canEncounterEnemy(x)) {
            return false;
        }

        Random rand = new Random();
        int diceThrow = rand.nextInt(6) + 1; // Random number from 1 to 6 (inclusive)
        int ENCOUNTER_PROBABILITY = 4;       // 4 out of 6 = 66.7% chance
        return ENCOUNTER_PROBABILITY >= diceThrow;
    }

    private boolean canEncounterEnemy(int x) {
        for (int i = 0; i < startAndEndPointsForTypes.size(); i++) {
            int[][] encounterTypeCoords = startAndEndPointsForTypes.get(i);
            for (int[] startAndEndPoint : encounterTypeCoords) {
                // Check if x is within the inclusive range between start and end point
                if (x >= startAndEndPoint[0] && x <= startAndEndPoint[1]) {
                    encounterType = i; // Save the type for later use
                    return true;
                }
            }
        }
        return false;
    }

    public int getRandomEnemyId() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(enemyTypeIds[encounterType].length); // Choose a random index for this encounter type
        int enemyId = enemyTypeIds[encounterType][randomIndex];

        // Avoid choosing the same enemy twice in a row
        while (enemyId == previousEnemyId){
            enemyId = enemyTypeIds[encounterType][randomIndex];
            randomIndex = rand.nextInt(enemyTypeIds[encounterType].length);
        }

        previousEnemyId = enemyId; // Store the enemy ID to avoid repetition next time
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
        float randomProportion = random.nextFloat(); // Random float between 0.0 and 1.0

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
    }

    private int getPhaseTwoTier(float randomProportion) {
        // 30% chance Tier 4, 70% chance Tier 3
        if (randomProportion < 0.30) {
            return 4;
        } else  {
            return 3;
        }
    }

    private int getPhaseThreeTier(float randomProportion) {
        // 5% Tier 4, 25% Tier 3, 70% Tier 2
        if (randomProportion < 0.05) {
            return 4;
        } else if (randomProportion < 0.30) { // 0.05 + 0.25 = 0.30
            return 3;
        } else {
            return 2;
        }
    }

    private int getDefaultTier() {
        // Equal probability of Tier 1, 2, or 3
        Random random = new Random();
        return random.nextInt(3) + 1; // Returns 1, 2, or 3
    }

    public int getNumberOfEnemies() {
        return NUMBER_OF_ENEMIES;
    }
}