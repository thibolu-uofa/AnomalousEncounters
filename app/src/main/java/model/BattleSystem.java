package model;

import static model.Utils.getListOfDataProperty;
import static model.Utils.loadJsonArrayFromFile;

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
     * +
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
     */
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
    return false;
}

    public List<Skill> getEnemySkills() {
        return enemySkills;
    }

    private void populatePlayerSkills() {
        int[] skillIds = playerState.getSkillList();
        populateSkills(skillIds, playerSkill);
    }

    private void populateEnemySkills() {
        int[] skillIds = enemyState.getSkillList();
        populateSkills(skillIds, enemySkills);
    }

    private void populateSkills(int[] ids, List<Skill> skillList) {
        List<String> skillNames = getListOfDataProperty("skills.json", "name", ids, presenter.getBaseContext());
        List<String> skillAtkTypes = getListOfDataProperty("skills.json", "atkPattern", ids, presenter.getBaseContext());
        for(int i = 0; i <= ids.length; i++) {
            Skill skill = new Skill(skillNames.get(i), skillAtkTypes.get(i));
            skillList.add(skill);

        }
    }
}
