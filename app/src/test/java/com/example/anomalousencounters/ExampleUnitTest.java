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
public class ExampleUnitTest {
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
    public void modifyHealthIsCorrect() {
        PlayerState player = new PlayerState();
        player.modifyHealth(-20);
        assertEquals(80, player.getHealth());
        player.modifyHealth(50);
        assertEquals(100, player.getHealth());
        player.modifyHealth(-200);
        assertEquals(0, player.getHealth());
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
        player.addSkill(1, 2);
        assertArrayEquals(new int[]{1}, player.getSkillList());
        assertArrayEquals(new int[]{2}, player.getSkillLevels());

        player.removeSkill(1);
        assertArrayEquals(new int[]{}, player.getSkillList());
    }
    @Test
    public void updateTokensIsCorrect() {
        PlayerState player = new PlayerState();
        assertTrue(player.canUpdateTokens(-10));
        player.updateTokens(-10);
        assertEquals(10, player.getTokens());

        assertFalse(player.canUpdateTokens(-15));
        player.updateTokens(-15);
        assertEquals(10, player.getTokens()); // Should not change since it would go negative
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
        EnemyState enemy = new EnemyState("Goblin", 80, 1);
        assertEquals("Goblin", enemy.getName());
    }

    @Test
    public void getEnemyMaxHealthIsCorrect() {
        EnemyState enemy = new EnemyState("Orc", 120, 2);
        assertEquals(120, enemy.getEnemyMaxHealth());
    }

    @Test
    public void getEnemyCurrentHealthIsCorrect() {
        EnemyState enemy = new EnemyState("Troll", 150, 3);
        assertEquals(150, enemy.getEnemyCurrentHealth());
    }

    @Test
    public void getIdIsCorrect() {
        EnemyState enemy = new EnemyState("Dragon", 200, 99);
        assertEquals(99, enemy.getId());
    }
}

