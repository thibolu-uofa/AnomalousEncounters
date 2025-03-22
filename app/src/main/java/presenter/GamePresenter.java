package presenter;

import static model.Utils.getDataProperty;
import static model.Utils.getSingleDataProperty;
import static model.Utils.getStringListOfDataProperty;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.BattleSystem;
import model.EncounterSystem;
import model.GameLogic;
import model.PlayerState;
import view.GameView;

public class GamePresenter extends AppCompatActivity {
    private GameView view;
    private GameLogic gameLogic;
    private PlayerState playerState;
    private EncounterSystem encounterSystem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        makeNewPlayer();
        // Initialize gameView and set it as the view
        view = new GameView(this, this);
        setContentView(view);

        gameLogic = new GameLogic();
        encounterSystem = new EncounterSystem();

        // MORE TESTING
//        Log.d("Skill Names", getSkillNamesString());
//        Log.d("Skill Description", getSkillDescription(0));
//        Log.d("Item Names", getItemNames());
//        Log.d("Item Description", getItemDescription(0));

        //FOR TESTING PURPOSES
        setUpBattle(0);
    }

    private void makeNewPlayer() {
        int newPlayerIndex = 0;
        String name = (String) getSingleDataProperty("player_config.json", "name", newPlayerIndex, this);
        int maxHealth = (int) getSingleDataProperty("player_config.json", "maxHealth", newPlayerIndex, this);

        playerState = new PlayerState(name, maxHealth);

        loadPlayerItems(newPlayerIndex);
        loadPlayerSkills(newPlayerIndex);
    }

    private void loadPlayerItems(int playerIndex) {
        try {
            JSONArray items = (JSONArray) getSingleDataProperty("player_config.json", "items", playerIndex, this);
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                int itemId = item.getInt("id");
                int itemAmount = item.getInt("amount");
                playerState.addItem(itemId, itemAmount);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadPlayerSkills(int playerIndex) {
        try {
            JSONArray skills = (JSONArray) getSingleDataProperty("player_config.json", "skills", playerIndex, this);
            for (int i = 0; i < skills.length(); i++) {
                JSONObject skill = skills.getJSONObject(i);
                int skillId = skill.getInt("id");
                int skillLevel = skill.getInt("level");
                playerState.addSkill(skillId, skillLevel);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isInHitbox(int eventX, int eventY, int leftX, int rightX, int topY, int bottomY) {
        return gameLogic.isInHitbox(eventX, eventY, leftX, rightX, topY, bottomY);
    }

    public float getPlayerHealthPercentage() {
        int max_health = playerState.getPlayerMaxHealth();
        int health = playerState.getHealth();
        return (float) health /max_health;
    }

    public String getPlayerMovementState (int eventX, int playerX1, int playerX2){
       String playerMovementState = gameLogic.getPlayerMovementState(eventX, playerX1, playerX2);
       String playerAnimation;
       switch (playerMovementState) {
           case "Right":
               playerAnimation = "walk_right";
               break;
           case "Left":
               playerAnimation = "walk_left";
               break;
           default:
              playerAnimation = "idle";
       }
       return playerAnimation;
    }

    public int getBackgroundDirection(String playerMovementState) {
        switch (playerMovementState){
            case "walk_right":
                return -1;
            case "walk_left":
                return  1;
            default:
                return 0;
        }
    }

    public boolean hasPlayerEncounteredEnemy(int playerX) {
        return encounterSystem.hasEncounteredEnemy(playerX);
    }

    public void setUpBattle(int enemyId) {
        BattleSystem battleSystem = new BattleSystem(playerState, enemyId, this);

        // tell view that a battle has started
        view.displayBattle();

        // tell view to draw player and enemy
        int [] playerPosition = battleSystem.getPlayerPosition();
        int [] enemyPosition = battleSystem.getEnemyPosition();

        // covert position to board dimensions
        int rows = battleSystem.getMaxRows();
        int cols = battleSystem.getMaxCols();
        int boardWidth = view.getBoardWidth();
        int boardHeight = view.getBoardHeight();

        int tileWidth = boardWidth / cols;
        int tileHeight = boardHeight / rows;

        int playerBoardX = playerPosition[0] * tileWidth;
        int playerBoardY = playerPosition[1] * tileHeight;

        int enemyBoardX = enemyPosition[0] * tileWidth;
        int enemyBoardY = enemyPosition[1] * tileHeight;

        //tell view to draw player and enemy
        view.updatePlayerGridPosition(playerBoardX, playerBoardY);
        view.updateEnemyGridPosition(enemyBoardX, enemyBoardY);
    }

    public String getPlayerNameHealthAndTokens() {
        String name = playerState.getName();
        String maxHealth = String.valueOf(playerState.getPlayerMaxHealth());
        String currentHealth = String.valueOf(playerState.getHealth());
        String tokens = String.valueOf(playerState.getTokens());
        return name + "\nHP: " + maxHealth + "/" + currentHealth + "\nTokens: " + tokens;
    }

    public String getPlayerNameAndHealth() {
        String name = playerState.getName();
        String maxHealth = String.valueOf(playerState.getPlayerMaxHealth());
        String currentHealth = String.valueOf(playerState.getHealth());
        return name + "\nHP: " + maxHealth + "/" + currentHealth;
    }


    public String getSkillNamesString() {
        int[] skillIds = playerState.getSkillList();
        return getDataProperty("skills.json", "name", skillIds, this);
    }

    public ArrayList<String> getSkillNamesArray() {
        int[] skillIds = playerState.getSkillList();
        return getStringListOfDataProperty("skills.json", "name", skillIds, this);
    }

    public String getSkillDescription(int skillId) {
        int[] skillIds = {skillId};
        return getDataProperty("skills.json", "description", skillIds, this);
    }

    public String getSkillLevelsString() {
        int[] skillLevels = playerState.getSkillLevels();
        StringBuilder skillLevelsString = new StringBuilder();
        for (int level: skillLevels) {
            skillLevelsString.append(level).append('\n');
        }
        return String.valueOf(skillLevelsString);
    }

    public String getItemNames() {
        int[] itemIds = playerState.getItemList();
        return getDataProperty("items.json", "name", itemIds, this);
    }

    public String getItemDescription(int itemId) {
        int[] itemIds = {itemId};
        return getDataProperty("items.json", "description", itemIds, this);
    }

    public String getItemAmounts() {
        int[] itemAmounts = playerState.getItemAmountsList();
        StringBuilder itemAmountsString = new StringBuilder();
        for (int amount: itemAmounts) {
            itemAmountsString.append(amount).append('\n');
        }
        return String.valueOf(itemAmountsString);
    }

    // This method executes when the user continues the game
    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }

    // This method executes when the user quits the game
    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
    }
}
