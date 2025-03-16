package presenter;

import static model.Utils.getDataProperty;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

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

        // Initialize gameView and set it as the view
        view = new GameView(this, this);
        setContentView(view);

        gameLogic = new GameLogic();
        encounterSystem = new EncounterSystem();

        playerState = new PlayerState("Nxy", 25); //make player name a string resource

        //TESTING PURPOSES
        playerState.addSkill(0);
        playerState.addSkill(1);
        playerState.addSkill(2);

        playerState.addItem(0);
        playerState.addItem(1);

        Log.d("Skill Names", getSkillNames());
        Log.d("Skill Description", getSkillDescription(0));
        Log.d("Item Names", getItemNames());
        Log.d("Item Description", getItemDescription(0));
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

    public String getPlayerNameHealthAndTokens() {
        String name = playerState.getName();
        String maxHealth = String.valueOf(playerState.getPlayerMaxHealth());
        String currentHealth = String.valueOf(playerState.getHealth());
        String tokens = String.valueOf(playerState.getTokens());
        return name + "\nHP: " + maxHealth + "/" + currentHealth + "\nTokens: " + tokens;
    }


    public String getSkillNames() {
        int[] skillIds = playerState.getSkillList();
        return getDataProperty("skills.json", "name", skillIds, this);
    }

    public String getSkillDescription(int skillId) {
        int[] skillIds = {skillId};
        return getDataProperty("skills.json", "description", skillIds, this);
    }

    public String getSkillLevel() {
        int[] skillIds = playerState.getSkillList();
        StringBuilder skillLevels = new StringBuilder();
        for (int id: skillIds) {
            skillLevels.append(1).append('\n');
        }
        return String.valueOf(skillLevels);
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
        int[] itemIds = playerState.getItemList();
        StringBuilder itemAmounts = new StringBuilder();
        for (int id: itemIds) {
            itemAmounts.append(12).append('\n');
        }
        return String.valueOf(itemAmounts);
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
