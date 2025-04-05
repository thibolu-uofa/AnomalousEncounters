package model;

import java.util.ArrayList;
import java.util.Random;

import presenter.GamePresenter;

public class EnemyAI {
    private final GamePresenter presenter;
    private final BattleSystem battleSystem;
    private final ArrayList<Skill> enemySkills;
    private Skill chosenSkill;
    private int[] newPosition;
    public enum Action {
        ATTACK,
        MOVE_ATTACK,
        MOVE,
    }
    private int[][] moveVector;
    private final int[][] eightDimensionalMoveVectors = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    private final int[][] straightMoveVectors = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    private final int[][] diagonalMoveVectors = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    public EnemyAI(ArrayList<Skill> enemySkills, BattleSystem battleSystem, GamePresenter presenter) {
        this.enemySkills = enemySkills;
        this.battleSystem = battleSystem;
        this.presenter = presenter;
        setEnemyMoveVector(enemySkills);
    }

    private void setEnemyMoveVector(ArrayList<Skill> enemySkills) {
        // First pick a random skill of the enemy
        Random rand = new Random();
        int randIndex = rand.nextInt(enemySkills.size());
        Skill randomSkill = enemySkills.get(randIndex);
        AttackPattern.AttackType type = randomSkill.getAtkType();
        switch (type) {
            case STRAIGHT:
                moveVector = straightMoveVectors;
                break;
            case STAR:
                moveVector = eightDimensionalMoveVectors;
                break;
            case HOURGLASS:
            case BUTTERFLY:
            case DIAGONAL:
                moveVector = diagonalMoveVectors;
                break;
        }
    }

    public void executeEnemyTurn() {
        Action action = selectOptimalAction();
        executeAction(action);
    }

    private void executeAction(Action action) {
        switch (action) {
            case ATTACK:
                presenter.enemyChoseSkill(chosenSkill);
                break;
            case MOVE_ATTACK:
                battleSystem.updateEnemyPos(newPosition);
                presenter.enemyChoseSkill(chosenSkill);
                break;
            case MOVE:
                battleSystem.updateEnemyPos(newPosition);
                presenter.endEnemyTurn();
                break;
        }
    }

    private Action selectOptimalAction() {
        // Best case: check if any attack can reach player, if so attack the player
        int[] enemyPosition = battleSystem.getEnemyPosition();
        if (canAttackPlayer(enemyPosition)) {
            return Action.ATTACK;
        }

        // Second best case: check if enemy can move to attack player, if so move then attack player
        if (canMoveToAttackPosition()) {
            return Action.MOVE_ATTACK;
        }

        // Worse Case: enemy just moves closer to the player
        findBestTileToMoveTowards();
        return Action.MOVE;
    }

    private boolean canAttackPlayer(int[] enemyPosition) {
        int maxDamage = 0;
        for (Skill skill: enemySkills) {
            if (canSkillReachPlayer(skill, enemyPosition) && skill.getDamage() > maxDamage && skill.canUseSkill()) {
                chosenSkill = skill;
                return true;
            }
        }
        return false;
    }

    private boolean canSkillReachPlayer(Skill skill, int[] enemyPosition) {
        ArrayList<int[]> affectedTiles = skill.getAffectedTiles(enemyPosition);
        return battleSystem.didAtkHit(affectedTiles);
    }

    private boolean canMoveToAttackPosition() {
        ArrayList<int[]> availableNewPositions = battleSystem.getAvailableMoveTilesForEnemy(moveVector);
        for (int[] newPosition: availableNewPositions) {
            if (canAttackPlayer(newPosition) && battleSystem.isMoveValid(newPosition, availableNewPositions)) {
                this.newPosition = newPosition;
                return true;
            }
        }
        return false;
    }

    private void findStrategicMove() {
        double minDistance = Integer.MAX_VALUE;
        ArrayList<int[]> availableNewPositions = battleSystem.getAvailableMoveTilesForEnemy(moveVector);
        availableNewPositions.add(battleSystem.getEnemyPosition());  // can also choose to stay in place
        for (int[] newPosition: availableNewPositions) {
            double distance = calculateDistance(newPosition, battleSystem.getPlayerPosition());

            boolean achievesMinDistance = distance < minDistance;
            boolean isPositionInLineWithAtLeastOneSkill = isPositionInLineWithSkill(newPosition);
            boolean isMoveValid = battleSystem.isMoveValid(newPosition, availableNewPositions);

            if (achievesMinDistance && isPositionInLineWithAtLeastOneSkill && isMoveValid) {
                minDistance = distance;
                this.newPosition = newPosition;
            }
        }
    }

    private void findBestTileToMoveTowards() {
        ArrayList<int[]> validPotentialTargetTiles = new ArrayList<>();
        int maxRows = battleSystem.getMaxRows();
        int maxCols = battleSystem.getMaxCols();

        // go through every tile and check if the enemy was on that tile would they be able to hit the player
        for (int row = 0; row < maxRows; row++) {
            for (int col = 0; col < maxCols; col++) {
                int[] tile = new int[]{col, row};

                boolean isTileValid = battleSystem.isSingleMoveValid(tile);
                // only consider a tile that is valid
                if (!isTileValid) {
                    continue;
                }

                for (Skill skill: enemySkills) {
                    if (canSkillReachPlayer(skill, tile)) {
                        validPotentialTargetTiles.add(tile);
                        break;
                    }
                }
            }
        }

        //TODO: What if they are same distance from enemy
        //TODO: What if no valid tile is found, currently assigning 0,0, maybe just move towards player??

        // Find the closest target tile to the current enemy position
        int[] enemyPosition = battleSystem.getEnemyPosition();
        double minDistance = Double.MAX_VALUE;
        int[] closestTargetTile = {0, 0};

        for (int[] tile : validPotentialTargetTiles) {
            double distance = calculateDistance(enemyPosition, tile);
            if (distance < minDistance) {
                minDistance = distance;
                closestTargetTile = tile;
            }
        }


        minDistance = Integer.MAX_VALUE;
        ArrayList<int[]> availableNewPositions = battleSystem.getAvailableMoveTilesForEnemy(moveVector);
        for (int[] newPosition: availableNewPositions) {
            double distance = calculateDistance(newPosition, closestTargetTile);
            boolean achievesMinDistance = distance < minDistance;
            boolean isMoveValid = battleSystem.isMoveValid(newPosition, availableNewPositions);

            if (achievesMinDistance && isMoveValid) {
                minDistance = distance;
                this.newPosition = newPosition;
            }
        }
    }


    private boolean isPositionInLineWithSkill(int[] newPosition) {
        for (Skill skill: enemySkills) {
            if (canSkillReachPlayerInFuture(skill, newPosition)) {
                return true;
            }
        }
        return false;
    }

    private boolean canSkillReachPlayerInFuture(Skill skill, int[] enemyPosition) {
        ArrayList<int[]> affectedTiles = skill.getAffectedTilesForMaxDistance(enemyPosition);
        return battleSystem.didAtkHit(affectedTiles);
    }

    //Source: https://www.baeldung.com/java-distance-between-two-points
    private double calculateDistance(int[] startPosition, int[] endPosition) {
        int x1 = startPosition[0];
        int y1 = startPosition[1];
        int x2 = endPosition[0];
        int y2 = endPosition[1];

        int ac = Math.abs(y2 - y1);
        int cb = Math.abs(x2 - x1);

        return Math.hypot(ac, cb);
    }

    public Skill getChosenSkill() {
        return chosenSkill;
    }
}
