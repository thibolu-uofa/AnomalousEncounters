package model;
import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Random;

import presenter.GamePresenter;

public class EncounterSystem {
    private GamePresenter presenter;
    private final ArrayList<int[]> startAndEndPoints = new ArrayList<>();
    private final int ENCOUNTER_PROBABILITY = 4;
    private int numberOfEnemies = 9;
    private int previousEnemyId = -1;

    public EncounterSystem() {
        startAndEndPoints.add(new int[]{-927, -1172});
        startAndEndPoints.add(new int[]{-1826, -2003});
        startAndEndPoints.add(new int[]{-2610, -2810});
    }
    public boolean hasEncounteredEnemy(int x) {
        if (canEncounterEnemy(x)) {
            Random rand = new Random();
            int diceThrow = rand.nextInt(6) + 1;
            return ENCOUNTER_PROBABILITY >= diceThrow;
        }
        return false;
    }

    private boolean canEncounterEnemy(int x) {
        for (int[] startAndEndPoint: startAndEndPoints){
            if (abs(x) >= abs(startAndEndPoint[0]) && abs(x) <= abs(startAndEndPoint[1])){

               return true;
            }
        }
        return false;
    }

    public int getRandomEnemyId() {
        Random rand = new Random();
        int enemyId = rand.nextInt(numberOfEnemies);

        while (enemyId == previousEnemyId){
            enemyId = rand.nextInt(numberOfEnemies);
        }

        if (previousEnemyId == -1){
            previousEnemyId = enemyId;
        }

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

        // get tier based on the current phase
        if (phase == 1) {
            // Phase 1: 85% chance of Tier 4, 15% chance of Tier 3
            if (randomProportion < 0.85) {
                return 4;
            } else {
                return 3;
            }
        }
        else if (phase == 2) {
            // Phase 2: 10% chance of Tier 4, 80% chance of Tier 3, 10% chance of Tier 2
            if (randomProportion < 0.10) {
                return 4;
            } else if (randomProportion < 0.90) { // 0.10 + 0.80 = 0.90
                return 3;
            } else {
                return 2;
            }
        }
        else if (phase == 3) {
            // Phase 3: 5% chance of Tier 4, 25% chance of Tier 3, 70% chance of Tier 2
            if (randomProportion < 0.05) {
                return 4;
            } else if (randomProportion < 0.30) { // 0.05 + 0.25 = 0.30
                return 3;
            } else {
                return 2;
            }
        }
        else {
            // equal chance of any tier
            return random.nextInt(3) + 1;
        }
    }
}
