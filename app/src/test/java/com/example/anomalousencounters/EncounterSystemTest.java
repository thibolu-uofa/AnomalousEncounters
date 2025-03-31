package com.example.anomalousencounters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import model.EncounterSystem;
import model.PlayerState;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EncounterSystemTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void hasEncounteredEnemy_isCorrect(){
        double actualProbability = getActualProbability();
        double expectedProbability = 4.0 / 6.0;
        double allowedDeviation = 0.05;

        assertTrue("Expected probability: " + expectedProbability +
                        ", Actual probability: " + actualProbability,
                Math.abs(actualProbability - expectedProbability) < allowedDeviation);
    }

    private static double getActualProbability() {
        EncounterSystem encounterSystem = new EncounterSystem();
        int validPosition = -950;

        int totalTrials = 10000;
        int encounters = 0;

        // Run many trials to get statistically significant results
        for (int i = 0; i < totalTrials; i++) {
            if (encounterSystem.hasEncounteredEnemy(validPosition)) {
                encounters++;
            }
        }

        // Calculate the actual probability
        return (double) encounters / totalTrials;
    }

    @Test
    public void testRandomEnemyIdDistribution() {
        EncounterSystem encounterSystem = new EncounterSystem();
        int totalTrials = 10000;

        // track the distribution of enemy IDs
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            distribution.put(i, 0);
        }

        int previousId = -1, currentId;

        for (int i = 0; i < totalTrials; i++) {
            currentId = encounterSystem.getRandomEnemyId();
            if (previousId != -1) {
                assertNotEquals(previousId, currentId);
            }
            distribution.put(currentId, distribution.get(currentId) + 1);
            previousId = currentId;
        }

        double expectedFrequency = totalTrials / 9.0;
        double allowedDeviation = 0.1 * expectedFrequency;

        // make sure each ID appears with roughly equal frequency
        for (int i = 0; i < 9; i++) {
            int count = distribution.get(i);
            assertTrue("Enemy ID " + i + " appeared " + count + " times, expected around " +
                            expectedFrequency + " ±" + allowedDeviation,
                    Math.abs(count - expectedFrequency) < allowedDeviation);
        }
    }

    @Test
    public void testEnemyTierDistribution() {
        EncounterSystem encounterSystem = new EncounterSystem();
        int totalTrials = 100000;

        for (int phase = 1; phase <= 4; phase++) {
            // keep track of the distributions of tiers for current phase
            Map<Integer, Integer> distribution = new HashMap<>();
            distribution.put(1, 0);
            distribution.put(2, 0);
            distribution.put(3, 0);
            distribution.put(4, 0);

            for (int i = 0; i < totalTrials; i++) {
                int tier = encounterSystem.getEnemyTier(phase);
                distribution.put(tier, distribution.get(tier) + 1);
            }

            Map<Integer, Double> expectedProbabilities = new HashMap<>();

            switch (phase) {
                case 1:
                    // Phase 1: 85% chance of Tier 4, 15% chance of Tier 3
                    expectedProbabilities.put(2, 0.0);
                    expectedProbabilities.put(3, 0.15);
                    expectedProbabilities.put(4, 0.85);
                    expectedProbabilities.put(1, 0.0);
                    break;
                case 2:
                    // Phase 2: 10% chance of Tier 4, 80% chance of Tier 3, 10% chance of Tier 2
                    expectedProbabilities.put(2, 0.10);
                    expectedProbabilities.put(3, 0.80);
                    expectedProbabilities.put(4, 0.10);
                    expectedProbabilities.put(1, 0.0);
                    break;
                case 3:
                    // Phase 3: 5% chance of Tier 4, 25% chance of Tier 3, 70% chance of Tier 2
                    expectedProbabilities.put(2, 0.70);
                    expectedProbabilities.put(3, 0.25);
                    expectedProbabilities.put(4, 0.05);
                    expectedProbabilities.put(1, 0.0);
                    break;
                case 4:
                    // Phase 4: Equal chance of tiers 1-3
                    expectedProbabilities.put(1, 0.333);
                    expectedProbabilities.put(2, 0.333);
                    expectedProbabilities.put(3, 0.333);
                    expectedProbabilities.put(4, 0.0);
                    break;
            }

            // verify distribution matches expected probabilities (with tolerance)
            for (int tier = 1; tier <= 4; tier++) {
                double actualProbability = (double) distribution.get(tier) / totalTrials;
                double expectedProbability = expectedProbabilities.get(tier);

                double allowedDeviation = 0.02;

                assertTrue(Math.abs(actualProbability - expectedProbability) < allowedDeviation);
            }
        }
    }


}