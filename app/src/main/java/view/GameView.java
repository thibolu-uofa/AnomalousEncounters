/**
 * This class includes code adapted from:
 * Source: https://gamecodeschool.com/android/building-a-simple-game-engine/
 * Accessed: March 2nd, 2025
 * Borrowed elements: documentation for setting up the Canvas, Paint, Thread and Surface Holder,
 * creating a Bitmap, drawing to the canvas, and implementation of the run, pause, and resume
 * functions
 */

package view;

import static view.ViewConstants.SCREEN_WIDTH;
import static view.ViewConstants.SCREEN_HEIGHT;
import static view.ViewConstants.CANVAS_WIDTH;
import static view.ViewConstants.CANVAS_HEIGHT;
import static view.ViewConstants.ENEMY_TILE_HIGHLIGHT_COLOR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.anomalousencounters.R;

import java.util.ArrayList;

import presenter.GamePresenter;
import view.battle.BattleView;
import view.battle.EndBattleScreen;

public class GameView  extends SurfaceView implements Runnable{
    private final GamePresenter presenter;
    private Thread gameThread;
    volatile boolean isPlaying; //check if the game is running
    private final SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private final Paint paint;
    private final HealthBar healthBar;
    private final Sprite inventory, settingsIcon, shopIcon, indexIcon;
    private final PlayerSprite playerSprite;
    private final BackgroundImage backgroundImage;
    private PlayerMenu playerMenu;
    private ShopMenu shopMenu;
    private BattleView battleView;
    private EndBattleScreen endBattleScreen;
    int backgroundDirection;
    private boolean isOnOverworld = true;
    private boolean canPlayerMove = true;
    long fps; //keeps track of frame rate
    private long lastEnemyEncounterCheck = 0;
    private boolean isMenuOpen = false;

    public GameView(Context context, GamePresenter presenter){
        super(context);
        this.presenter = presenter;
        surfaceHolder = getHolder();
        paint = new Paint();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = displayMetrics.widthPixels;
        SCREEN_HEIGHT = displayMetrics.heightPixels;

        Bitmap settingsIconBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings_icon);
        int iconsX = SCREEN_WIDTH - settingsIconBitmap.getWidth() - 25;
        settingsIcon = new Sprite(settingsIconBitmap, iconsX, 70);

        Bitmap indexIconBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.book_icon);
        indexIcon = new Sprite(indexIconBitmap, iconsX, settingsIcon.getY() + settingsIconBitmap.getHeight() + 20);

        Bitmap shopIconBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.shop_icon);
        shopIcon = new Sprite(shopIconBitmap, iconsX, indexIcon.getY() + indexIconBitmap.getHeight() + 20);

        Bitmap inventoryBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.quick_inventory);
        int inventoryX = SCREEN_WIDTH/2 - inventoryBitmap.getWidth()/2;
        int inventoryY = (int) (SCREEN_HEIGHT*0.75);
        inventory = new Sprite(inventoryBitmap, inventoryX, inventoryY);

        Bitmap healthBarBaseBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.healthbar_base);
        Bitmap healthBarHealthBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.healthbar_health);
        int healthBarX = SCREEN_WIDTH/2 - healthBarHealthBitmap.getWidth()/2;
        healthBar = new HealthBar(healthBarBaseBitmap, healthBarHealthBitmap, healthBarX, 50);


        Bitmap skyBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.game_sky);
        Bitmap groundBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.game_map);
        int backgroundY = -(SCREEN_HEIGHT/5);

        Bitmap playerBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.player_sprite_sheet_v2);
        int playerX = SCREEN_WIDTH/2 - playerBitmap.getWidth()/12;
        int playerY = (int) (backgroundY + (groundBitmap.getHeight() * 0.65) - ((double) playerBitmap.getHeight()/4)) + 5;

        backgroundImage = new BackgroundImage(skyBitmap, groundBitmap, backgroundY, playerX);
        backgroundImage.setDirection(0);

        playerSprite = new PlayerSprite(playerBitmap, playerX, playerY);
        playerSprite.setAnimation("idle");

        playerMenu = new PlayerMenu(presenter, getContext());
        shopMenu = new ShopMenu(presenter, getContext());
    }

    /**
     * This function will run the game loop that draws frames and calculates the fps.
     * The implementation of this function has code adapted from:
     * Source: <a href="https://gamecodeschool.com/android/building-a-simple-game-engine/">...</a>
     *
     */
    @Override
    public void run() {
        while (isPlaying) {
            long startFrameTime = System.currentTimeMillis();

            drawOnCanvas(); //draw frame

            // calculate the fps for this frame
            //used to help calculate the frame rate
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    public void drawOnCanvas(){
        // make sure our drawing surface is valid or we crash
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas(); // Lock the canvas ready to draw and make the drawing surface our canvas object
            CANVAS_WIDTH = canvas.getWidth();
            CANVAS_HEIGHT = canvas.getHeight();

            drawBackground();

            if (isOnOverworld){
                drawOverworldElements();
            }

            if (battleView != null) {
                battleView.updateMenuTexts();
                battleView.draw(canvas, paint);
            }

            if (endBattleScreen != null) {
                endBattleScreen.draw(canvas, paint);
            }

            // draw everything to the screen and unlock the drawing surface
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBackground() {
        int backgroundColor = Color.argb(255, 255, 255, 255);
        canvas.drawColor(backgroundColor); // draw the background color
        paint.setColor(Color.argb(255,  255, 255, 255)); // choose the brush color for drawing
    }

    private void drawOverworldElements() {
        long currentTime = System.currentTimeMillis();

        backgroundImage.update(fps, canPlayerMove);
        backgroundImage.draw(canvas, paint);

        // only check enemies every 500 millisecond
        if (currentTime - lastEnemyEncounterCheck >= 500) {
            presenter.hasPlayerEncounteredEnemy(backgroundImage.getX());
            lastEnemyEncounterCheck = currentTime;
        }

        settingsIcon.draw(canvas, paint);
        indexIcon.draw(canvas, paint);
        shopIcon.draw(canvas, paint);
        inventory.draw(canvas, paint);

        healthBar.draw(canvas, paint, presenter.getPlayerHealthPercentage());

        playerSprite.update(currentTime, canPlayerMove);
        playerSprite.draw(canvas);

        if (!playerMenu.isMenuClosed()) {
            playerMenu.updateMenuTexts(presenter);
            playerMenu.draw(canvas, paint);
        }

        if (!shopMenu.isMenuClosed()) {
            shopMenu.updateMenuTexts(presenter);
            shopMenu.draw(canvas, paint);
        }

        IndexMenu indexMenu = new IndexMenu(presenter, getContext());
        indexMenu.draw(canvas, paint);
    }


    /**
     * Handles touch events on the game screen.
     * Processes ACTION_DOWN events to detect inventory interactions and
     * updates player animations based on touch position.
     * Processes ACTION_UP events to reset player to idle state when touch is released.
     * @param motionEvent The MotionEvent object containing touch data
     * @return Always returns true to indicate the event was handled
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // User has touched the screen
            case MotionEvent.ACTION_DOWN:
                float eventX = motionEvent.getX();
                float eventY = motionEvent.getY();

                Log.d("Get Player/Background Position", "The Background X1 Position" + backgroundImage.getX());

                if (isOnOverworld) {
                    checkIfInventoryOpened(eventX, eventY);
                    checkIfShopOpened(eventX, eventY);
                    if (!playerMenu.isMenuClosed()) {
                        playerMenu.checkForUserTouch(eventX, eventY, presenter);
                    }
                    if (!shopMenu.isMenuClosed()) {
                        shopMenu.checkForUserTouch(eventX, eventY, presenter);
                    }
                    if (hasInventoryBeenClosed(eventX, eventY) || hasShopBeenClosed(eventX, eventY)) {
                        isMenuOpen = false;
                        break;
                    }
                    updatePlayerAnimation((int) eventX);
                }

                if (battleView != null) {
                    battleView.checkForUserTouch(eventX, eventY, presenter);
                }

                if (endBattleScreen != null && hasEndBattleScreenBeenClosed(eventX, eventY)) {
                    closeEndBattleInfo();
                }

                break;

            // user has removed finger from screen, so character should stop moving
            case MotionEvent.ACTION_UP:
                if (isOnOverworld) {
                    playerSprite.setAnimation("idle");
                    backgroundImage.setDirection(0);
                }
                break;
        }
        return true;
    }

    private void checkIfInventoryOpened(float eventX, float eventY) {
        if (isMenuOpen){
            return;
        }
        if (inventory.hasBeenTouched(eventX, eventY, presenter, 3)) {
            isMenuOpen = true;
            playerMenu = new PlayerMenu(presenter, getContext());
            playerMenu.openMenu();
            canPlayerMove = false;
        }
    }

    private void checkIfShopOpened(float eventX, float eventY) {
        if(isMenuOpen){
            return;
        }
        if (shopIcon.hasBeenTouched(eventX, eventY, presenter, 1)) {
            isMenuOpen = true;
            shopMenu = new ShopMenu(presenter, getContext());
            shopMenu.openMenu();
            canPlayerMove = false;
        }
    }

    private boolean hasInventoryBeenClosed(float eventX, float eventY) {
        if (playerMenu.isMenuClosed()) {
            return false;
        }

        if (playerMenu.hasClosedMenu(eventX, eventY, presenter)) {
            playerMenu.closeMenu();
            canPlayerMove = true;
            return true;
        }
        return false;
    }

    private boolean hasShopBeenClosed(float eventX, float eventY) {
        if (shopMenu.isMenuClosed()) {
            return false;
        }
        if (shopMenu.hasClosedMenu(eventX, eventY, presenter)) {
            shopMenu.closeMenu();
            canPlayerMove = true;
            return true;
        }
        return false;
    }

    private boolean hasEndBattleScreenBeenClosed(float eventX, float eventY) {
        return endBattleScreen.hasPressedContinueButton(eventX, eventY, presenter);
    }

    private void updatePlayerAnimation(int eventX) {
        int playerX = playerSprite.getX();
        int playerX2 = playerX + playerSprite.getSpriteWidth();
        String playerMovementState = presenter.getPlayerMovementState(eventX, playerX, playerX2);
        playerSprite.setAnimation(playerMovementState);

        updateBackgroundDirection(playerMovementState);
    }

    private void updateBackgroundDirection(String playerMovementState) {
        backgroundDirection = presenter.getBackgroundDirection(playerMovementState);
        backgroundImage.setDirection(backgroundDirection);
    }

    public void displayOverworld() {
        isOnOverworld = true;
        canPlayerMove = true;
    }

    public void displayBattle() {
        battleView = new BattleView(this.getContext(), presenter, this);
        isOnOverworld = false;
        canPlayerMove = false;
    }

    public void fleeFromBattle(){
        presenter.chargeBattleFleeFee();
        handleEndBattle();
    }

    public void endBattle(boolean isPlayerWinner){
        displayEndBattleInfo(isPlayerWinner);
    }

    public void displayEndBattleInfo(boolean isPlayerWinner){
        endBattleScreen = new EndBattleScreen(isPlayerWinner, this.getContext(), presenter);
    }

    public void closeEndBattleInfo() {
        endBattleScreen = null;
        handleEndBattle();
    }

    private void handleEndBattle(){
        battleView = null;
        //prevents player from immediately encountering another enemy
        int stopGap = 5000;
        lastEnemyEncounterCheck = System.currentTimeMillis() + stopGap;
        displayOverworld();
    }

    public int getBoardWidth() {
        if (battleView == null) {
            return 0;
        }
        return battleView.getBoardWidth();
    }

    public int getBoardHeight() {
        if (battleView == null) {
            return 0;
        }
        return battleView.getBoardHeight();
    }

    public  void updatePlayerGridPosition(int x, int y) {
        battleView.updatePlayerPosition(x, y);
    }

    public  void updateEnemyGridPosition(int x, int y) {
        battleView.updateEnemyPosition(x, y);
    }

    public void setEnemyImage(Bitmap enemyImage) {
        battleView.setEnemyIcon(enemyImage);
    }

    public void resetActionFlags() {
        battleView.resetActionFlags();
    }

    public void animateEnemySkill(ArrayList<int[]> tileList) {
        battleView.setTilesToHighlight(tileList);
        battleView.setTileHighlightColor(ENEMY_TILE_HIGHLIGHT_COLOR);
        battleView.flashTiles();
    }

    public String getChosenPlayerSkill() {
        return battleView.getChosenPlayerSkill();
    }
    /**
     * Function to shutdown our thread when the activity if paused or stopped
     * The implementation of this function comes from:
     * Source: <a href="https://gamecodeschool.com/android/building-a-simple-game-engine/">...</a>
     */
    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }


    }

    /**
     * Function to start our thread when the Activity is started
     * The implementation of this function comes from:
     * Source: <a href="https://gamecodeschool.com/android/building-a-simple-game-engine/">...</a>
     */
    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
