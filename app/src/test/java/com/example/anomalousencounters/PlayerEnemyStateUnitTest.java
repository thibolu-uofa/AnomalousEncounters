package com.example.anomalousencounters;

import org.junit.Test;

import static org.junit.Assert.*;

import model.EnemyState;
import model.PlayerState;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PlayerEnemyStateUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void playerStateGetMaxHealth_isCorrect() {
        PlayerState playerState = new PlayerState("Nxy", 50, 20);
        assertEquals(50, playerState.getPlayerMaxHealth());

    }

    @Test
    public void modifyHealthWithNegativeDeltaIsCorrect() {
        int maxHealth = 50;
        PlayerState player = new PlayerState("Nxy", 50, 20);
        int delta = -20;
        player.modifyHealth(delta);

        double DAMAGE_REDUCTION_PERCENTAGE = 0.2;
        int newHealth =  maxHealth + (int) (delta * (1 - DAMAGE_REDUCTION_PERCENTAGE));

        assertEquals(newHealth, player.getHealth());
    }

    @Test
    public void modifyHealthWithPositiveDeltaIsCorrect() {
        PlayerState player = new PlayerState("Nxy", 50, 20);
        int delta = -20;
        player.modifyHealth(delta);

        int delta2 = 10;
        int currentHealth = player.getHealth();
        player.modifyHealth(delta2);

        int newHealth = currentHealth + delta2;

        assertEquals(newHealth, player.getHealth());
    }

    @Test
    public void modifyHealthWithNegativeOverflowIsCorrect() {
        PlayerState player = new PlayerState("Nxy", 50, 20);

        player.modifyHealth(-200);
        assertEquals(0, player.getHealth());
    }

    @Test
    public void modifyHealthWithPositiveOverflowIsCorrect() {
        int maxHealth = 50;
        PlayerState player = new PlayerState("Nxy", maxHealth, 20);
        player.modifyHealth(-20);

        player.modifyHealth(200);
        assertEquals(maxHealth, player.getHealth());
    }

    @Test
    public void addAndRemoveItemIsCorrect() {
        PlayerState player = new PlayerState();
        player.addItem(1, 2);
        assertArrayEquals(new int[]{1}, player.getItemList());
        assertArrayEquals(new int[]{2}, player.getItemAmountsList());

        player.addItem(1, 3);
        assertArrayEquals(new int[]{1}, player.getItemList());
        assertArrayEquals(new int[]{3}, player.getItemAmountsList());

        player.removeItem(1);
        assertArrayEquals(new int[]{1}, player.getItemList());
        assertArrayEquals(new int[]{2}, player.getItemAmountsList());
    }

    @Test
    public void addAndRemoveSkillIsCorrect() {
        PlayerState player = new PlayerState();
        player.addSkill(1, 2, 0);
        assertArrayEquals(new int[]{1}, player.getSkillList());
        assertArrayEquals(new int[]{2}, player.getSkillLevels());
    }

    @Test
    public void removeSkillIsCorrect() {
        PlayerState player = new PlayerState();
        player.addSkill(1, 2, 0);
        player.removeSkill(1);
        assertArrayEquals(new int[]{}, player.getSkillList());
    }

    @Test
    public void getTokensIsCorrect() {
        int tokens = 5;
        PlayerState player = new PlayerState("Ben", 50, tokens);
        assertEquals(tokens, player.getTokens());
    }

    @Test
    public void updateTokensIsCorrect() {
        PlayerState player = new PlayerState();
        int tokens = player.getTokens();
        int delta = -5;
        player.updateTokens(delta);

        assertEquals(tokens + delta, player.getTokens());
    }

    @Test
    public void updateTokensNegOverflowIsCorrect() {
        PlayerState player = new PlayerState();
        int tokens = player.getTokens();
        int delta = -(tokens + 50);
        player.updateTokens(delta);

        assertEquals(0, player.getTokens());
    }

    @Test
    public void canUpdateTokensIsCorrect() {
        PlayerState player = new PlayerState();
        player.updateTokens(20);
        int delta = -5;
        assertTrue(player.canUpdateTokens(delta));
    }

    @Test
    public void canUpdateTokensWithNegOverflowIsCorrect() {
        PlayerState player = new PlayerState();
        int tokens = player.getTokens();
        int delta = -(tokens + 50);

        assertFalse(player.canUpdateTokens(delta));
    }


    @Test
    public void modifyEnemyHealthIsCorrect() {
        EnemyState enemy = new EnemyState();
        enemy.modifyHealth(-20);
        assertEquals(80, enemy.getHealth());
        enemy.modifyHealth(50);
        assertEquals(100, enemy.getHealth());
        enemy.modifyHealth(-200);
        assertEquals(0, enemy.getHealth());
    }

    @Test
    public void getSkillListIsCorrect() {
        EnemyState enemy = new EnemyState();
        assertArrayEquals(new int[]{}, enemy.getSkillList());
    }

    @Test
    public void getNameIsCorrect() {
        EnemyState enemy = new EnemyState("Goblin", 80, 1, 1);
        assertEquals("Goblin", enemy.getName());
    }

    @Test
    public void getEnemyMaxHealthIsCorrect() {
        EnemyState enemy = new EnemyState("Orc", 120, 2, 1);
        assertEquals(120, enemy.getEnemyMaxHealth());
    }

    @Test
    public void getEnemyCurrentHealthIsCorrect() {
        EnemyState enemy = new EnemyState("Troll", 150, 3, 1);
        assertEquals(150, enemy.getEnemyCurrentHealth());
    }

    @Test
    public void getIdIsCorrect() {
        EnemyState enemy = new EnemyState("Dragon", 200, 99, 1);
        assertEquals(99, enemy.getId());
    }
}

