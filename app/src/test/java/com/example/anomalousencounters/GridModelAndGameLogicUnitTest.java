package com.example.anomalousencounters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.GameLogic;
import model.GridModel;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GridModelAndGameLogicUnitTest {
    @Test
    public void getColumnCountIsCorrect(){
        GridModel gridModel = new GridModel();
        assertEquals(8, gridModel.getColumnCount());
    }

    @Test
    public void getRowCountIsCorrect(){
        GridModel gridModel = new GridModel();
        assertEquals(6, gridModel.getRowCount());
    }

    @Test
    public void isInHitBoxTrueIsCorrect(){
        GameLogic gameLogic = new GameLogic();
        assertTrue(gameLogic.isInHitbox(7, 6, 5, 8, 8, 4));
    }

    @Test
    public void isInHitBoxFalseIsCorrect(){
        GameLogic gameLogic = new GameLogic();
        assertFalse(gameLogic.isInHitbox(6, 7, 7, 8, 8, 4));
    }

    @Test
    public void getPlayerMovementStateRightIsCorrect(){
        GameLogic gameLogic = new GameLogic();
        assertEquals(gameLogic.getPlayerMovementState(7, 0, 6), "Right");
    }

    @Test
    public void getPlayerMovementStateLeftIsCorrect(){
        GameLogic gameLogic = new GameLogic();
        assertEquals(gameLogic.getPlayerMovementState(7, 9, 12), "Left");
    }

    @Test
    public void getPlayerMovementStateIdleIsCorrect(){
        GameLogic gameLogic = new GameLogic();
        assertEquals(gameLogic.getPlayerMovementState(7, 4, 8), "Idle");
    }
}