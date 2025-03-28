package model;

import static model.Utils.getStringListOfDataProperty;
import static model.Utils.getRepeatingPattern;
import static model.Utils.getSingleDataProperty;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
    private boolean isPlayerTurn = true;
    private GridModel gridModel;
    private final int maxRows;
    private final int maxCols;
    private boolean hasPlayerAttacked;
    private boolean hasPlayerMoved;

    public BattleSystem(GamePresenter presenter, PlayerState playerState, int enemyId, Context context) {
        this.presenter = presenter;
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

        hasPlayerAttacked = false;
        hasPlayerMoved = false;

    }

    public boolean canEndPlayerTurn() {
        return hasPlayerMoved || hasPlayerAttacked;
    }

    public void changeTurn() {
        isPlayerTurn = !isPlayerTurn;
        hasPlayerMoved = false;
        hasPlayerAttacked = false;
    }

    public void executeEnemyTurn() {
        EnemyAI enemyAI = new EnemyAI(enemySkills, this);
        enemyAI.executeEnemyTurn();
    }

    public void endEnemyTurn() {
        presenter.endEnemyTurn();
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

    //TODO: TEST FUNCTION BEFORE USING
    public void useSkill(String skillName) {
        ArrayList<Skill> skillList = isPlayerTurn ? playerSkills : enemySkills;
        Skill skill = getSkillByName(skillName, skillList);
        if (skill == null) return; // Exit if skill not found

        // Get affected tiles using
        int[] originPosition = isPlayerTurn ? playerPosition : enemyPosition;
        ArrayList<int[]> affectedTiles = skill.getAffectedTiles(originPosition);

        // Check if the attack hits
        if (didAtkHit(affectedTiles)) {
            // Get skill damage
            int damage = skill.getDamage();

            // Apply damage using modifyHealth() (negative delta for damage)
            if (isPlayerTurn) {
                enemyState.modifyHealth(-damage);
                hasPlayerAttacked = true;
            } else {
                playerState.modifyHealth(-damage);
            }
        }
    }

    public void usePlayerSkill(String skillName) {
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
            hasPlayerAttacked = true;
        }
    }

    public void useEnemySkill(Skill skill) {
        if (skill == null) {
            return;
        } // Exit if skill not found

        // Get affected tiles using player's position
        ArrayList<int[]> affectedTiles = skill.getAffectedTiles(enemyPosition);

        // Check if the attack hits
        if (didAtkHit(affectedTiles)) {
            // Get skill damage
            int damage = skill.getDamage();

            // Apply damage to enemyState using modifyHealth() (negative delta for damage)
            playerState.modifyHealth(-damage);
        }
    }

    private Skill getRandomEnemySkill() {
        int[] skillIds = enemyState.getSkillList();

        Random rand = new Random();
        int random_skill_id = rand.nextInt(skillIds.length);

        return enemySkills.get(random_skill_id);
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

    public ArrayList<Integer> getEnemySkillIDs(int enemyId) {
        JSONArray skillIdsJson = (JSONArray) getSingleDataProperty("enemySkill.json", "skills", enemyId, context);
        ArrayList<Integer> skillIds = new ArrayList<>();

        try {
            for (int i = 0; i < skillIdsJson.length(); i++) {
                skillIds.add(skillIdsJson.getInt(i));
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error parsing skill IDs from enemySkill.json", e);
        }

        return skillIds;
    }


    public void updatePlayerPos(int[] position) {
        // this function just makes playerPosition equal to the position passed in
        playerPosition = position;
        hasPlayerMoved = true;
    }

    public void updateEnemyPos(int[] position) {
        if (position == null) {
            return;
        }
        enemyPosition = position;
        presenter.visuallyUpdateEnemyPos(position);
    }

    public int[] getNewPlayerBoardPosition(String direction) {
        int[] playerTempPosition = new int[2];
        switch (direction) {
            case "up":
                playerTempPosition[0] = playerPosition[0];
                playerTempPosition[1] = playerPosition[1] - 1;
                break;
            case "down":
                playerTempPosition[0] = playerPosition[0];
                playerTempPosition[1] = playerPosition[1] + 1;
                break;
            case "right":
                playerTempPosition[0] = playerPosition[0] + 1;
                playerTempPosition[1] = playerPosition[1];
                break;
            case "left":
                playerTempPosition[0] = playerPosition[0] - 1;
                playerTempPosition[1] = playerPosition[1];
                break;
        }
        ArrayList<int[]> availableMoves = getAvailableMoveTilesForPlayer();
        boolean isValidMove = isMoveValid(playerTempPosition, availableMoves);
        if (isValidMove) {
            return playerTempPosition;
        }
        return playerPosition;
    }

    private ArrayList<int[]> getAvailableMoveTilesForPlayer() {
        int[][] positionVectors = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        return getRepeatingPattern(playerPosition, 1, positionVectors, maxRows, maxCols);
    }

    public ArrayList<int[]> getAvailableMoveTilesForEnemy() {
        int[][] positionVectors = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        return getRepeatingPattern(enemyPosition, 1, positionVectors, maxRows, maxCols);
    }

    // make sure move is in available moves and not equal to enemy position
    public boolean isMoveValid(int[] newPosition, ArrayList<int[]> availableMoves) {
        int[] position = isPlayerTurn ? enemyPosition : playerPosition;
        for (int[] move : availableMoves) {
            if (Arrays.equals(newPosition, move) && !Arrays.equals(newPosition, position)) {
                return true;
            }
        }
        return false;
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

    //TO DO: add maxCooldown as an attribute in skills.json and retrieve all skills maxCooldown and pass that instead of 1
    private void populateSkills(int[] ids, String skillList) {
        ArrayList<String> skillNames = getStringListOfDataProperty("skills.json", "name", ids, context);
        ArrayList<String> skillAtkTypes = getStringListOfDataProperty("skills.json", "atkPattern", ids, context);
        int[] playerSkillLevels = playerState.getSkillLevels();
        for(int i = 0; i < ids.length; i++) {
            Skill skill;
            switch (skillList) {
                case "playerSkills":
                    skill = new Skill(skillNames.get(i), skillAtkTypes.get(i), playerSkillLevels[i], 1);
                    playerSkills.add(skill);
                    break;
                case "enemySkills":
                    int skillLevel = generateEnemySkillLevel();
                    skill = new Skill(skillNames.get(i), skillAtkTypes.get(i), skillLevel, 1);
                    enemySkills.add(skill);
                    break;
            }
        }
    }

    // Make this method more sophisticated later (maybe generate skill levels according to player phase)
    private int generateEnemySkillLevel() {
        Random rand = new Random();
        return rand.nextInt(3);
    }

    private void createEnemy(int enemyId) {
        String name = (String) getSingleDataProperty("enemies.json", "name", enemyId, context);
        int maxHealth = (int) getSingleDataProperty("enemies.json", "maxhealth", enemyId, context);
        enemyState = new EnemyState(name, maxHealth, enemyId);
    }

    public ArrayList<Integer> getPlayerSkillCooldowns() {
        ArrayList<Integer> playerSkillCooldowns = new ArrayList<>();
        for (Skill skill: playerSkills) {
            playerSkillCooldowns.add(skill.getCurrentCooldown());
        }
        return playerSkillCooldowns;
    }

    public int[] getPlayerPosition() {
        return playerPosition;
    }

    public int[] getEnemyPosition() {
        return enemyPosition;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public int getMaxCols() {
        return maxCols;
    }

    public EnemyState getEnemyState() {
        return enemyState;
    }

    public int getEnemyId() {
        return enemyState.getId();
    }
}
