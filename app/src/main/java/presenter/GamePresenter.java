package presenter;

import static model.EnemyUtils.ESSENCE_NAME;
import static model.EnemyUtils.SHARD_NAME;
import static model.EnemyUtils.getEnemyDropsFromTier;
import static model.SkillUtils.getMaxExperience;
import static model.SkillUtils.getSkillCompensation;
import static model.SkillUtils.getSkillExpGainedForVolume;
import static model.SkillUtils.getUpdatedLevelAndExperience;
import static model.Utils.getDataProperty;
import static model.Utils.getEnemyImage;
import static model.Utils.getPropertyByName;
import static model.Utils.getSingleDataProperty;
import static model.Utils.getStringListOfDataProperty;

import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.anomalousencounters.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import model.BattleSystem;
import model.EncounterSystem;
import model.EnemyState;
import model.GameLogic;
import model.ItemUtils;
import model.PlayerState;
import model.Skill;
import model.SkillUtils;
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

        // set orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView startNewGame = findViewById(R.id.startButton);
        startNewGame.setOnClickListener(v ->
                setContentView(view)
        );

        setUpNewGame();

        // FOR TESTING PURPOSES
        // playSound("sample_sound.wav");
    }

    private void setUpNewGame() {
        makeNewPlayer();

        gameLogic = new GameLogic();
        encounterSystem = new EncounterSystem();

        // Initialize gameView and set it as the view
        view = new GameView(this, this);
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
        int tokens = (int) getSingleDataProperty("player_config.json", "tokens", newPlayerIndex, this);

        playerState = new PlayerState(name, maxHealth, tokens);
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
                int skillExp = skill.getInt("experience");
                playerState.addSkill(skillId, skillLevel, skillExp);
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
            int phase = playerState.getPhase();
            int enemyTier = encounterSystem.getEnemyTier(phase);
            Log.d("Enemy ID picked", "Enemy ID:" + enemyId);
            setUpBattle(enemyId, enemyTier);
        }
    }

    public String getEnemyNameAndHealth() {
        EnemyState enemyState = battleSystem.getEnemyState();
        String name = enemyState.getName();
        String maxHealth = String.valueOf(enemyState.getEnemyMaxHealth());
        String currentHealth = String.valueOf(enemyState.getEnemyCurrentHealth());
        return name + "\nHP: " + currentHealth + "/" + maxHealth;
    }

    public void setUpBattle(int enemyId, int enemyTier) {
        battleSystem = new BattleSystem(this, playerState, enemyId, enemyTier,this);

        // tell view that a battle has started
        view.displayBattle();

        //tell view what enemy image to use
        Bitmap enemyImage = getEnemyImage(enemyId, this);
        view.setEnemyImage(enemyImage);

        updatePlayerAndEnemyPositions();
    }

    private void updatePlayerAndEnemyPositions() {
        int[] playerPosition = battleSystem.getPlayerPosition();
        int[] enemyPosition = battleSystem.getEnemyPosition();

        int[] playerBoardPosition = convertPositionToBoardDimensions(playerPosition);
        int[] enemyBoardPosition = convertPositionToBoardDimensions(enemyPosition);

        view.updatePlayerGridPosition(playerBoardPosition[0], playerBoardPosition[1]);
        view.updateEnemyGridPosition(enemyBoardPosition[0], enemyBoardPosition[1]);
    }

    public void visuallyUpdateEnemyPos(int[] position) {
        position = convertPositionToBoardDimensions(position);
        view.updateEnemyGridPosition(position[0], position[1]);
    }

    public void endPlayerTurn() {
        battleSystem.updatePlayerSkillCooldowns();
    }

    public void startEnemyTurn() {
        battleSystem.changeTurn();
        battleSystem.executeEnemyTurn();
    }

    public void endEnemyTurn() {
        battleSystem.updateEnemySkillCooldowns();
        battleSystem.changeTurn();
        view.resetActionFlags();
    }

    public boolean canPlayerUseSkill(String name) {
        return battleSystem.canUsePlayerSkill(name);
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

    public boolean isPlayerLoser() {
        return battleSystem.isPlayerLoser();
    }

    public boolean isPlayerWinner() {
        return battleSystem.isPlayerWinner();
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
        return covertAffectedTilesToRealPositions(affectedTiles);
    }

    public ArrayList<int[]> covertAffectedTilesToRealPositions(ArrayList<int[]> affectedTiles) {
        ArrayList<int[]> affectedTilesRealPositions = new ArrayList<>();
        for (int[] position: affectedTiles) {
            int[] realPosition = convertPositionToBoardDimensions(position);
            affectedTilesRealPositions.add(realPosition);
        }
        return affectedTilesRealPositions;
    }

    public void skillHasBeenUsed() {
        boolean isPlayerTurn = battleSystem.getIsPlayerTurn();
        if (isPlayerTurn) {
            String chosenPlayerSkill = view.getChosenPlayerSkill();
            battleSystem.usePlayerSkill(chosenPlayerSkill);
            checkIfPlayerWinner();

        } else {
            Skill chosenEnemySkill = battleSystem.getChosenEnemySkill();
            battleSystem.useEnemySkill(chosenEnemySkill);
            endEnemyTurn();
            checkIfPlayerLoser();
        }
    }

    public void enemyChoseSkill(Skill skill) {
        // animate the enemy skill
        ArrayList<int[]> affectedTiles = battleSystem.getAffectedTilesForEnemy(skill);
        ArrayList<int[]> affectedTilesRealPositions = covertAffectedTilesToRealPositions(affectedTiles);
        view.animateEnemySkill(affectedTilesRealPositions);
    }

    private void checkIfPlayerWinner() {
        boolean isPlayerWinner = isPlayerWinner();
        if (isPlayerWinner) {
            battleSystem.playerSkillExperience();
            view.endBattle(true);
        }
    }

    public void checkIfPlayerLoser() {
        boolean isPlayerLoser = isPlayerLoser();
        if (isPlayerLoser) {
            battleSystem.playerSkillExperience();
            view.endBattle(false);
            playerLost();
        }
    }

    public void chargeBattleFleeFee(){
        int fee = 5;
        playerState.updateTokens(-fee);
    }

    private String formatPlayerInfo(boolean includeTokens) {
        StringBuilder sb = new StringBuilder();
        String name = playerState.getName();
        int currentHealth = playerState.getHealth(), maxHealth = playerState.getPlayerMaxHealth();

        sb.append(name).append("\nHP: ").append(currentHealth).append("/").append(maxHealth);

        if (includeTokens) {
            sb.append("\nTokens: ").append(playerState.getTokens());
        }
        return sb.toString();
    }

    public String getPlayerNameHealthAndTokens() {
        return formatPlayerInfo(true);
    }

    public String getPlayerNameAndHealth() {
        return formatPlayerInfo(false);
    }

    public String getEnityNameAndType(int id) {
        StringBuilder sb = new StringBuilder();
        String name = (String) getSingleDataProperty("enemies.json", "name", id, this);
        String type = (String) getSingleDataProperty("enemies.json", "type", id, this);

        sb.append(name).append("\nType: ").append(type);

        return sb.toString();
    }

    public ArrayList<String> getSkillNamesArray() {
        int[] skillIds = playerState.getSkillList();
        return getStringListOfDataProperty("skills.json", "name", skillIds, this);
    }

    public String getSkillDescriptionByName(String name) {
        String description = (String) getPropertyByName("skills.json", name, "description", this);
        String skillInfo = "Name: " + name + "\n" + "Description: " + description;

        int id = (int) getPropertyByName("skills.json", name, "id", this);
        int currentExp = playerState.getExperienceOfSkill(id);
        int level = playerState.getLevelOfSkill(id);
        int maxExp = getMaxExperience(level);

        skillInfo = skillInfo + "\nExperience Progress " + currentExp + "/" + maxExp;
        return skillInfo;
    }

    public String getSkillBattleDescriptionByName(String name) {
        String skillInfo = name;

        int id = (int) getPropertyByName("skills.json", name, "id", this);
        int currentExp = playerState.getExperienceOfSkill(id);
        int level = playerState.getLevelOfSkill(id);
        int maxExp = getMaxExperience(level);
        int damage = battleSystem.getSkillDamage(name);
        String atkPattern = (String) getPropertyByName("skills.json", name, "atkPattern", this);

        skillInfo = skillInfo + "   LV " + level + "\nDamage: " + damage + "\nAttack Pattern: " + atkPattern + "\nExperience Progress: " + currentExp + "/" + maxExp;
        return skillInfo;
    }



    public String getSkillLevelsString() {
        int[] skillLevels = playerState.getSkillLevels();
        StringBuilder skillLevelsString = new StringBuilder();
        for (int level: skillLevels) {
            skillLevelsString.append(level).append('\n');
        }
        return String.valueOf(skillLevelsString);
    }

    /**
     * Gets the battle completion text and drop amounts.
     * @return String array with [0] containing drop names and [1] containing drop amounts
     */
    public String[] getEndBattleTextAndDropAmounts() {
        int tier = battleSystem.getEnemyTier();
        int[] dropAmounts = getEnemyDropsFromTier(tier);

        StringBuilder dropNames = new StringBuilder("Entity has been purified\n\nAnomalous Drops\n");
        StringBuilder dropAmountsString = new StringBuilder();

        processDropItem(ESSENCE_NAME, dropAmounts[0], dropNames, dropAmountsString);
        processDropItem(SHARD_NAME, dropAmounts[1], dropNames, dropAmountsString);

        return new String[]{String.valueOf(dropNames), String.valueOf(dropAmountsString)};
    }

    private void processDropItem(String itemName, int amount, StringBuilder dropNames, StringBuilder dropAmounts) {
        if (amount > 0) {
            addItemToPlayerInventory(itemName, amount);
            dropNames.append(itemName).append('\n');
            dropAmounts.append(amount);
        }
    }

    private void addItemToPlayerInventory(String itemName, int amount) {
        int itemId = (int) getPropertyByName("items.json", itemName, "id", this);
        playerState.addItem(itemId, amount);
    }

    //TODO: Call this function appropriately
    private void playerLost() {
        int playerHealth = playerState.getHealth();
        if (playerHealth <= 0) {
            int playerMaxHealth = playerState.getPlayerMaxHealth();
            playerState.modifyHealth(playerMaxHealth);

            int tokensLost = playerState.getTokensLostOnDeath();
            playerState.updateTokens(-tokensLost);
        }
    }

    public int getTokensLost() {
        return playerState.getTokensLostOnDeath();
    }

    public String getItemAmounts() {
        int[] itemAmounts = playerState.getItemAmountsList();
        StringBuilder itemAmountsString = new StringBuilder();
        for (int amount: itemAmounts) {
            itemAmountsString.append(amount).append('\n');
        }
        return String.valueOf(itemAmountsString);
    }

    public int getItemPriceByName(String name) {
        return (int) getPropertyByName("items.json", name, "price", this);
    }

    public boolean canPlayerAffordItem(String name) {
        int price = getItemPriceByName(name);
        return playerState.canUpdateTokens(-price);
    }

    public void buySingleItem(String name) {
        int price = getItemPriceByName(name);
        playerState.updateTokens(-price);
        int id = (int) getPropertyByName("items.json", name, "id", this);
        playerState.addItem(id, 1);
    }

    public String getItemDescriptionByName(String name) {
        return (String) getPropertyByName("items.json", name, "description", this);
    }

    public String getItemInfoByName(String name) {
        String description = (String) getPropertyByName("items.json", name, "description", this);
        return "Name: " + name + "\n" + "Description: " + description;
    }

    public ArrayList<String> getPlayerItemNamesArray() {
        int[] itemIds = playerState.getItemList();
        return getStringListOfDataProperty("items.json", "name", itemIds, this);
    }

    public ArrayList<String> getShopItemsArray() {
        int[] shopItemIds = {0, 1, 2, 3, 4, 5, 6};
        return getStringListOfDataProperty("items.json", "name", shopItemIds, this);
    }

    public String getEnemyDescription(int id) {
        int[] itemIds = {id};
        return getDataProperty("enemies.json", "description", itemIds, this);
    }

    public String getItemShopInfo(String name) {
        StringBuilder itemInfo = new StringBuilder();
        String description = getItemDescriptionByName(name);
        int price = getItemPriceByName(name);
        itemInfo.append('\n').append(description).append("\n\nPrice: ").append(price);
        return String.valueOf(itemInfo);
    }

    public void removePlayerSkill(String name) {
        int id = (int) getPropertyByName("skills.json", name, "id", this);
        int level = playerState.getLevelOfSkill(id);
        playerState.removeSkill(id);
        givePlayerCompensationForForgettingSkill(level);
    }

    private void givePlayerCompensationForForgettingSkill(int level) {
        getSkillCompensation(level, playerState);
    }

    public void sellPlayerItem(String name, int price) {
        int id = (int) getPropertyByName("items.json", name, "id", this);
        playerState.removeItem(id);
        playerState.updateTokens(price);
    }

    public void useItem(String name) {
        switch (name) {
            case "Health Rune I":
                useHealthRune(1);
                break;
            case "Health Rune II":
                useHealthRune(2);
                break;
            case "Life Skill Stone":
            case "Death Skill Stone":
            case "Null Skill Stone":
                useSkillStone(name);
                break;
            case "Book of Skills: Volume I":
                useBookOfSkills(1);
                break;
            case "Book of Skills: Volume II":
                useBookOfSkills(2);
                break;
            case "Book of Skills: Volume III":
                useBookOfSkills(3);
                break;
        }

        // remove item from the player's inventory
        int id = (int) getPropertyByName("items.json", name, "id", this);
        playerState.removeItem(id);
    }

    public void useHealthRune(int runeNumber) {
        ItemUtils.useHealthRune(playerState, runeNumber);
    }

    public void useSkillStone(String name) {
        SkillUtils.AnomalyTypes type;
        switch (name) {
            case "Life Skill Stone":
                type = SkillUtils.AnomalyTypes.LIFE;
                break;
            case "Death Skill Stone":
                type = SkillUtils.AnomalyTypes.DEATH;
                break;
            case "Null Skill Stone":
                type = SkillUtils.AnomalyTypes.NOTHINGNESS;
                break;
            default:
                return;
        }

        ItemUtils.useSkillStone(type, playerState);
    }

    public void useBookOfSkills(int volume) {
        String selectedSkill = view.getSelectedSkillForPlayerMenu();
        int id = (int) getPropertyByName("skills.json", selectedSkill, "id", this);
        int level = playerState.getLevelOfSkill(id);
        int exp = playerState.getExperienceOfSkill(id);
        int expGain = getSkillExpGainedForVolume(volume);

        int[] newLevelAndExp = getUpdatedLevelAndExperience(level, exp, expGain);
        playerState.setSkillLevelAndExperience(id, newLevelAndExp[0], newLevelAndExp[1]);
    }

    public boolean hasReachedMaxSkillLimit() {
        int MAX_NUMBER_OF_SKILLS = 5;
        int numberOfSkills = playerState.getSkillList().length;
        if (numberOfSkills >= MAX_NUMBER_OF_SKILLS) {
            return true;
        }
        return false;
    }

    public int getNumberOfEnemies() {
        return encounterSystem.getNumberOfEnemies();
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
