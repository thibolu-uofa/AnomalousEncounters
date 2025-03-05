package presenter;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.anomalousencounters.R;
import view.GameView;

public class GamePresenter extends AppCompatActivity {
    private GameView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize gameView and set it as the view
        view = new GameView(this);
        setContentView(view);
    }

    public float getPlayerHealthPercentage() {
        int max_health = 25;
        int health = 20;
        return (float) health /max_health;
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
