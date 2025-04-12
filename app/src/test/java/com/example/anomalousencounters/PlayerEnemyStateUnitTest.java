package com.example.anomalousencounters;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import model.EnemyState;
import model.PlayerState;
import model.Skill;

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
    public void setPlayerCurrentHealthIsCorrect() {
        int maxHealth = 50;
        PlayerState player = new PlayerState("Nxy", maxHealth, 20);

        int newCurrentHealth = 3;
        player.setPlayerCurrentHealth(newCurrentHealth);

        assertEquals(newCurrentHealth, player.getHealth());
    }

    @Test
    public void getItemListIsCorrect(){
        PlayerState playerState = new PlayerState();
        playerState.addItem(0, 2);
        assertArrayEquals(new int[]{0}, playerState.getItemList());

    }

    @Test
    public void getItemAmountsListIsCorrect(){
        PlayerState playerState = new PlayerState();
        playerState.addItem(0, 3);
        assertArrayEquals(new int[]{3}, playerState.getItemAmountsList());
    }

    @Test
    public void addItemIsCorrect() {
        PlayerState player = new PlayerState();
        player.addItem(1, 2);
        assertArrayEquals(new int[]{1}, player.getItemList());
        assertArrayEquals(new int[]{2}, player.getItemAmountsList());
    }

    @Test
    public void removeItemIsCorrect() {
        PlayerState player = new PlayerState();
        player.addItem(1, 2);
        player.addItem(2, 1);
        player.addItem(3, 4);
        player.removeItem(2);

        int[] itemId = {1, 3};
        int[] playerItemIds =  player.getItemList();

        assertArrayEquals(itemId, playerItemIds);

    }

    @Test
    public void getPlayerItemssIsCorrect() {
        PlayerState player = new PlayerState();
        player.addItem(1, 2);
        player.addItem(2, 10);
        List<int[]> items = new ArrayList<>();
        items.add(new int[]{1, 2});
        items.add(new int[]{2, 10});

        for (int i = 0; i < items.size(); i++) {
            assertArrayEquals(items.get(i), player.getItems().get(i));
        }
    }

    @Test
    public void getSkillListIsCorrect() {
        PlayerState player = new PlayerState();
        player.addSkill(4, 2, 0);
        player.addSkill(2, 2, 0);
        player.addSkill(7, 2, 0);

        int[] skillIds = {4, 2, 7};
        assertArrayEquals(skillIds, player.getSkillList());
    }

    @Test
    public void getSkillLevelsIsCorrect() {
        PlayerState player = new PlayerState();
        player.addSkill(4, 10, 0);
        player.addSkill(2, 5, 0);
        player.addSkill(7, 4, 0);

        int[] skillLevels = {10, 5, 4};
        assertArrayEquals(skillLevels, player.getSkillLevels());
    }

    @Test
    public void addAndRemoveSkillIsCorrect() {
        PlayerState player = new PlayerState();
        player.addSkill(1, 2, 0);
        assertArrayEquals(new int[]{1}, player.getSkillList());
        assertArrayEquals(new int[]{2}, player.getSkillLevels());
    }

    @Test
    public void getPlayerSkillsIsCorrect() {
        PlayerState player = new PlayerState();
        player.addSkill(1, 2, 0);
        player.addSkill(2, 3, 10);
        List<int[]> skills = new ArrayList<>();
        skills.add(new int[]{1, 2, 0});
        skills.add(new int[]{2, 3, 10});

        for (int i = 0; i < skills.size(); i++) {
            assertArrayEquals(skills.get(i), player.getSkills().get(i));
        }
    }

    @Test
    public void addSkillPlayerAlreadyHasIsCorrect() {
        PlayerState player = new PlayerState();
        player.addSkill(1, 2, 0);
        int[] skills = player.getSkillList();

        player.addSkill(1, 2, 0);
        assertEquals(skills.length, player.getSkillList().length);
    }

    @Test
    public void getSkillLevelIsCorrect() {
        PlayerState player = new PlayerState();

        int id = 1;
        int level = 2;
        player.addSkill(id, level, 0);

        assertEquals(level, player.getLevelOfSkill(id));
    }

    @Test
    public void getSkillExpIsCorrect() {
        PlayerState player = new PlayerState();

        int id = 1;
        int exp = 250;
        player.addSkill(id, 1, exp);

        assertEquals(exp, player.getExperienceOfSkill(id));
    }

    @Test
    public void setSkillLevelAndExpIsCorrect() {
        PlayerState player = new PlayerState();

        int id = 1;
        player.addSkill(id, 2, 0);

        int newSkillLevel = 5;
        int newExp = 100;
        player.setSkillLevelAndExperience(id, newSkillLevel, newExp);

        assertEquals(newSkillLevel, player.getLevelOfSkill(id));
        assertEquals(newExp, player.getExperienceOfSkill(id));
    }

    @Test
    public void removeSkillIsCorrect() {
        PlayerState player = new PlayerState();
        player.addSkill(1, 2, 0);
        player.removeSkill(1);
        assertArrayEquals(new int[]{}, player.getSkillList());
    }

    @Test
    public void getTotalSkillLevelIsCorrect() {
        PlayerState player = new PlayerState();
        player.addSkill(1, 1, 0);
        player.addSkill(2, 2, 0);
        player.addSkill(3, 3, 0);
        player.addSkill(4, 2, 0);

        assertEquals(8, player.getTotalSkillLevel());
    }

    @Test
    public void getTokensLostOnDeathIsCorrect() {
        PlayerState player = new PlayerState("Max", 25, 0);
        player.addSkill(1, 2, 0);

        assertEquals(2, player.getTokensLostOnDeath());
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
    public void getPlayerNameIsCorrect() {
        PlayerState playerState = new PlayerState("Gen", 80, 1);
        assertEquals("Gen", playerState.getName());
    }

    @Test
    public void getPhaseIsCorrect() {
        PlayerState playerState = new PlayerState();
        assertEquals(1, playerState.getPhase());
    }

    @Test
    public void setPhaseIsCorrect() {
        PlayerState playerState = new PlayerState();
        playerState.setPhase(3);
        assertEquals(3, playerState.getPhase());
    }

    @Test
    public void getEnemyNameIsCorrect() {
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

