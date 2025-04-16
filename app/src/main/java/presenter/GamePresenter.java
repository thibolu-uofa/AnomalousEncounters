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
import static model.Utils.getSingleDataPropertyFromJSONArray;
import static model.Utils.getStringListOfDataProperty;
import static model.Utils.loadJsonArrayFromFileOnDevice;
import static model.Utils.saveJSONArrayOnUserDevice;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.anomalousencounters.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import model.BattleSystem;
import model.EncounterSystem;
import model.EnemyState;
import model.GameLogic;
import model.ItemUtils;
import model.PlayerState;
import model.Skill;
import model.SkillUtils;
import model.SoundUtils;
import model.Utils;
import view.GameView;

public class GamePresenter extends AppCompatActivity {
    private GameView view;
    private GameLogic gameLogic;
    private PlayerState playerState;
    private EncounterSystem encounterSystem;
    private BattleSystem battleSystem;
    private SoundUtils soundUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideNavAndStatusBar();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);
        applyWindowInsets();

        initializeGameComponents();

        setUpAllMainMenuListeners();
        
        soundUtils.playMusic(this,"menu_theme.wav", true);
    }

    private void hideNavAndStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setUpAllMainMenuListeners() {
        setupStartButtonListeners();
        setupContinueGameListeners();
        setUpSettingsListener();
        setUpHowToPlayListeners();
    }

    private void initializeGameComponents() {
        playerState = new PlayerState();
        gameLogic = new GameLogic();
        encounterSystem = new EncounterSystem();
        view = new GameView(this, this);
        soundUtils = new SoundUtils();
    }

    public void changeViewBackToMainActivity() {
        view.pause();
        soundUtils.stopMusic();
        setContentView(R.layout.activity_main);
        applyWindowInsets();

        // Apply fade-in animation to main menu
        View mainView = findViewById(android.R.id.content);
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
        mainView.startAnimation(fadeIn);

        fadeIn.setAnimationListener(new TransitionListener(() -> {
            soundUtils.playMusic(this,"menu_theme.wav", true);
        }));

        setUpAllMainMenuListeners();
        view.resume();
    }

    private void setupStartButtonListeners() {
        ImageView startNewGame = findViewById(R.id.startButton);
        startNewGame.setOnClickListener(v -> {
            soundUtils.playSelectSound(this);

            setContentView(R.layout.choose_affinity);
            setupGoBackListeners();
            setUpContinueToNewGameListener();
            setUpAffinityRuneListeners();
        });
    }

    private void setUpHowToPlayListeners() {
        ImageView howToPlayButton = findViewById(R.id.howToPlayButton);
        howToPlayButton.setOnClickListener(v -> {
            String url = "https://www.canva.com/design/DAGkWRTx7o0/3DdCiVSnFC10WRxepmU4sA/view";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }

    private void setupContinueGameListeners() {
        ImageView continueGame = findViewById(R.id.continueButton);
        continueGame.setOnClickListener(v -> {
            soundUtils.playConfirmSound(this);
            setContentView(R.layout.save_slots);
            setupGoBackListeners();
            initializeSaveSlots();
        });
    }

    private void setUpSettingsListener() {
        ImageView settingsIcon = findViewById(R.id.settings_icon);
        settingsIcon.setOnClickListener(v -> {
            soundUtils.playSelectSound(this);
            setContentView(R.layout.game_settings);

            int musicVolume = soundUtils.getMusicVolume();
            SeekBar musicSeekBar = findViewById(R.id.musicSeekBar);
            musicSeekBar.setProgress(musicVolume);
            updateMusicSliderAndText(musicVolume);

            int soundVolume = soundUtils.getSoundVolume();
            SeekBar soundSeekBar = findViewById(R.id.sfxSeekBar);
            soundSeekBar.setProgress(soundVolume);
            updateSoundText(soundVolume);

            setupGoBackListeners();
            setUpMusicSliderListeners();
            setUpSoundSliderListeners();
        });
    }

    private void setupGoBackListeners() {
        ImageView backButton = findViewById(R.id.back_icon);
        backButton.setOnClickListener(v -> {
            soundUtils.playSelectSound(this);
            setContentView(R.layout.activity_main);
            setUpAllMainMenuListeners();
        });
    }

    private void initializeSaveSlots(){
        JSONArray jsonArray = loadJsonArrayFromFileOnDevice("save_slots.json", this);
        int numberOfSaveSlots = jsonArray.length();
        if (numberOfSaveSlots == 0){
            return;
        }

        View mainView = findViewById(android.R.id.content);
        ImageView[] saveSlots = {findViewById(R.id.slot_image1), findViewById(R.id.slot_image2), findViewById(R.id.slot_image3)};
        TextView[] saveSlotTexts = {findViewById(R.id.slot_text1), findViewById(R.id.slot_text2), findViewById(R.id.slot_text3)};

        for (int i = 0; i < numberOfSaveSlots; i++){
            String text = "Save\nSlot " + (i + 1);
            saveSlotTexts[i].setText(text);

            ImageView saveSlot = saveSlots[i];
            setUpSaveSlotListener(saveSlot, mainView, i);
        }
    }

    private void setUpSaveSlotListener(ImageView saveSlot, View mainView, int finalI) {
        saveSlot.setOnClickListener(v -> {
            soundUtils.playSelectSound(this);
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_animation);

            mainView.startAnimation(fadeOut);
            soundUtils.stopMusic();

            fadeOut.setAnimationListener(new TransitionListener(() -> {
                loadSaveSlot(finalI);
                setContentView(view);
                view.startFadeIn();
                soundUtils.playMusic(this,"fallen_down.wav", true);
            }));
        });
    }

    private void setUpMusicSliderListeners() {
        SeekBar volumeSlider = findViewById(R.id.musicSeekBar);

        volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateMusicSliderAndText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateMusicSliderAndText(int progress) {
        float volumeProgress = (float) progress;
        float volume = volumeProgress/100;
        soundUtils.setMusicVolume(volume);

        //update text of music progress
        TextView volumeText = findViewById(R.id.musicText);
        String volumeString = "Music " + progress + "%";
        volumeText.setText(volumeString);
    }


    private void setUpSoundSliderListeners() {
        SeekBar volumeSlider = findViewById(R.id.sfxSeekBar);

        volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSoundText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateSoundText(int progress) {
        float volumeProgress = (float) progress;
        float volume = volumeProgress/100;
        soundUtils.setSoundEffectsVolume(volume);

        //update text of music progress
        TextView volumeText = findViewById(R.id.sfxText);
        String volumeString = "Sound " + progress + "%";
        volumeText.setText(volumeString);
    }

    private void setUpContinueToNewGameListener() {
        TextView continueText = findViewById(R.id.confirm_new_game);
        View mainView = findViewById(android.R.id.content);

        continueText.setOnClickListener(v -> {
            soundUtils.playSelectSound(this);

            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_animation);
            mainView.startAnimation(fadeOut);
            soundUtils.stopMusic();

            fadeOut.setAnimationListener(new TransitionListener(() -> {
                makeNewPlayer();
                setContentView(view);
                view.startNewGame();
                view.startFadeIn();
                soundUtils.playMusic(this,"fallen_down.wav", true);
            }));
        });
    }

    private void setUpAffinityRuneListeners() {

        ImageView[] affinityRuneImages = {findViewById(R.id.life_rune), findViewById(R.id.null_rune), findViewById(R.id.death_rune)};
        SkillUtils.AnomalyTypes[] affinityTypes = {SkillUtils.AnomalyTypes.LIFE, SkillUtils.AnomalyTypes.NOTHINGNESS, SkillUtils.AnomalyTypes.DEATH};

        for (int i = 0; i < affinityTypes.length; i++) {
            ImageView affinityImage = affinityRuneImages[i];
            SkillUtils.AnomalyTypes affinity = affinityTypes[i];
            setUpRuneImageListeners(affinityImage, affinity);
        }
    }

    private void setUpRuneImageListeners(ImageView runeImage, SkillUtils.AnomalyTypes affinity) {
        TextView chosenAffinity = findViewById(R.id.chosen_affinity_text);

        runeImage.setOnClickListener(v -> {
            soundUtils.playSelectSound(this);

            playerState.setAffinity(affinity);

            String affinityName = affinity.toString().toLowerCase();
            affinityName = affinityName.substring(0, 1).toUpperCase() + affinityName.substring(1);
            String chosenAffinityText = "Affinity: " + affinityName;

            chosenAffinity.setText(chosenAffinityText);
        });
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void playOverworldMusic() {
        soundUtils.playMusic(this, "fallen_down.wav", true);
    }

    private void makeNewPlayer() {
        int newPlayerIndex = 0;
        String name = (String) getSingleDataProperty("player_config.json", "name", newPlayerIndex, this);
        int maxHealth = (int) getSingleDataProperty("player_config.json", "maxHealth", newPlayerIndex, this);
        int tokens = (int) getSingleDataProperty("player_config.json", "tokens", newPlayerIndex, this);

        SkillUtils.AnomalyTypes affinity = playerState.getAffinity(); //get affinity from pre-loaded player
        playerState = new PlayerState(name, maxHealth, tokens);

        JSONArray items = (JSONArray) getSingleDataProperty("player_config.json", "items", newPlayerIndex, this);
        loadPlayerItems(items);

        getRandomStartingSkills(affinity);
    }

    private void getRandomStartingSkills(SkillUtils.AnomalyTypes type) {
        // give player 3 starting skills
        ItemUtils.useSkillStone(type, playerState);
        ItemUtils.useSkillStone(type, playerState);
        ItemUtils.useSkillStone(type, playerState);
    }

    private void loadPlayerItems(JSONArray items) {
        try {
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                int itemId = item.getInt("id");
                int itemAmount = item.getInt("amount");
                playerState.addItem(itemId, itemAmount);
            }
        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON file", e);
            throw new RuntimeException(e);
        }
    }

    private void loadPlayerSkills(JSONArray skills) {
        try {
            for (int i = 0; i < skills.length(); i++) {
                JSONObject skill = skills.getJSONObject(i);
                int skillId = skill.getInt("id");
                int skillLevel = skill.getInt("level");
                int skillExp = skill.getInt("experience");
                playerState.addSkill(skillId, skillLevel, skillExp);
            }

        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON files", e);
            throw new RuntimeException(e);
        }
    }

    public void loadSaveSlot(int index) {
        JSONArray jsonArray = loadJsonArrayFromFileOnDevice("save_slots.json", this);

        String name = (String) getSingleDataPropertyFromJSONArray(jsonArray, "name", index, this);
        int maxHealth = (int) getSingleDataPropertyFromJSONArray(jsonArray, "maxHealth", index, this);
        int health = (int) getSingleDataPropertyFromJSONArray(jsonArray, "health", index, this);
        int tokens = (int) getSingleDataPropertyFromJSONArray(jsonArray, "tokens", index, this);
        playerState = new PlayerState(name, maxHealth, tokens);
        playerState.setPlayerCurrentHealth(health);

        JSONArray items = (JSONArray) getSingleDataPropertyFromJSONArray(jsonArray, "items", index, this);
        loadPlayerItems(items);

        JSONArray skills = (JSONArray) getSingleDataPropertyFromJSONArray(jsonArray, "skills", index, this);
        loadPlayerSkills(skills);
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

    public String getEnemyNameHealthAndTier() {
        EnemyState enemyState = battleSystem.getEnemyState();
        String name = enemyState.getName();
        int tier = enemyState.getTier();
        String maxHealth = String.valueOf(enemyState.getEnemyMaxHealth());
        String currentHealth = String.valueOf(enemyState.getEnemyCurrentHealth());
        return name + "\nTier: " + tier + "\nHP: " + currentHealth + "/" + maxHealth;
    }

    public void setUpBattle(int enemyId, int enemyTier) {
        battleSystem = new BattleSystem(this, playerState, enemyId, enemyTier,this);

        // tell view to fade out
        view.startBattleTransition();
        soundUtils.stopMusic();
    }

    public void finishedBattleFadeOutTransition() {
        view.displayBattle();

        // tell view what enemy image to use
        int enemyId = battleSystem.getEnemyId();
        Bitmap enemyImage = getEnemyImage(enemyId, this);
        view.setEnemyImage(enemyImage);

        updatePlayerAndEnemyPositions();

        view.startFadeIn();
        soundUtils.playMusic(this,"battle_theme.wav", true);
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
            soundUtils.stopMusic();
        }
    }

    public void checkIfPlayerLoser() {
        boolean isPlayerLoser = isPlayerLoser();
        if (isPlayerLoser) {
            battleSystem.playerSkillExperience();
            view.endBattle(false);
            playerLost();
            soundUtils.stopMusic();
        }
    }

    public void chargeBattleFleeFee(){
        int fee = 5;
        playerState.updateTokens(-fee);
    }

    public boolean hasPlayerProgressedPhase() {
        return Utils.hasPlayerProgressedPhase(playerState);
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
        int[] shopItemIds = {0, 1, 2, 3, 4, 5, 6, 7};
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
        itemInfo.append('\n').append(description).append("\n\nPrice: ").append(price).append(" tokens");
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

    public boolean hasMaxHealth() {
        int maxHealth = playerState.getPlayerMaxHealth();
        int currentHealth = playerState.getHealth();
        return currentHealth == maxHealth;
    }

    public boolean hasReachedMaxSkillLimit() {
        int MAX_NUMBER_OF_SKILLS = 5;
        int numberOfSkills = playerState.getSkillList().length;
        if (numberOfSkills >= MAX_NUMBER_OF_SKILLS) {
            return true;
        }
        return false;
    }

    public boolean hasMinAmountOfSkills() {
        int MIN_NUMBER_OF_SKILLS = 3;
        int numberOfSkills = playerState.getSkillList().length;
        if (numberOfSkills <= MIN_NUMBER_OF_SKILLS) {
            return true;
        }
        return false;
    }

    public int getNumberOfEnemies() {
        return encounterSystem.getNumberOfEnemies();
    }

    public void makeFirstSaveSlot() {
        try {
            JSONArray jsonArray = new JSONArray();

            JSONObject newPlayer = createPlayerSaveObject();
            jsonArray.put(newPlayer);

            saveJSONArrayOnUserDevice(jsonArray, this);
        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON files", e);
            throw new RuntimeException(e);
        }
    }

    public boolean hasMaxSaves() {
        JSONArray jsonArray = loadJsonArrayFromFileOnDevice("save_slots.json", this);
        int numberOfSaveSlots = jsonArray.length();
        int MAX_SAVE_SLOTS = 3;
        return numberOfSaveSlots >= MAX_SAVE_SLOTS;
    }

    public void addSaveSlot() {
        try {
            String filename = "save_slots.json";
            JSONArray existingSlots = loadJsonArrayFromFileOnDevice(filename, this);

            JSONObject newPlayer = createPlayerSaveObject();
            existingSlots.put(newPlayer);

            saveJSONArrayOnUserDevice(existingSlots, this);
        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON files", e);
            throw new RuntimeException(e);
        }
    }

    public int getIndexOfSaveSlot(String saveSlot) {
        char num = saveSlot.charAt(saveSlot.length() - 1);
        return Character.getNumericValue(num);
    }

    public void overwriteSaveSlot(int index) {
        try {
            String filename = "save_slots.json";
            JSONArray existingSlots = loadJsonArrayFromFileOnDevice(filename, this);

            JSONObject newPlayer = createPlayerSaveObject();

            if (index >= 0 && index < existingSlots.length()) {
                existingSlots.put(index, newPlayer); // overwrites at given index
                saveJSONArrayOnUserDevice(existingSlots, this);
            } else {
                throw new IndexOutOfBoundsException("Error: invalid save slot index: " + index);
            }

        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON files", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteSaveSlot(int index) {
        String filename = "save_slots.json";
        JSONArray existingSlots = loadJsonArrayFromFileOnDevice(filename, this);
        if (index >= 0 && index < existingSlots.length()) {
            existingSlots.remove(index);
            saveJSONArrayOnUserDevice(existingSlots, this);
        } else {
            throw new IndexOutOfBoundsException("Error: invalid save slot index: " + index);
        }
    }

    private JSONObject createPlayerSaveObject() throws JSONException {
        JSONObject newPlayer = new JSONObject();

        newPlayer.put("name", playerState.getName());
        newPlayer.put("health", playerState.getHealth());
        newPlayer.put("maxHealth", playerState.getPlayerMaxHealth());
        newPlayer.put("tokens", playerState.getTokens());
        newPlayer.put("phase", playerState.getPhase());

        // Items
        JSONArray items = new JSONArray();
        for (int[] item : playerState.getItems()) {
            JSONObject itemObj = new JSONObject();
            itemObj.put("id", item[0]);
            itemObj.put("amount", item[1]);
            items.put(itemObj);
        }
        newPlayer.put("items", items);

        // Skills
        JSONArray skills = new JSONArray();
        for (int[] skill : playerState.getSkills()) {
            JSONObject skillObj = new JSONObject();
            skillObj.put("id", skill[0]);
            skillObj.put("level", skill[1]);
            skillObj.put("experience", skill[2]);
            skills.put(skillObj);
        }
        newPlayer.put("skills", skills);

        return newPlayer;
    }

    public boolean hasSaveFile() {
        String filename = "save_slots.json";
        File file = new File(getFilesDir(), filename);
        return file.exists();
    }

    public ArrayList<String> getSaveSlotList() {
        ArrayList<String> saveSlotList = new ArrayList<>();
        JSONArray jsonArray = loadJsonArrayFromFileOnDevice("save_slots.json", this);
        int numberOfSaveSlots = jsonArray.length();

        for (int i = 1; i < numberOfSaveSlots + 1; i++) {
            String saveSlot = "Save Slot " + i;
            saveSlotList.add(saveSlot);
        }
        return saveSlotList;
    }

    public String getSaveFileInfo(int index) {
        JSONArray jsonArray = loadJsonArrayFromFileOnDevice("save_slots.json", this);

        String name = (String) getSingleDataPropertyFromJSONArray(jsonArray, "name", index, this);
        int health = (int) getSingleDataPropertyFromJSONArray(jsonArray, "health", index, this);
        int maxHealth = (int) getSingleDataPropertyFromJSONArray(jsonArray, "maxHealth", index, this);
        int tokens = (int) getSingleDataPropertyFromJSONArray(jsonArray, "tokens", index, this);
        int phase = (int) getSingleDataPropertyFromJSONArray(jsonArray, "phase", index, this);

        JSONArray items = (JSONArray) getSingleDataPropertyFromJSONArray(jsonArray, "items", index, this);
        JSONArray skills = (JSONArray) getSingleDataPropertyFromJSONArray(jsonArray, "skills", index, this);

        String info = String.format(Locale.ENGLISH, "%s \nHP: %d/%d | Tokens: %d | Phase: %d",
                name, health, maxHealth, tokens, phase);

        String skillsInfo = getSkillsListForSaveFileInfo(skills);
        String itemsInfo = getItemsListForSaveFile(items);

        return info + "\n\n" + skillsInfo + "\n\n" + itemsInfo;
    }

    private String getItemsListForSaveFile(JSONArray items) {
        if (items == null || items.length() == 0) {
            return "Items: None";
        }

        StringBuilder itemsSb = new StringBuilder("Items: \n");
        try {
            int displayCount = items.length();
            for (int i = 0; i < displayCount; i++) {
                JSONObject item = items.getJSONObject(i);
                int itemId = item.getInt("id");
                int amount = item.getInt("amount");
                String name = (String) getSingleDataProperty("items.json", "name", itemId, this);

                itemsSb.append(name).append("(").append(amount).append(")");

                if (i < displayCount - 1) {
                    itemsSb.append(", ");
                }
            }
        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON files", e);
            return "Items: Error loading";
        }

        return itemsSb.toString();
    }

    private String getSkillsListForSaveFileInfo(JSONArray skills) {
        if (skills == null || skills.length() == 0) {
            return "Skills: None";
        }

        StringBuilder skillsSb = new StringBuilder("Skills: \n");
        try {
            int displayCount = skills.length();
            for (int i = 0; i < displayCount; i++) {
                JSONObject skill = skills.getJSONObject(i);
                int skillId = skill.getInt("id");
                int level = skill.getInt("level");
                String name = (String) getSingleDataProperty("skills.json", "name", skillId, this);

                skillsSb.append(name).append(" Lv").append(level);

                if (i < displayCount - 1) {
                    skillsSb.append(", ");
                }
            }
        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON files", e);
            return "Skills: Error loading";
        }

        return skillsSb.toString();
    }

    public void playSelectSound() {
        soundUtils.playSelectSound(this);
    }

    public void playConfirmSound() {
        soundUtils.playConfirmSound(this);
    }

    public void playCancelSound() {
        soundUtils.playCancelSound(this);
    }

    public void playGetHitSound() {
        soundUtils.playGetHitSound(this);
    }

    public void playHitEnemySound() {
        soundUtils.playHitEnemySound(this);
    }

    // This method executes when the user continues the game
    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
        soundUtils.resumeMusic();
    }

    // This method executes when the user quits the game
    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
        soundUtils.pauseMusic();
    }

    private class TransitionListener implements Animation.AnimationListener {
        private final Runnable onAnimationEndAction;

        public TransitionListener(Runnable onAnimationEndAction) {
            this.onAnimationEndAction = onAnimationEndAction;
        }

        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            onAnimationEndAction.run();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }
}
