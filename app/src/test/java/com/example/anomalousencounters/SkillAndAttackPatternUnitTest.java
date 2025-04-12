package com.example.anomalousencounters;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import model.AttackPattern;
import model.PlayerState;
import model.Skill;

public class SkillAndAttackPatternUnitTest {

    @Test
    public void canUseSkill_isCorrect() {
        Skill skill = new Skill("Blackhole", "STRAIGHT", 1, 4, 1, false);
        assertTrue(skill.canUseSkill());
    }

    @Test
    public void activateSkillCooldown_isCorrect() {
        Skill skill = new Skill("Blackhole", "STRAIGHT", 1, 4, 1, false);
        skill.activateSkillCooldown();
        assertFalse(skill.canUseSkill());
    }

    @Test
    public void getCurrentCooldown_isCorrect() {
        Skill skill = new Skill("Blackhole", "STRAIGHT", 1, 4, 1, false);
        skill.activateSkillCooldown();
        assertEquals(2, skill.getCurrentCooldown());
    }

    @Test
    public void canUpdateSkillCooldown_isCorrect() {
        Skill skill = new Skill("Blackhole", "STRAIGHT", 1, 4, 1, false);
        skill.activateSkillCooldown();
        skill.updateSkillCooldown();
        skill.updateSkillCooldown();
        assertTrue(skill.canUseSkill());
    }

    @Test
    public void getName_isCorrect() {
        String name = "Blackhole";
        Skill skill = new Skill(name, "STRAIGHT", 1, 4, 1, false);
        assertEquals(name, skill.getName());
    }

    @Test
    public void getAtkType_isCorrect() {
        Skill skill = new Skill("Blackhole", "DIAGONAL", 1, 4, 1, false);
        AttackPattern.AttackType type = AttackPattern.AttackType.DIAGONAL;
        assertEquals(type, skill.getAtkType());
    }

    @Test
    public void getTimesUsed_isCorrect() {
        Skill skill = new Skill("Blackhole", "DIAGONAL", 1, 4, 1, false);
        assertEquals(0, skill.getTimesUsed());

        skill.activateSkillCooldown();
        assertEquals(1, skill.getTimesUsed());
    }

    @Test
    public void getId_isCorrect() {
        int id = 3;
        Skill skill = new Skill("Blackhole", "DIAGONAL", 1, 4, id, false);
        assertEquals(id, skill.getId());
    }

    @Test
    public void getDamage_isCorrect() {
        int totalTrials = 9999;

        // track the distribution of damage since there is a random factor, damage from 1 + (4-5), 5-6
        Map<Integer, Integer> distribution = new HashMap<>();
        distribution.put(5, 0);
        distribution.put(6, 0);

        for (int i = 0; i < totalTrials; i++) {
            Skill skill = new Skill("Blackhole", "STRAIGHT", 1, 4, 1, false);
            int damage = skill.getDamage();

            distribution.put(damage, distribution.get(damage) + 1);
        }

        Map<Integer, Double> expectedProbabilities = new HashMap<>();
        expectedProbabilities.put(5, 0.5);
        expectedProbabilities.put(6, 0.5);

        int[] damadges = {5, 6};
        for (int damage: damadges) {
            double actualProbability = (double) distribution.get(damage) / totalTrials;
            double expectedProbability = expectedProbabilities.get(damage);

            double allowedDeviation = 0.05;

            assertTrue(Math.abs(actualProbability - expectedProbability) < allowedDeviation);
        }
    }

    @Test
    public void getAffectedTiles_isCorrect() {
        Skill skill = new Skill("Blackhole", "STRAIGHT", 1, 4, 1, false);
        int[] origin_pos = {2, 3};
        ArrayList<int[]> attackPatternOutput = skill.getAffectedTiles(origin_pos);


        ArrayList<int[]> expectedAtkPatternOutput = new ArrayList<>();
        expectedAtkPatternOutput.add(new int[]{2, 2});
        expectedAtkPatternOutput.add(new int[]{2, 1});
        expectedAtkPatternOutput.add(new int[]{2, 4});
        expectedAtkPatternOutput.add(new int[]{2, 5});
        expectedAtkPatternOutput.add(new int[]{3, 3});
        expectedAtkPatternOutput.add(new int[]{4, 3});
        expectedAtkPatternOutput.add(new int[]{1, 3});
        expectedAtkPatternOutput.add(new int[]{0, 3});

        // check that all expected patterns are in the actual output
        for (int[] expected : expectedAtkPatternOutput) {
            boolean found = false;
            for (int[] actual : attackPatternOutput) {
                if (Arrays.equals(expected, actual)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Expected pattern not found: " + Arrays.toString(expected), found);
        }
    }

    @Test
    public void getAttackPatterStraight_isCorrect() {
        AttackPattern attackPattern = new AttackPattern(AttackPattern.AttackType.STRAIGHT);
        int[] origin_pos = {2, 3};
        ArrayList<int[]> attackPatternOutput = attackPattern.getAttackPattern(origin_pos, 2);
        ArrayList<int[]> expectedAtkPatternOutput = new ArrayList<>();
        expectedAtkPatternOutput.add(new int[]{2, 2});
        expectedAtkPatternOutput.add(new int[]{2, 1});
        expectedAtkPatternOutput.add(new int[]{2, 4});
        expectedAtkPatternOutput.add(new int[]{2, 5});
        expectedAtkPatternOutput.add(new int[]{3, 3});
        expectedAtkPatternOutput.add(new int[]{4, 3});
        expectedAtkPatternOutput.add(new int[]{1, 3});
        expectedAtkPatternOutput.add(new int[]{0, 3});

        // check that all expected patterns are in the actual output
        for (int[] expected : expectedAtkPatternOutput) {
            boolean found = false;
            for (int[] actual : attackPatternOutput) {
                if (Arrays.equals(expected, actual)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Expected pattern not found: " + Arrays.toString(expected), found);
        }
    }

    @Test
    public void getAttackPatterDiagonal_isCorrect() {
        AttackPattern attackPattern = new AttackPattern(AttackPattern.AttackType.DIAGONAL);
        int[] origin_pos = {2, 3};
        ArrayList<int[]> attackPatternOutput = attackPattern.getAttackPattern(origin_pos, 2);
        ArrayList<int[]> expectedAtkPatternOutput = new ArrayList<>();
        expectedAtkPatternOutput.add(new int[]{1, 2});
        expectedAtkPatternOutput.add(new int[]{0, 1});

        expectedAtkPatternOutput.add(new int[]{3, 2});
        expectedAtkPatternOutput.add(new int[]{4, 1});

        expectedAtkPatternOutput.add(new int[]{1, 4});
        expectedAtkPatternOutput.add(new int[]{0, 5});

        expectedAtkPatternOutput.add(new int[]{3, 4});
        expectedAtkPatternOutput.add(new int[]{4, 5});

        // check that all expected patterns are in the actual output
        for (int[] expected : expectedAtkPatternOutput) {
            boolean found = false;
            for (int[] actual : attackPatternOutput) {
                if (Arrays.equals(expected, actual)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Expected pattern not found: " + Arrays.toString(expected), found);
        }
    }

    @Test
    public void getAttackPatterStar_isCorrect() {
        AttackPattern attackPattern = new AttackPattern(AttackPattern.AttackType.STAR);
        int[] origin_pos = {2, 3};
        ArrayList<int[]> attackPatternOutput = attackPattern.getAttackPattern(origin_pos, 2);
        ArrayList<int[]> expectedAtkPatternOutput = new ArrayList<>();
        expectedAtkPatternOutput.add(new int[]{2, 2});
        expectedAtkPatternOutput.add(new int[]{2, 1});
        expectedAtkPatternOutput.add(new int[]{2, 4});
        expectedAtkPatternOutput.add(new int[]{2, 5});
        expectedAtkPatternOutput.add(new int[]{3, 3});
        expectedAtkPatternOutput.add(new int[]{4, 3});
        expectedAtkPatternOutput.add(new int[]{1, 3});
        expectedAtkPatternOutput.add(new int[]{0, 3});
        expectedAtkPatternOutput.add(new int[]{1, 2});
        expectedAtkPatternOutput.add(new int[]{0, 1});
        expectedAtkPatternOutput.add(new int[]{3, 2});
        expectedAtkPatternOutput.add(new int[]{4, 1});
        expectedAtkPatternOutput.add(new int[]{1, 4});
        expectedAtkPatternOutput.add(new int[]{0, 5});
        expectedAtkPatternOutput.add(new int[]{3, 4});
        expectedAtkPatternOutput.add(new int[]{4, 5});

        // check that all expected patterns are in the actual output
        for (int[] expected : expectedAtkPatternOutput) {
            boolean found = false;
            for (int[] actual : attackPatternOutput) {
                if (Arrays.equals(expected, actual)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Expected pattern not found: " + Arrays.toString(expected), found);
        }
    }

    @Test
    public void getAttackPatterHourglass_isCorrect() {
        AttackPattern attackPattern = new AttackPattern(AttackPattern.AttackType.HOURGLASS);
        int[] origin_pos = {2, 3};
        ArrayList<int[]> attackPatternOutput = attackPattern.getAttackPattern(origin_pos, 1);
        ArrayList<int[]> expectedAtkPatternOutput = new ArrayList<>();
        expectedAtkPatternOutput.add(new int[]{1, 4});
        expectedAtkPatternOutput.add(new int[]{3, 2});

        expectedAtkPatternOutput.add(new int[]{2, 4});
        expectedAtkPatternOutput.add(new int[]{2, 2});

        expectedAtkPatternOutput.add(new int[]{3, 4});
        expectedAtkPatternOutput.add(new int[]{1, 2});

        // check that all expected patterns are in the actual output
        for (int[] expected : expectedAtkPatternOutput) {
            boolean found = false;
            for (int[] actual : attackPatternOutput) {
                if (Arrays.equals(expected, actual)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Expected pattern not found: " + Arrays.toString(expected), found);
        }
    }

    @Test
    public void getAttackPatterButterfly_isCorrect() {
        AttackPattern attackPattern = new AttackPattern(AttackPattern.AttackType.BUTTERFLY);
        int[] origin_pos = {2, 3};
        ArrayList<int[]> attackPatternOutput = attackPattern.getAttackPattern(origin_pos, 1);
        ArrayList<int[]> expectedAtkPatternOutput = new ArrayList<>();
        // int[][] positionVectors = {{-1, 0}, {1, 0}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        expectedAtkPatternOutput.add(new int[]{1, 3});
        expectedAtkPatternOutput.add(new int[]{3, 3});

        expectedAtkPatternOutput.add(new int[]{1, 2});
        expectedAtkPatternOutput.add(new int[]{1, 4});

        expectedAtkPatternOutput.add(new int[]{3, 2});
        expectedAtkPatternOutput.add(new int[]{3, 4});

        // check that all expected patterns are in the actual output
        for (int[] expected : expectedAtkPatternOutput) {
            boolean found = false;
            for (int[] actual : attackPatternOutput) {
                if (Arrays.equals(expected, actual)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Expected pattern not found: " + Arrays.toString(expected), found);
        }
    }

}