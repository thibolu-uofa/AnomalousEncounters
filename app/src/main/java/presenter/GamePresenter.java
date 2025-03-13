package presenter;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import model.GameLogic;
import model.PlayerState;
import view.GameView;

public class GamePresenter extends AppCompatActivity {
    private GameView view;
    private GameLogic gameLogic;
    private PlayerState playerState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        view = new GameView(this, this);
        setContentView(view);

        gameLogic = new GameLogic();

        playerState = new PlayerState("Nxy", 25);
        //TESTING PURPOSES
        playerState.addSkill(0);
        playerState.addSkill(1);
        playerState.addSkill(2);

        getSkillNames();
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

    public String getPlayerNameHealthAndTokens() {
        String name = playerState.getName();
        String maxHealth = String.valueOf(playerState.getPlayerMaxHealth());
        String currentHealth = String.valueOf(playerState.getHealth());
        String tokens = String.valueOf(playerState.getTokens());
        return name + "\nHP: " + maxHealth + "/" + currentHealth + "\nTokens: " + tokens;
    }

    public String getSkillNames() throws JSONException {
        int[] skills = playerState.getSkillList();
        StringBuilder skillNames = new StringBuilder();
        JSONArray skillJsonArray = loadJsonArrayFromFile("skills.json");
        for (int skillId: skills) {
            skillNames.append(skillJsonArray.getJSONObject(skillId).getString("name")).append("\n");
        }
        return String.valueOf(skillNames);
    }

    private JSONArray loadJsonArrayFromFile(String filename) {
        try (InputStream inputStream = this.getAssets().open(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder skillString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                skillString.append(line);
            }

            reader.close();
            inputStream.close();
            return new JSONArray(skillString.toString());
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }



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
