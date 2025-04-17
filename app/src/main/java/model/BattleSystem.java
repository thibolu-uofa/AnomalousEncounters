package model;

import static model.EnemyUtils.getEnemyMaxHealth;
import static model.EnemyUtils.getEnemyTypeFromId;
import static model.SkillUtils.calculateEnemySkillLevel;
import static model.SkillUtils.getExperienceGained;
import static model.SkillUtils.getUpdatedLevelAndExperience;
import static model.Utils.getStringListOfDataProperty;
import static model.Utils.getRepeatingPattern;
import static model.Utils.getSingleDataProperty;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import presenter.GamePresenter;

public class BattleSystem {
    private final GamePresenter presenter;
    private final Context context;

    private EnemyState enemyState;
    private EnemyAI enemyAI;
    private int[] enemyPosition = new int[2];
    private final PlayerState playerState;
    private int[] playerPosition = new int[2];
    private final ArrayList<Skill> playerSkills = new ArrayList<>();
    private final ArrayList<Skill> enemySkills = new ArrayList<>();
    private boolean isPlayerTurn = true;
    private final int maxRows;
    private final int maxCols;
    private final ArrayList<int[]> PLAYER_START_POSITIONS = new ArrayList<>();
    private final ArrayList<int[]> ENEMY_START_POSITIONS = new ArrayList<>();
    private boolean hasPlayerAttacked;
    private boolean hasPlayerMoved;

    public BattleSystem(GamePresenter presenter, PlayerState playerState, int enemyId, int enemyTier, Context context) {
        this.presenter = presenter;
        this.playerState = playerState;
        this.context = context;

        populatePlayerSkills(enemyId);
        createEnemy(enemyId, enemyTier);
        populateEnemySkills(enemyId, enemyTier);

        GridModel gridModel = new GridModel();
        maxRows = gridModel.getRowCount();
        maxCols = gridModel.getColumnCount();

        initializePlayerAndEnemyStartingPositions();

        hasPlayerAttacked = false;
        hasPlayerMoved = false;

        enemyAI = new EnemyAI(enemySkills, this, presenter);
    }

    private void initializePlayerAndEnemyStartingPositions() {
        for (int i = 1; i < maxRows - 1; i++) {
            PLAYER_START_POSITIONS.add(new int[]{1, i});
            ENEMY_START_POSITIONS.add(new int[]{6, i});
        }

        Random rand = new Random();
        int randPlayerIndex = rand.nextInt(PLAYER_START_POSITIONS.size());
        playerPosition = PLAYER_START_POSITIONS.get(randPlayerIndex);

        int randEnemyIndex = rand.nextInt(ENEMY_START_POSITIONS.size());
        enemyPosition = ENEMY_START_POSITIONS.get(randEnemyIndex);
    }

    public void changeTurn() {
        isPlayerTurn = !isPlayerTurn;
        hasPlayerMoved = false;
        hasPlayerAttacked = false;
    }

    public void executeEnemyTurn() {
        enemyAI.executeEnemyTurn();
    }

    public boolean didAtkHit(ArrayList<int[]> coords) {
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

    public ArrayList<int[]> getAffectedTilesForEnemy(Skill skill) {
        return skill.getAffectedTiles(enemyPosition);
    }

    public boolean canUsePlayerSkill(String skillName) {
        Skill skill = getSkillByName(skillName, playerSkills);
        return skill.canUseSkill();
    }

    public void usePlayerSkill(String skillName) {
        Skill skill = getSkillByName(skillName, playerSkills);
        if (skill == null) return; // Exit if skill not found
        if (!skill.canUseSkill()) return;

        // Get affected tiles using player's position
        ArrayList<int[]> affectedTiles = skill.getAffectedTiles(playerPosition);

        // Check if the attack hits
        if (didAtkHit(affectedTiles)) {
            // Get skill damage
            int damage = skill.getDamage();

            // Apply damage to enemyState using modifyHealth() (negative delta for damage)
            enemyState.modifyHealth(-damage);
            hasPlayerAttacked = true;

            //play sound effect
            presenter.playHitEnemySound();
        }

        skill.activateSkillCooldown();
    }

    public void updatePlayerSkillCooldowns() {
        for (Skill skill: playerSkills) {
            skill.updateSkillCooldown();
        }
    }

    public void useEnemySkill(Skill skill) {
        if (skill == null) {
            return;
        } // Exit if skill not found
        if (!skill.canUseSkill()) return;

        // Get affected tiles using player's position
        ArrayList<int[]> affectedTiles = skill.getAffectedTiles(enemyPosition);

        // Check if the attack hits
        if (didAtkHit(affectedTiles)) {
            // Get skill damage
            int damage = skill.getDamage();

            // Apply damage to enemyState using modifyHealth() (negative delta for damage)
            playerState.modifyHealth(-damage);

            //play sound effect
            presenter.playGetHitSound();
        }

        skill.activateSkillCooldown();
    }

    public void updateEnemySkillCooldowns() {
        for (Skill skill: enemySkills) {
            skill.updateSkillCooldown();
        }
    }

    public double getPlayerSkillResistanceFactorByName(String name) {
        Skill skill = getSkillByName(name, playerSkills);
        return skill.getResistanceFactor();
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
        int[] playerTempPosition = calculatePositionForDirection(direction);

        if (isValidPlayerMove(playerTempPosition)) {
            return playerTempPosition;
        }

        return playerPosition;
    }

    private int[] calculatePositionForDirection(String direction) {
        int[] playerTempPosition = new int[] {playerPosition[0], playerPosition[1]};

        // apply offset based on direction
        switch (direction) {
            case "up":
                playerTempPosition[1]--;
                break;
            case "down":
                playerTempPosition[1]++;
                break;
            case "right":
                playerTempPosition[0]++;
                break;
            case "left":
                playerTempPosition[0]--;
                break;
        }

        return playerTempPosition;
    }

    private boolean isValidPlayerMove(int[] potentialPosition) {
        ArrayList<int[]> availableMoves = getAvailableMoveTilesForPlayer();
        return isMoveValid(potentialPosition, availableMoves);
    }

    private ArrayList<int[]> getAvailableMoveTilesForPlayer() {
        int[][] positionVectors = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        return getRepeatingPattern(playerPosition, 1, positionVectors, maxRows, maxCols);
    }

    public ArrayList<int[]> getAvailableMoveTilesForEnemy(int[][] positionVectors) {
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

    public boolean isSingleMoveValid(int[] newPosition) {
        int[] position = isPlayerTurn ? enemyPosition : playerPosition;
        if (!Arrays.equals(newPosition, position)) {
            return true;
        }
        return false;
    }


    private void populatePlayerSkills(int enemyId) {
        int[] skillIds = playerState.getSkillList();
        int phase = playerState.getPhase();
        int tier = 5 - phase;
        populateSkills(skillIds, "playerSkills", tier, enemyId);
    }

    private void populateEnemySkills(int enemyId, int tier) {
        JSONArray skillIds = (JSONArray) getSingleDataProperty("enemies.json", "skills", enemyId, context);
        try {
            int length = skillIds.length();
            int[] skillArray = new int[length];
            for (int i = 0; i < length; i++) {
                skillArray[i] = skillIds.getInt(i);
            }
            populateSkills(skillArray, "enemySkills", tier, enemyId);
        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON files", e);
            throw new RuntimeException(e);
        }
    }

    private void populateSkills(int[] ids, String skillList, int tier, int enemyId) {
        ArrayList<String> skillNames = getStringListOfDataProperty("skills.json", "name", ids, context);
        ArrayList<String> skillAtkTypes = getStringListOfDataProperty("skills.json", "atkPattern", ids, context);
        SkillUtils.AnomalyTypes playerType = playerState.getAffinity();
        SkillUtils.AnomalyTypes enemyType = getEnemyTypeFromId(enemyId, context);
        int[] playerSkillLevels = playerState.getSkillLevels();
        for(int i = 0; i < ids.length; i++) {
            Skill skill;
            switch (skillList) {
                case "playerSkills":
                    skill = new Skill(skillNames.get(i), skillAtkTypes.get(i), playerSkillLevels[i], tier, ids[i], false);
                    skill.applySkillResistance(playerType, enemyType);
                    playerSkills.add(skill);
                    break;
                case "enemySkills":
                    int skillLevel = calculateEnemySkillLevel(tier);
                    skill = new Skill(skillNames.get(i), skillAtkTypes.get(i), skillLevel, tier, ids[i], true);
                    skill.applySkillResistance(enemyType, playerType);
                    enemySkills.add(skill);
                    break;
            }
        }
    }


    private void createEnemy(int enemyId, int tier) {
        String name = (String) getSingleDataProperty("enemies.json", "name", enemyId, context);
        String type = (String) getSingleDataProperty("enemies.json", "type", enemyId, context);
        int maxHealth = getEnemyMaxHealth(tier);
        enemyState = new EnemyState(name, maxHealth, enemyId, tier);
        enemyState.setType(type);
    }

    public ArrayList<Integer> getPlayerSkillCooldowns() {
        ArrayList<Integer> playerSkillCooldowns = new ArrayList<>();
        for (Skill skill: playerSkills) {
            playerSkillCooldowns.add(skill.getCurrentCooldown());
        }
        return playerSkillCooldowns;
    }

    public void playerSkillExperience() {
        for (Skill skill: playerSkills) {
            int enemyTier = enemyState.getTier();
            int phase = playerState.getPhase();
            int usage = skill.getTimesUsed();
            int expGained = getExperienceGained(enemyTier, phase, usage);

            int id = skill.getId();
            int level = playerState.getLevelOfSkill(id);
            int currentExperience = playerState.getExperienceOfSkill(id);
            int[] levelAndExperience = getUpdatedLevelAndExperience(level, currentExperience, expGained);

            playerState.setSkillLevelAndExperience(id, levelAndExperience[0], levelAndExperience[1]);
        }
    }

    public int getSkillDamage(String name) {
        for (Skill skill: playerSkills) {
            if (Objects.equals(skill.getName(), name)) {
                return skill.getDamage();
            }
        }

        return -1;
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

    public int getEnemyTier() {
        return enemyState.getTier();
    }

    public boolean getIsPlayerTurn() {
        return isPlayerTurn;
    }

    public Skill getChosenEnemySkill() {
        return enemyAI.getChosenSkill();
    }
}
