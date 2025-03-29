package model;

import java.util.ArrayList;

import presenter.GamePresenter;

public class EnemyAI {
    private GamePresenter presenter;
    private BattleSystem battleSystem;
    private final ArrayList<Skill> enemySkills;
    private Skill chosenSkill;
    private int[] newPosition;
    public enum Action {
        ATTACK,
        MOVE_ATTACK,
        MOVE,
    }

    public EnemyAI(ArrayList<Skill> enemySkills, BattleSystem battleSystem, GamePresenter presenter) {
        this.enemySkills = enemySkills;
        this.battleSystem = battleSystem;
        this.presenter = presenter;
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
        findStrategicMove();
        return Action.MOVE;
    }

    private boolean canAttackPlayer(int[] enemyPosition) {
        int maxDamage = 0;
        for (Skill skill: enemySkills) {
            if (canSkillReachPlayer(skill, enemyPosition) && skill.getDamage() > maxDamage) {
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
        ArrayList<int[]> availableNewPositions = battleSystem.getAvailableMoveTilesForEnemy();
        for (int[] newPosition: availableNewPositions) {
            if (canAttackPlayer(newPosition) && battleSystem.isMoveValid(newPosition, availableNewPositions)) {
                this.newPosition = newPosition;
                return true;
            }
        }
        return false;
    }

    //FIND MOVE CLOSEST, WHILE BEING A POSITION TO USE SKILL
    //SOMETIMES MOVING FURTHER AWAY
    private void findStrategicMove() {
        double minDistance = Integer.MAX_VALUE;
        ArrayList<int[]> availableNewPositions = battleSystem.getAvailableMoveTilesForEnemy();
        availableNewPositions.add(battleSystem.getEnemyPosition());
        for (int[] newPosition: availableNewPositions) {
            double distance = calculateDistance(newPosition, battleSystem.getPlayerPosition());
            if (distance < minDistance && isPositionInLineWithSkill(newPosition) && battleSystem.isMoveValid(newPosition, availableNewPositions)) {
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
