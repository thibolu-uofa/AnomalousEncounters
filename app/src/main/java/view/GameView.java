package view;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import presenter.GamePresenter;

public class GameView {
    private GamePresenter presenter;
    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private boolean isPlaying;
    private Canvas canvas;
    private HealthBar healthBar;
    private Sprite inventory;
    private Sprite SettingsIcon;
    private PlayerSprite playerSprite;
    private BackgroundImage backgroundImage;

    public GameView(){

    }


}
