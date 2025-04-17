package com.example.anomalousencounters;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.EnemyUtils;
import model.ItemUtils;
import model.PlayerState;
import model.SkillUtils;

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

    @Test
    public void testSkillBaseDamage() {
        assertEquals(40, SkillUtils.getSkillBaseDamage(1));
        assertEquals(30, SkillUtils.getSkillBaseDamage(2));
        assertEquals(20, SkillUtils.getSkillBaseDamage(3));
    }

    @Test
    public void testResistanceFactor() {
        assertEquals(1.25, SkillUtils.getResistanceFactor(SkillUtils.AnomalyTypes.NOTHINGNESS, SkillUtils.AnomalyTypes.DEATH));
        assertEquals(1.25, SkillUtils.getResistanceFactor(SkillUtils.AnomalyTypes.DEATH, SkillUtils.AnomalyTypes.LIFE));
        assertEquals(1.25, SkillUtils.getResistanceFactor(SkillUtils.AnomalyTypes.LIFE, SkillUtils.AnomalyTypes.NOTHINGNESS));

        assertEquals(0.75, SkillUtils.getResistanceFactor(SkillUtils.AnomalyTypes.DEATH, SkillUtils.AnomalyTypes.NOTHINGNESS));
        assertEquals(0.75, SkillUtils.getResistanceFactor(SkillUtils.AnomalyTypes.LIFE, SkillUtils.AnomalyTypes.DEATH));
        assertEquals(0.75, SkillUtils.getResistanceFactor(SkillUtils.AnomalyTypes.NOTHINGNESS, SkillUtils.AnomalyTypes.LIFE));

        assertEquals(1.0, SkillUtils.getResistanceFactor(SkillUtils.AnomalyTypes.LIFE, SkillUtils.AnomalyTypes.LIFE));
    }

    @Test
    public void testGetUpdatedLevelAndExperience() {
        int[] result = SkillUtils.getUpdatedLevelAndExperience(1, 200, 300);
        assertEquals(2, result[0]); // Level up
        assertTrue(result[1] >= 0); // Remaining EXP should be non-negative
    }

    @Test
    public void testGetExperienceGained() {
        assertEquals(8, SkillUtils.getExperienceGained(2, 1, 1));
        assertEquals(18, SkillUtils.getExperienceGained(3, 2, 1));
    }

    @Test
    public void testMaxExperienceGrowth() {
        assertTrue(SkillUtils.getMaxExperience(1) < SkillUtils.getMaxExperience(2));
        assertTrue(SkillUtils.getMaxExperience(5) < SkillUtils.getMaxExperience(10));
    }

    @Test
    public void testSkillExpGainForVolume() {
        assertEquals(50, SkillUtils.getSkillExpGainedForVolume(1));
        assertEquals(250, SkillUtils.getSkillExpGainedForVolume(2));
        assertEquals(1000, SkillUtils.getSkillExpGainedForVolume(3));
    }

    @Test
    public void testSkillCompensationTokens() {
        PlayerState state = new PlayerState();
        SkillUtils.getSkillCompensation(1, state);
        assertEquals(40, state.getTokens());
    }



    @Test
    public void testGetSkillsByType() {
        assertArrayEquals(new int[]{0, 1, 2, 3, 12}, SkillUtils.getSkillsByType(SkillUtils.AnomalyTypes.LIFE));
        assertArrayEquals(new int[]{4, 5, 6, 7, 13}, SkillUtils.getSkillsByType(SkillUtils.AnomalyTypes.DEATH));
        assertArrayEquals(new int[]{8, 9, 10, 11, 14}, SkillUtils.getSkillsByType(SkillUtils.AnomalyTypes.NOTHINGNESS));
    }

    @Test
    public void testEnemySkillLevelRange() {
        for (int i = 0; i < 100; i++) {
            int lvl = SkillUtils.calculateEnemySkillLevel(3);
            assertTrue("Tier 3 skill level should be 2–3, got: " + lvl, lvl >= 2 && lvl <= 3);
        }
    }
    @Test
    public void testHealthModification() {
        PlayerState player = new PlayerState();
        player.modifyHealth(10); // Increase health by 10
        assertEquals(100, player.getHealth());

        player.modifyHealth(-150); // Decrease health by 150 (should clamp to 0)
        assertEquals(0, player.getHealth());
    }

    // Test Adding and Removing Skills
    /* @Test
    public void testAddAndRemoveSkills() {
        PlayerState player = new PlayerState();
        player.addSkill(1, 5, 100);
        player.addSkill(2, 3, 50);

        // Test skill addition
        assertEquals(5, player.getLevelOfSkill(1));
        assertEquals(3, player.getLevelOfSkill(2));

        // Remove skill and check
        player.removeSkill(1);
        assertEquals(-1, player.getLevelOfSkill(1));
    }

    // Test Adding and Removing Items
    @Test
    public void testAddAndRemoveItems() {
        PlayerState player = new PlayerState();

        player.addItem(101, 5); // Add 5 of item with ID 101
        assertArrayEquals(new int[] {101}, player.getItemList();

        player.addItem(101, 3); // Add 3 more of item 101
        assertArrayEquals(new int[] {101}, player.getItemList());

        player.removeItem(101); // Remove 1 of item 101
        assertEquals(7, player.getItemAmountsList()[0]);

        player.removeItem(101); // Remove last item of 101
        assertArrayEquals(new int[] {}, player.getItemList());
    }
*/
    // Test Token Management
    @Test
    public void testTokenManagement() {
        PlayerState player = new PlayerState();

        // Tokens should be 20 by default
        assertEquals(20, player.getTokens());

        // Update tokens and ensure they don't exceed max
        player.updateTokens(50);
        assertEquals(70, player.getTokens());

        // Tokens shouldn't drop below 0
        player.updateTokens(-100);
        assertEquals(0, player.getTokens());

        // Check if tokens update is valid
        assertTrue("Player should be able to update tokens by -10", player.canUpdateTokens(10));
        assertFalse("Player should not be able to update tokens by a huge negative number", player.canUpdateTokens(-99999));
    }@Test
    public void testUseHealthRune_rune1_increasesHealthBy20Percent() {
        PlayerState player = new PlayerState("Test", 100, 0);
        player.setPlayerCurrentHealth(50);
        ItemUtils.useHealthRune(player, 1);
        assertEquals(70, player.getHealth()); // 50 + 20% of 100 = 70
    }

    @Test
    public void testUseHealthRune_rune2_increasesHealthBy50Percent() {
        PlayerState player = new PlayerState("Test", 100, 0);
        player.setPlayerCurrentHealth(40);
        ItemUtils.useHealthRune(player, 2);
        assertEquals(90, player.getHealth()); // 40 + 50% of 100 = 90
    }

    @Test
    public void testUseExperienceBook_volume1() {
        int[] result = ItemUtils.useExperienceBook(1, 1, 0);
        assertEquals(1, result[0]); // Should still be level 1 (less than maxExp)
    }

    @Test
    public void testUseExperienceBook_volume3_levelUp() {
        int[] result = ItemUtils.useExperienceBook(3, 1, 0);
        int expectedLevel = (SkillUtils.getSkillExpGainedForVolume(3) >= SkillUtils.getMaxExperience(1)) ? 2 : 1;
        assertEquals(expectedLevel, result[0]); // Should level up if enough experience gained
    }

    @Test
    public void testUseSkillStone_addsNewSkill() {
        PlayerState player = new PlayerState("Test", 100, 0);
        ItemUtils.useSkillStone(SkillUtils.AnomalyTypes.LIFE, player);

        int totalSkills = player.getSkillList().length;
        assertEquals(1, totalSkills); // Should have 1 skill after use
    }

    @Test
    public void testUseSkillStone_whenPlayerHasAllSkills() {
        PlayerState player = new PlayerState("Test", 100, 0);
        int[] lifeSkills = SkillUtils.getSkillsByType(SkillUtils.AnomalyTypes.LIFE);
        for (int skillId : lifeSkills) {
            player.addSkill(skillId, 1, 0);
        }

        ItemUtils.useSkillStone(SkillUtils.AnomalyTypes.LIFE, player);
        int totalSkills = player.getSkillList().length;
        assertEquals(lifeSkills.length, totalSkills); // No new skill added
    }
}