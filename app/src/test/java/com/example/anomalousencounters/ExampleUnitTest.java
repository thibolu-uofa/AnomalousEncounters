package com.example.anomalousencounters;

import org.junit.Test;

import static org.junit.Assert.*;

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
}