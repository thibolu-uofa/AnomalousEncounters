package model;

import static model.Utils.getListOfDataProperty;
import static model.Utils.getRepeatingPattern;
import static model.Utils.getSingleDataProperty;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import presenter.GamePresenter;

public class BattleSystem {
    private GamePresenter presenter;
    private Context context;

    private EnemyState enemyState;
    private int[] enemyPosition = new int[2];
    private final PlayerState playerState;
    private int[] playerPosition = new int[2];
    private ArrayList<Skill> playerSkills = new ArrayList<>();
    private ArrayList<Skill> enemySkills = new ArrayList<>();
    private enum CurrentAction{
        MOVE,
        ATTACK,
        USE
    }
    private CurrentAction currentAction;
    private boolean isPlayerTurn = true;

    private GridModel gridModel;
    private int maxRows;
    private int maxCols;


    private static final Map<CurrentAction, Boolean> actionsPerformed = new HashMap<>() {{
        put(CurrentAction.MOVE, false);
        put(CurrentAction.ATTACK, false);
        put(CurrentAction.USE, false);
    }};

    public BattleSystem(PlayerState playerState, int enemyId, Context context) {
        this.playerState = playerState;
        this.context = context;
        populatePlayerSkills();
        populateEnemySkills(enemyId);//

        createEnemy(enemyId);

        gridModel = new GridModel();
        maxRows = gridModel.getRowCount();
        maxCols = gridModel.getColumnCount();

        //positions for testing purposes
        playerPosition = new int[]{3, 2};
        enemyPosition = new int[]{5, 4};

    }

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

    public void changeTurn() {
        isPlayerTurn = !isPlayerTurn;
    }

    public boolean didAtkHit(ArrayList<int[]> coords) {
        //if isPlayerTurn is true then, this function returns true if enemyPosition is equal to any coord in coords, false otherwise
        //if isPlayerTurn is false then, this function returns true if playerPosition is equal to coord in coords, false otherwise
        int[] targetPosition = isPlayerTurn ? enemyPosition : playerPosition;

        // Check if targetPosition is in the list of affected coordinates
        for (int[] coord : coords) {
            if (coord[0] == targetPosition[0] && coord[1] == targetPosition[1]) {
                return true;
            }
        }
        return false;
    }

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

    public ArrayList<int[]> getAffectedTilesForPlayer(String skillName) {
        // Get the skill from playerSkills
        Skill skill = getSkillByName(skillName, playerSkills);
        // Return the affected tiles using player position as the origin
        return skill.getAffectedTiles(playerPosition);
    }

    public ArrayList<int[]> getAffectedTilesForEnemy(String skillName) {
        // Get the skill from enemySkills
        Skill skill = getSkillByName(skillName, enemySkills);
        // Return the affected tiles using enemy position as the origin
        return skill.getAffectedTiles(enemyPosition);
    }

    public void usePlayerSkill(String skillName) {
        // this function gets the Skill using getSkillByName, passing in skillName and playerSkills
        // then the function gets affectedTiles from skill.getAffectedTiles, passing in the playerPosition as the origin_pos
        // then this functions calls didAtkHit, passing in affectedTiles
        // if didAtkHit is true then this function gets the damage from the Skill using skill.getDamage and
        // calls updateHealth on enemyState with -damage as delta
        // Get the skill by name from playerSkills

        Skill skill = getSkillByName(skillName, playerSkills);
        if (skill == null) return; // Exit if skill not found

        // Get affected tiles using player's position
        ArrayList<int[]> affectedTiles = skill.getAffectedTiles(playerPosition);

        // Check if the attack hits
        if (didAtkHit(affectedTiles)) {
            // Get skill damage
            int damage = skill.getDamage();

            // Apply damage to enemyState using modifyHealth() (negative delta for damage)
            enemyState.modifyHealth(-damage);
        }
    }
    public Skill getSkillByName(String name, ArrayList<Skill> skillList) {
        // this function goes and finds the correct skill by matching the name to the name on
        //each Skill in the the skill list
        for (Skill skill : skillList) {
            if (skill.getName().equals(name)) {
                return skill; // Found the matching skill, return it
            }
        }
        return null; // Skill not found, return null
    }

    public void updatePlayerPos(int[] position) {
        // this function just makes playerPosition equal to the position passed in
        playerPosition = position;
    }

    public ArrayList<int[]> getAvailableMoveTilesForPlayer() {
        // this function gets the position in the four cardinal directions from the player
        // and returns those available move tiles
        // (I wrote this code since its the same tiles as a straight atk with a distance of 1)
        int[][] positionVectors = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        return getRepeatingPattern(playerPosition, 1, positionVectors, maxRows, maxCols);
    }

    public ArrayList<Skill> getEnemySkills() {
        return enemySkills;
    }

    private void populatePlayerSkills() {
        int[] skillIds = playerState.getSkillList();
        populateSkills(skillIds, "playerSkills");
    }

    private void populateEnemySkills(int enemyId) {
        JSONArray skillIds = (JSONArray) getSingleDataProperty("enemies.json", "skills", enemyId, context);
        try {
            int length = skillIds.length();
            int[] skillArray = new int[length];
            for (int i = 0; i < length; i++) {
                skillArray[i] = skillIds.getInt(i);
            }
            populateSkills(skillArray, "enemySkills");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateSkills(int[] ids, String skillList) {
        ArrayList<String> skillNames = getListOfDataProperty("skills.json", "name", ids, context);
        ArrayList<String> skillAtkTypes = getListOfDataProperty("skills.json", "atkPattern", ids, context);
        for(int i = 0; i < ids.length; i++) {
            Skill skill = new Skill(skillNames.get(i), skillAtkTypes.get(i));
            switch (skillList) {
                case "playerSkills":
                    playerSkills.add(skill);
                    break;
                case "enemySkills":
                    enemySkills.add(skill);
                    break;
            }
        }
    }

    private void createEnemy(int enemyId) {
        String name = (String) getSingleDataProperty("enemies.json", "name", enemyId, context);
        int maxHealth = (int) getSingleDataProperty("enemies.json", "maxhealth", enemyId, context);
        enemyState = new EnemyState(name, maxHealth);
    }
}
