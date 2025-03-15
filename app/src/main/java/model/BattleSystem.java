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
     * + getAffecetdTiles(skillName: String)
     * - didAtkHit(coords: int[][])
     * + getAvailableMoveTiles()
     * + updatePlayerPos(coords: int[][])
     * + usePotion()
     * + changeTurn()
     * + isWinner(), DONE
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
