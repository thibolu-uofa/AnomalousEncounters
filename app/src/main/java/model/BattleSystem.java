package model;

import java.util.List;

import presenter.GamePresenter;

/**
 * - presenter: GamePresenter
 * - enemyState: EnemyState
 * - playerState: PlayerState
 * - playerSkills: List<Skill>
 * - enemySkills: List<Skill>
 * - CurrentAction: enum
 */
public class BattleSystem {
    private GamePresenter presenter;

    private EnemyState enemyState;
    private PlayerState playerState;
    private List<Skill> playerSkill;
    private List<Skill> enemySkills;
    private CurrentAction currentAction;
    private enum CurrentAction{
        MOVE,
        ATTACK,
        USE
    }
    //public PlayerState BattleSystem(){

   // }
    /*
    public presenter updateCurrentBattleAction(String action){

    }
    */
    /*
    public  selectPlayerSkill(String skill){

    }
    public Boolean didAtkHit(coords: int[][]){

    }
     */
    /**
     * + BattleSystem(playerState: PlayerState)
     * - createEnemyFromId()
     * - populateSkills()
     * + getPlayerSkills()
     * + updateCurrentBattleAction(action: String)
     * + selectPlayerSkill(skillName: String)
     * + usePlayerSkill(skillname: String)

     * - didAtkHit(coords: int[][])

     * + updatePlayerPos(coords: int[][])
     * + usePotion()
     * + changeTurn()
     *
     */
    /**
     * + getAvailableMoveTiles()
     * + getAffecetdTiles(skillName: String)
     * updateCurrentBattleAction(action: String)
     *///
    public void updateCurrentBattleAction(String action) {
        switch (action.toUpperCase()) {
            case "MOVE":
                this.currentAction = CurrentAction.MOVE;
                break;
            case "ATTACK":
                this.currentAction = CurrentAction.ATTACK;
                break;
            case "USE":
                this.currentAction = CurrentAction.USE;
                break;
            default:
                System.out.println("Invalid action");
        }
    }
   /* public boolean didAtkHit(int[][] coords) {
        return enemyState.isInPosition(coords);
    }
*/
public boolean isPlayerWinner(){
    if (enemyState.getHealth() == 0) {
        return true;
    }
    return false;
}
public boolean isPlayerLoser(){
    if (playerState.getHealth() == 0){
        return true;
    }
    return false;//
}


}
