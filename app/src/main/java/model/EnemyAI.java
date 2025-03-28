package model;

import java.util.ArrayList;

public class EnemyAI {
    BattleSystem battleSystem;
    private final ArrayList<Skill> enemySkills;
    Skill chosenSkill;
    int[] newPosition;
    public enum Action {
        ATTACK,
        MOVE_ATTACK,
        MOVE,
    }

    public EnemyAI(ArrayList<Skill> enemySkills, BattleSystem battleSystem) {
        this.enemySkills = enemySkills;
        this.battleSystem = battleSystem;
    }

    public void executeEnemyTurn() {
        Action action = selectOptimalAction();
        executeAction(action);
    }

    private void executeAction(Action action) {
        switch (action) {
            case ATTACK:
                battleSystem.useEnemySkill(chosenSkill);
                break;
            case MOVE_ATTACK:
                battleSystem.updateEnemyPos(newPosition);
                battleSystem.useEnemySkill(chosenSkill);
                break;
            case MOVE:
                battleSystem.updateEnemyPos(newPosition);
                break;
        }
        battleSystem.endEnemyTurn();
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
        findMoveClosestToPlayer();
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
    private void findMoveClosestToPlayer() {
        int minDistance = Integer.MAX_VALUE;
        ArrayList<int[]> availableNewPositions = battleSystem.getAvailableMoveTilesForEnemy();
        for (int[] newPosition: availableNewPositions) {
            double distance = calculateDistance(newPosition, battleSystem.getPlayerPosition());
            if (distance < minDistance) {
                this.newPosition = newPosition;
            }
        }
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

}
