package model;
import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Random;

import presenter.GamePresenter;

public class EncounterSystem {
    private GamePresenter presenter;
    private final ArrayList<int[]> startAndEndPoints = new ArrayList<>();
    private final int ENCOUNTER_PROBABILITY = 4;
    private int[] enemyIds = {0, 1, 2};

    public EncounterSystem() {
        startAndEndPoints.add(new int[]{-927, -1172});
        startAndEndPoints.add(new int[]{-1826, -2003});
        startAndEndPoints.add(new int[]{-2610, -2810});
    }
    public boolean hasEncounteredEnemy(int x) {
        if (canEncounterEnemy(x)) {
            Random rand = new Random();
            int diceThrow = rand.nextInt(6) + 1;
//            Log.d("Dice Throw", String.valueOf(diceThrow));
            return ENCOUNTER_PROBABILITY >= diceThrow;
        }
        return false;
    }

    private boolean canEncounterEnemy(int x) {
        for (int[] startAndEndPoint: startAndEndPoints){
            if (abs(x) >= abs(startAndEndPoint[0]) && abs(x) <= abs(startAndEndPoint[1])){

               return true;
            }
//            Log.d("X Positions", startAndEndPoint[0] + " "  + x);
        }
        return false;
    }

    public int getRandomEnemyId() {
        Random rand = new Random();
        int randomEnemyId = rand.nextInt(enemyIds.length);
        return enemyIds[randomEnemyId];
    }

}
