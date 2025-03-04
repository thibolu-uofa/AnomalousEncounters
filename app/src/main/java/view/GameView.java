package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.anomalousencounters.R;

import presenter.GamePresenter;

public class GameView  extends SurfaceView implements Runnable{
    private GamePresenter presenter;
    private Thread gameThread;
    volatile boolean isPlaying; //check if the game is running
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint paint;
    private HealthBar healthBar;
    private Sprite inventory;
    private Sprite settingsIcon;
    private PlayerSprite playerSprite;
    private BackgroundImage backgroundImage;
    private boolean isOnOverworld;
    long fps; //keeps track of frame rate

    private boolean isInBattle;

    public GameView(Context context){
        super(context);
        surfaceHolder = getHolder();
        paint = new Paint();

        //settingsIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.)
        Bitmap settingsIconBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings_gear);
        settingsIcon = new Sprite(settingsIconBitmap, 2235, 50);

        isOnOverworld = true;
    }


    @Override
    public void run() {
        while (isPlaying) {
            long startFrameTime = System.currentTimeMillis();

            draw(); //draw frame

            // calculate the fps for this frame
            //used to help calculate the frame rate
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }
    }
    public void draw(){
        // make sure our drawing surface is valid or we crash
        if (surfaceHolder.getSurface().isValid()) {
            if(isOnOverworld){
                settingsIcon.draw(canvas, paint);
            }
        }
    }

    // If the Activity is paused/stopped the shutdown our thread.
    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }


    }

    // If  Activity is started then start our thread.
    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
