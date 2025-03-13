package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.anomalousencounters.R;

import presenter.GamePresenter;

public class GameView  extends SurfaceView implements Runnable{
    private final GamePresenter presenter;
    private Thread gameThread;
    volatile boolean isPlaying; //check if the game is running
    private final SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private final Paint paint;
    private final HealthBar healthBar;
    private final Sprite inventory;
    private final Sprite settingsIcon;
    private final PlayerSprite playerSprite;
    private final BackgroundImage backgroundImage;
    int backgroundDirection;
    private boolean isOnOverworld;
    long fps; //keeps track of frame rate

    private boolean isInBattle;

    public GameView(Context context, GamePresenter presenter){
        super(context);
        this.presenter = presenter;
        surfaceHolder = getHolder();
        paint = new Paint();

        //settingsIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.)
        Bitmap settingsIconBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings_gear);
        settingsIcon = new Sprite(settingsIconBitmap, 2235, 50);

        Bitmap inventoryBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.quick_inventory);
        inventory = new Sprite(inventoryBitmap, 950, 840);

        Bitmap healthBarBaseBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.healthbar_base);
        Bitmap healthBarHealthBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.healthbar_health);
        healthBar = new HealthBar(healthBarBaseBitmap, healthBarHealthBitmap, 790, 50);

        Bitmap skyBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.game_sky);
        Bitmap groundBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.game_map);
        backgroundImage = new BackgroundImage(skyBitmap, groundBitmap, 0, -224);
        backgroundImage.setDirection(0);

        Bitmap playerBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.player_sprite_sheet_v2);
        playerSprite = new PlayerSprite(playerBitmap, 1100, 448);
        playerSprite.setAnimation("idle");

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
//                Log.d("FPS", String.valueOf(fps));
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
                backgroundImage.update(fps);
                backgroundImage.draw(canvas, paint);

                settingsIcon.draw(canvas, paint);
                inventory.draw(canvas, paint);
                healthBar.draw(canvas, paint, presenter.getPlayerHealthPercentage());

                playerSprite.update(System.currentTimeMillis());
                playerSprite.draw(canvas);

                MenuItem menuItem = new MenuItem(200, 200, 200, 500, "SKILLS\nCosmic Gas", getContext());
                menuItem.draw(canvas, paint);
            }

            // Draw everything to the screen and unlock the drawing surface
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // User has touched the screen
            case MotionEvent.ACTION_DOWN:
                float eventX = motionEvent.getX();
                float eventY = motionEvent.getY();
                String playerMovementState = presenter.getPlayerMovementState((int) eventX, playerSprite.getX(), playerSprite.getX() + playerSprite.getSpriteWidth());
                playerSprite.setAnimation(playerMovementState);
                switch (playerMovementState){
                    case "walk_right":
                        backgroundDirection = -1;
                        break;
                    case "walk_left":
                        backgroundDirection = 1;
                        break;
                    default:
                        backgroundDirection = 0;
                }
                backgroundImage.setDirection(backgroundDirection);
                break;

            // User has removed finger from screen, so character should stop moving
            case MotionEvent.ACTION_UP:
//                Log.d("Action up debg", "");
                playerSprite.setAnimation("idle");
                backgroundImage.setDirection(0);
                break;
        }
        return true;
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
