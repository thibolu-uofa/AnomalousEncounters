package com.example.anomalousencounters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.EnemyUtils;

public class SkillEnemyItemUtilsUnitTest {
    @Test
    public void testTier4HealthRange() {
        for (int i = 0; i < 100; i++) {
            int health = EnemyUtils.getEnemyMaxHealth(4);
            assertTrue("Tier 4 health out of range: " + health, health >= 30 && health <= 50);
        }
    }

    @Test
    public void testTier3HealthRange() {
        for (int i = 0; i < 100; i++) {
            int health = EnemyUtils.getEnemyMaxHealth(3);
            assertTrue("Tier 3 health out of range: " + health, health >= 100 && health <= 150);
        }
    }

    @Test
    public void testTier2HealthRange() {
        for (int i = 0; i < 100; i++) {
            int health = EnemyUtils.getEnemyMaxHealth(2);
            assertTrue("Tier 2 health out of range: " + health, health >= 200 && health <= 500);
        }
    }

    @Test
    public void testDefaultHealthRange() {
        for (int i = 0; i < 100; i++) {
            int health = EnemyUtils.getEnemyMaxHealth(1);
            assertTrue("Default tier health out of range: " + health, health >= 50 && health <= 300);
        }
    }

    @Test
    public void testTier4Drops() {
        for (int i = 0; i < 100; i++) {
            int[] drops = EnemyUtils.getEnemyDropsFromTier(4);
            assertEquals(2, drops.length);
            assertTrue("Tier 4 essence drop invalid", drops[0] >= 2 && drops[0] <= 3);
            assertEquals(0, drops[1]);

        }
    }

    @Test
    public void testTier3Drops() {
        for (int i = 0; i < 100; i++) {
            int[] drops = EnemyUtils.getEnemyDropsFromTier(3);
            assertEquals(2, drops.length);
            assertTrue("Tier 3 essence drop invalid", drops[0] >= 5 && drops[0] <= 8);
            assertTrue("Tier 3 shard drop must be 0 or 1", drops[1] == 0 || drops[1] == 1);
        }
    }

    @Test
    public void testTier2Drops() {
        for (int i = 0; i < 100; i++) {
            int[] drops = EnemyUtils.getEnemyDropsFromTier(2);
            assertEquals(2, drops.length);
            assertTrue("Tier 2 essence drop invalid", drops[0] >= 10 && drops[0] <= 15);
            assertTrue("Tier 2 shard drop invalid", drops[1] >= 2 && drops[1] <= 3);
        }
    }

    @Test
    public void testConstants() {
        assertEquals("Anomalous Essence", EnemyUtils.ESSENCE_NAME);
        assertEquals("Anomalous Shard", EnemyUtils.SHARD_NAME);
    }
}
