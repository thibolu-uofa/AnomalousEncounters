package presenter;

import static model.Utils.getDataProperty;
import static model.Utils.getSingleDataProperty;
import static model.Utils.getStringListOfDataProperty;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import model.BattleSystem;
import model.EncounterSystem;
import model.EnemyState;
import model.GameLogic;
import model.PlayerState;
import view.GameView;

public class GamePresenter extends AppCompatActivity {
    private GameView view;
    private GameLogic gameLogic;
    private PlayerState playerState;
    private EncounterSystem encounterSystem;
    private BattleSystem battleSystem;
    private SoundPool soundPool;


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
//        setUpBattle(0);

//        playSound("sample_sound.wav");
    }

    // https://gamecodeschool.com/android/playing-sound-fx-demo/
    //https://www.geeksforgeeks.org/soundpool-in-android-with-examples/
    private void playSound(String filename) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        try {
            AssetManager assetManager = this.getAssets();
            AssetFileDescriptor descriptor;

            // load sound in memory ready for use
            descriptor = assetManager.openFd("sample_sound.wav");
            int soundID = soundPool.load(descriptor, 0);

            soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
                if (status == 0) {
                    // sound loaded successfully
                    soundPool.play(soundID, 1, 1, 0, 0, 1);
                } else {
                    Log.e("Error with sound", "Sound load failed");
                }
            });

        } catch (IOException e) {
            Log.e("Error with sound", "failed to load sound files", e);
        }
    }

    // release sound pool when no longer in use, like when game is paused
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
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

    public void hasPlayerEncounteredEnemy(int playerX) {
        if (encounterSystem.hasEncounteredEnemy(playerX)) {
            int enemyId = encounterSystem.getRandomEnemyId();
            Log.d("Enemy ID picked", "Enemy ID:" + enemyId);
            setUpBattle(enemyId);
        }
    }

    public String getEnemyNameAndHealth() {
        EnemyState enemyState = battleSystem.getEnemyState();
        String name = enemyState.getName();
        String maxHealth = String.valueOf(enemyState.getEnemyMaxHealth());
        String currentHealth = String.valueOf(enemyState.getEnemyCurrentHealth());
        return name + "\nHP: " + maxHealth + "/" + currentHealth;
    }

    public void setUpBattle(int enemyId) {
        battleSystem = new BattleSystem(playerState, enemyId, this);

        // tell view that a battle has started
        view.displayBattle();

        //tell view what enemy image to use
        String image_name = (String) getSingleDataProperty("enemies.json", "image_name", enemyId, this);
        view.setEnemyImage(image_name);

        // tell view to draw player and enemy
        int [] playerPosition = battleSystem.getPlayerPosition();
        int [] enemyPosition = battleSystem.getEnemyPosition();

        int[] playerBoardPosition = convertPositionToBoardDimensions(playerPosition);
        int playerBoardX = playerBoardPosition[0];
        int playerBoardY = playerBoardPosition[1];

        int[] enemyBoardPosition = convertPositionToBoardDimensions(enemyPosition);
        int enemyBoardX = enemyBoardPosition[0];
        int enemyBoardY = enemyBoardPosition[1];

        //tell view to draw player and enemy
        view.updatePlayerGridPosition(playerBoardX, playerBoardY);
        view.updateEnemyGridPosition(enemyBoardX, enemyBoardY);
    }

    public String getSkillCooldownsString() {
        if (battleSystem == null) {
            return "";
        }
        ArrayList<Integer> playerSkillCooldowns = battleSystem.getPlayerSkillCooldowns();
        StringBuilder skillCooldowns = new StringBuilder();
        for (Integer cooldown: playerSkillCooldowns) {
            skillCooldowns.append(cooldown).append('\n');
        }
        return String.valueOf(skillCooldowns);
    }

    public int[] getPlayerPosition() {
        return convertPositionToBoardDimensions(battleSystem.getPlayerPosition());
    }

    public void movePlayer(String direction) {
        int[] newPlayerPos = battleSystem.getNewPlayerBoardPosition(direction);
        battleSystem.updatePlayerPos(newPlayerPos);
    }

    public int[] getTemporaryPlayerPosition(String direction) {
        int[] newTempPlayerPos = battleSystem.getNewPlayerBoardPosition(direction);
        return convertPositionToBoardDimensions(newTempPlayerPos);
    }

    private int[] convertPositionToBoardDimensions(int[] position) {
        // covert position to board dimensions
        int rows = battleSystem.getMaxRows();
        int cols = battleSystem.getMaxCols();
        int boardWidth = view.getBoardWidth();
        int boardHeight = view.getBoardHeight();

        int tileWidth = boardWidth / cols;
        int tileHeight = boardHeight / rows;

        int[] convertedDimensions = new int[2];
        convertedDimensions[0] = position[0] * tileWidth;
        convertedDimensions[1] = position[1] * tileHeight;

        return convertedDimensions;
    }

    public ArrayList<int[]> getAffectedTilesForPlayer(String skillName) {
        ArrayList<int[]> affectedTiles = battleSystem.getAffectedTilesForPlayer(skillName);
        ArrayList<int[]> affectedTilesRealPositions = new ArrayList<>();
        for (int[] position: affectedTiles) {
            int[] realPosition = convertPositionToBoardDimensions(position);
            affectedTilesRealPositions.add(realPosition);
        }
        return affectedTilesRealPositions;
    }

    public void usePlayerSkill(String skillName) {
        battleSystem.usePlayerSkill(skillName);
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
