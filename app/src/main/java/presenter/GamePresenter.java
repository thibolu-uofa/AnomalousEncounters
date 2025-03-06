package presenter;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import model.GameLogic;
import view.GameView;

public class GamePresenter extends AppCompatActivity {
    private GameView view;
    private GameLogic gameLogic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        view = new GameView(this, this);
        setContentView(view);

        gameLogic = new GameLogic();
    }

    public float getPlayerHealthPercentage() {
        int max_health = 25;
        int health = 20;
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
