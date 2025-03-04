package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private final SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private final Paint paint;
    private HealthBar healthBar;
    private Sprite inventory;
    private final Sprite settingsIcon;
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
            canvas = surfaceHolder.lockCanvas(); // Lock the canvas ready to draw and make the drawing surface our canvas object

            int backgroundColor = Color.argb(255, 255, 255, 255);
            canvas.drawColor(backgroundColor); // draw the background color
            paint.setColor(Color.argb(255,  255, 255, 255)); // choose the brush color for drawing

            if(isOnOverworld){
                settingsIcon.draw(canvas, paint);
            }

            // Draw everything to the screen and unlock the drawing surface
            surfaceHolder.unlockCanvasAndPost(canvas);
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
