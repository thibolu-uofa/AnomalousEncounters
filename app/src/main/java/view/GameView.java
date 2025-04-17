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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.anomalousencounters.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import presenter.GamePresenter;
import view.battle.BattleView;
import view.battle.EndBattleScreen;
import view.menu.BaseMenu;

public class GameView  extends SurfaceView implements Runnable{
    private final GamePresenter presenter;
    private Thread gameThread;
    volatile boolean isPlaying; //check if the game is running
    private final SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private final Paint paint;
    private HealthBar healthBar;
    private Sprite inventory, homeIcon, saveIcon, shopIcon, indexIcon, alertIcon;
    private PlayerSprite playerSprite;
    private BackgroundImage backgroundImage;
    private PlayerMenu playerMenu;
    private ShopMenu shopMenu;
    private IndexMenu indexMenu;
    private SaveMenu saveMenu;
    private BattleView battleView;
    private EndBattleScreen endBattleScreen;
    int backgroundDirection;
    private boolean isOnOverworld = true;
    private boolean isInBattle = false;
    private boolean canPlayerMove = true;
    long fps; //keeps track of frame rate
    private long lastEnemyEncounterCheck = 0;
    private boolean isMenuOpen = false;
    private int fadeAlpha = -1;       // -1 means no fade
    private boolean isCanvasFadingIn = false;
    private boolean isCanvasFadingOut = false;
    private final int fadeSpeed = 10;
    private ConfirmPopUp confirmPopUp;
    private String saveMsg, homeMsg, phaseMsg;


    public GameView(Context context, GamePresenter presenter) {
        super(context);
        this.presenter = presenter;
        surfaceHolder = getHolder();
        paint = new Paint();

        // initialize screen dimensions
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = displayMetrics.widthPixels;
        SCREEN_HEIGHT = displayMetrics.heightPixels;

        initializeVisualComponents();
    }

    public void getVisibleScreenWidth() {
        WindowManager windowManager = (WindowManager) presenter.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return;
        }

        DisplayMetrics outMetrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //for android 11+
            Objects.requireNonNull(presenter.getDisplay()).getRealMetrics(outMetrics);
        } else {
            //for older versions
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        }

        SCREEN_WIDTH = outMetrics.widthPixels;
        SCREEN_HEIGHT = outMetrics.heightPixels;
    }

    public void initializeScreenDimensions() {
        DisplayMetrics displayMetrics = presenter.getResources().getDisplayMetrics();

        int rawWidth = displayMetrics.widthPixels;
        int rawHeight = displayMetrics.heightPixels;

        int statusBarHeight = 0;
        @SuppressLint("InternalInsetResource") int statusBarId = presenter.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarId > 0) {
            statusBarHeight = presenter.getResources().getDimensionPixelSize(statusBarId);
        }

        int navigationBarWidth = 0;
        boolean hasNavigationBar = false; // check if device has navigation bar
        int resourceId = presenter.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId > 0) {
            hasNavigationBar = presenter.getResources().getBoolean(resourceId);
        }

        if (hasNavigationBar) {
            @SuppressLint("InternalInsetResource") int navBarWidthId = presenter.getResources().getIdentifier("navigation_bar_width", "dimen", "android");
            if (navBarWidthId > 0) {
                navigationBarWidth = presenter.getResources().getDimensionPixelSize(navBarWidthId);
                Log.d("Width", "Width " + navigationBarWidth);
            }
        }

        SCREEN_WIDTH = rawWidth + navigationBarWidth;
        SCREEN_HEIGHT = rawHeight + statusBarHeight;
    }

    private void initializeVisualComponents() {
        initializeOverworldMenuIcons();
        initializeInventory();
        initializeHealthBar();
        initializeBackground();
        initializePlayerSprite();
        initializeMenus();
    }

    private void initializeOverworldMenuIcons() {
        Bitmap homeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.house_icon);
        int iconsX = SCREEN_WIDTH - homeIconBitmap.getWidth() - 25;
        homeIcon = new Sprite(homeIconBitmap, iconsX, 70);

        Bitmap saveIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.save_icon);
        saveIcon = new Sprite(saveIconBitmap, iconsX, homeIcon.getY() + homeIconBitmap.getHeight() + 20);

        Bitmap indexIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.book_icon);
        indexIcon = new Sprite(indexIconBitmap, iconsX, saveIcon.getY() + saveIconBitmap.getHeight() + 20);

        Bitmap shopIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shop_icon);
        shopIcon = new Sprite(shopIconBitmap, iconsX, indexIcon.getY() + indexIconBitmap.getHeight() + 20);
    }

    private void updateIconPositioningToCanvas() {
        int iconsX = CANVAS_WIDTH - homeIcon.getWidth() - 25;
        homeIcon.setX(iconsX);
        saveIcon.setX(iconsX);
        indexIcon.setX(iconsX);
        shopIcon.setX(iconsX);
    }

    private void initializeInventory() {
        Bitmap inventoryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.quick_inventory);
        int inventoryX = SCREEN_WIDTH/2 - inventoryBitmap.getWidth()/2;
        int inventoryY = (int) (SCREEN_HEIGHT*0.75);
        inventory = new Sprite(inventoryBitmap, inventoryX, inventoryY);
    }

    private void initializeHealthBar() {
        Bitmap healthBarBaseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.healthbar_base);
        Bitmap healthBarHealthBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.healthbar_health);
        int healthBarX = SCREEN_WIDTH/2 - healthBarHealthBitmap.getWidth()/2;
        healthBar = new HealthBar(healthBarBaseBitmap, healthBarHealthBitmap, healthBarX, 50);
    }

    private void initializeBackground() {
        Bitmap skyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_sky);
        Bitmap groundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_map);
        int backgroundY = -(SCREEN_HEIGHT/9);

        int singlePlayerSpriteWidth = BitmapFactory.decodeResource(getResources(), R.drawable.player_sprite_sheet_v2).getWidth()/12;
        int playerX = SCREEN_WIDTH/2 - (singlePlayerSpriteWidth/2);

        backgroundImage = new BackgroundImage(skyBitmap, groundBitmap, backgroundY, playerX);
        backgroundImage.setDirection(0);
    }

    private void initializePlayerSprite() {
        Bitmap playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_sprite_sheet_v2);
        int playerX = SCREEN_WIDTH/2 - playerBitmap.getWidth()/12;

        Bitmap groundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_map);
        int backgroundY = -(SCREEN_HEIGHT/9);
        int playerY = (int) (backgroundY + (groundBitmap.getHeight() * 0.6) - ((double) playerBitmap.getHeight()/4)) + 5;

        playerSprite = new PlayerSprite(playerBitmap, playerX, playerY);
        playerSprite.setAnimation("idle");
    }

    private void initializeMenus() {
        playerMenu = new PlayerMenu(presenter, getContext());
        shopMenu = new ShopMenu(presenter, getContext());
        indexMenu = new IndexMenu(presenter, getContext());
        saveMenu = new SaveMenu(presenter, getContext(), this);
    }

    /**
     * This function will run the game loop that draws frames and calculates the fps.
     * The implementation of this function has code adapted from:
     * Source: <a href="https://gamecodeschool.com/android/building-a-simple-game-engine/">...</a>
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

    public void drawOnCanvas() {
        // make sure our drawing surface is valid or the app will crash
        if (!surfaceHolder.getSurface().isValid()) {
            return;
        }

        setUpCanvas();
        updateIconPositioningToCanvas();
        drawBackground();

        if (isOnOverworld) {
            drawOverworldElements();
        }

        drawBattleView();
        drawEndBattleScreen();
        drawConfirmPopUp();
        handleCanvasTransitions();

        // finish drawing
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void setUpCanvas() {
        // lock the canvas ready to draw and make the drawing surface our canvas object
        canvas = surfaceHolder.lockCanvas();
        CANVAS_WIDTH = canvas.getWidth();
        CANVAS_HEIGHT = canvas.getHeight();
    }

    private void drawBackground() {
        int backgroundColor = Color.argb(255, 255, 255, 255);
        canvas.drawColor(backgroundColor); // draw the background color
        paint.setColor(Color.argb(255,  255, 255, 255)); // choose the brush color for drawing
    }

    private void drawOverworldElements() {
        long currentTime = System.currentTimeMillis();

        updateAndDrawBackground();
        drawAlertIcon();
        checkForEnemyEncounter(currentTime);
        drawOverworldSprites();
        drawHealthBar();
        updateAndDrawPlayer(currentTime);
        drawOpenMenus();
    }

    private void updateAndDrawBackground() {
        backgroundImage.update(fps, canPlayerMove);
        backgroundImage.draw(canvas, paint);
    }

    private void checkForEnemyEncounter(long currentTime) {
        int TIME_GAP = 3000;  // in milliseconds
        boolean hasEnoughTimePassedSinceLastEncounter = (currentTime - lastEnemyEncounterCheck) >= TIME_GAP;
        if (hasEnoughTimePassedSinceLastEncounter && !isMenuOpen) {
            presenter.hasPlayerEncounteredEnemy(backgroundImage.getX());
            lastEnemyEncounterCheck = System.currentTimeMillis();
        }
    }

    private void drawOverworldSprites() {
        Sprite[] sprites = {homeIcon, saveIcon, indexIcon, shopIcon, inventory};
        for (Sprite sprite: sprites) {
            sprite.draw(canvas, paint);
        }
    }

    private void drawHealthBar() {
        healthBar.draw(canvas, paint, presenter.getPlayerHealthPercentage());
    }

    private void updateAndDrawPlayer(long currentTime) {
        playerSprite.update(currentTime, canPlayerMove);
        playerSprite.draw(canvas);
    }

    private void drawOpenMenus() {
        BaseMenu[] menus = {playerMenu, shopMenu, indexMenu, saveMenu};
        List<BaseMenu> menusToUpdate = Arrays.asList(playerMenu, shopMenu);

        for (BaseMenu menu : menus) {
            if (menu != null && !menu.isMenuClosed()) {
                if (menusToUpdate.contains(menu)) {
                    menu.updateMenuTexts(presenter);
                }
                menu.draw(canvas, paint);
            }
        }
    }

    private void drawBattleView() {
        if (battleView != null) {
            try {
                battleView.updateMenuTexts();
                battleView.draw(canvas, paint);
            } catch (NullPointerException e) {
                Log.e("Null Pointer Exception for BattleView", "BattleView is null", e);
            }
        }
    }

    private void drawEndBattleScreen() {
        if (endBattleScreen != null) {
            endBattleScreen.draw(canvas, paint);
        }
    }

    private void drawConfirmPopUp() {
        if (confirmPopUp != null) {
            confirmPopUp.draw(canvas, paint);
        }
    }

    private void drawAlertIcon() {
        if (alertIcon != null) {
            alertIcon.draw(canvas, paint);
        }
    }

    private void handleCanvasTransitions() {
        if (isCanvasFadingOut || isCanvasFadingIn) {
            Paint fadePaint = new Paint();
            fadePaint.setColor(Color.BLACK);
            fadePaint.setAlpha(fadeAlpha);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), fadePaint);

            if (isCanvasFadingIn) {
                fadeAlpha -= fadeSpeed;
                if (fadeAlpha <= 0) {
                    fadeAlpha = -1; // done fading in
                    isCanvasFadingIn = false;
                }
            } else if (isCanvasFadingOut) {
                fadeAlpha += fadeSpeed;
                if (fadeAlpha >= 255) {
                    fadeAlpha = 255; // fully faded out
                    isCanvasFadingOut = false;
                    if (isInBattle) {
                        presenter.finishedBattleFadeOutTransition();
                    }
                }
            }
        }
    }

    public void startFadeIn() {
        fadeAlpha = 255;
        isCanvasFadingIn = true;
        isCanvasFadingOut = false;
        checkIfPlayerProgressedPhase();
    }

    public void startFadeOut() {
        fadeAlpha = 0;
        isCanvasFadingIn = false;
        isCanvasFadingOut = true;
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
            // user has touched the screen
            case MotionEvent.ACTION_DOWN:
                Log.d("X Position", "Current X Position is " + backgroundImage.getX());
                processActionDownEvent(motionEvent);
                break;

            // user has removed finger from screen, so character should stop moving
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: // handle interrupted touches
            case MotionEvent.ACTION_OUTSIDE: // handle touches that move outside the view
                if (isOnOverworld) {
                    playerSprite.setAnimation("idle");
                    backgroundImage.setDirection(0);
                }
                break;
        }
        return true;
    }

    private void processActionDownEvent(MotionEvent motionEvent) {
        float eventX = motionEvent.getX();
        float eventY = motionEvent.getY();

        if (isOnOverworld) {
            handleOverworldOnTouchEvents(eventX, eventY);
        }

        if (battleView != null) {
            battleView.checkForUserTouch(eventX, eventY, presenter);
        }

        if (endBattleScreen != null && hasEndBattleScreenBeenClosed(eventX, eventY)) {
            closeEndBattleInfo();
        }
    }

    private void handleOverworldOnTouchEvents(float eventX, float eventY) {
        if (confirmPopUp != null) {
            handleConfirmPopUp(eventX, eventY, presenter);
            return;
        }

        if (alertIcon != null) {
            handlePhaseAlertIcon(eventX, eventY, presenter);
        }

        if (!isMenuOpen) {
            checkMenuOpening(eventX, eventY);
        }

        checkOpenMenuTouches(eventX, eventY);

        if (checkForMenuClosing(eventX, eventY)) {
            handleMenuClosed();
            //return to prevent player from moving on player when a menu has just been closed
            return;
        }

        updatePlayerAnimation((int) eventX);
    }

    private void checkMenuOpening(float eventX, float eventY) {
        if (inventory.hasBeenTouched(eventX, eventY, presenter, 3)) {
            openMenu("player");
        }
        else if (shopIcon.hasBeenTouched(eventX, eventY, presenter, 1)) {
            openMenu("shop");
        }
        else if (indexIcon.hasBeenTouched(eventX, eventY, presenter, 1)) {
            openMenu("index");
        }
        else if (homeIcon.hasBeenTouched(eventX, eventY, presenter, 1)) {
            showReturnToMainMenuConfirmation();
        }
        else if (saveIcon.hasBeenTouched(eventX, eventY, presenter, 1)) {
            handleSaveIconTouched();
        }
    }

    private void openMenu(String menuType) {
        isMenuOpen = true;
        canPlayerMove = false;

        switch (menuType) {
            case "player":
                playerMenu = new PlayerMenu(presenter, getContext());
                playerMenu.openMenu();
                break;
            case "shop":
                shopMenu = new ShopMenu(presenter, getContext());
                shopMenu.openMenu();
                break;
            case "index":
                indexMenu = new IndexMenu(presenter, getContext());
                indexMenu.openMenu();
                break;
            case "save":
                saveMenu = new SaveMenu(presenter, getContext(), this);
                saveMenu.openMenu();
                break;
        }
    }

    private void showReturnToMainMenuConfirmation() {
        homeMsg = presenter.getString(R.string.confirmHomeBtn);
        confirmPopUp = new ConfirmPopUp(homeMsg, presenter, true);
        isMenuOpen = true;
        canPlayerMove = false;
    }

    private void handleSaveIconTouched() {
        if (!presenter.hasSaveFile()) {
            saveMsg = presenter.getString(R.string.saveConfirmation);
            confirmPopUp = new ConfirmPopUp(saveMsg, presenter, true);
        } else {
            openMenu("save");
        }
        isMenuOpen = true;
        canPlayerMove = false;
    }

    private void checkOpenMenuTouches(float eventX, float eventY) {
        BaseMenu[] menus = {playerMenu, shopMenu, indexMenu, saveMenu};
        for (BaseMenu menu: menus) {
            // if the menu is not closed (so open), check for user touch
            if (!menu.isMenuClosed()) {
                menu.checkForUserTouch(eventX, eventY, presenter);
            }
        }
    }

    private boolean checkForMenuClosing(float eventX, float eventY) {
        boolean closedAnyMenu = false;

        BaseMenu[] menus = {playerMenu, shopMenu, indexMenu, saveMenu};
        for (BaseMenu menu: menus) {
            // if the menu is open, and has been closed, then close menu
            if (!menu.isMenuClosed() && menu.hasClosedMenu(eventX, eventY, presenter)) {
                menu.closeMenu();
                closedAnyMenu = true;
                checkIfPlayerProgressedPhase();
            }
        }

        if (closedAnyMenu) {
            canPlayerMove = true;
        }

        return closedAnyMenu;
    }

    private void handleMenuClosed() {
        int STOP_GAP = 5000;
        lastEnemyEncounterCheck = System.currentTimeMillis() + STOP_GAP;
        isMenuOpen = false;
    }

    private void handleConfirmPopUp(float eventX, float eventY, GamePresenter presenter) {
        boolean userTouchedPopUp = confirmPopUp.didUserTouchButton(eventX, eventY, presenter);
        if (userTouchedPopUp) {
            boolean didUserConfirm = confirmPopUp.didUserConfirm();

            String confirmText = confirmPopUp.getMessage();
            boolean isHomePopUp = Objects.equals(confirmText, homeMsg);
            boolean isSavePopUp = Objects.equals(confirmText, saveMsg);
            boolean isPhasePopUp = Objects.equals(confirmText, phaseMsg);

            if (isHomePopUp && didUserConfirm) {
                presenter.changeViewBackToMainActivity();
            }

            if (isSavePopUp && didUserConfirm) {
                presenter.makeFirstSaveSlot();
            }

            if (isPhasePopUp && didUserConfirm) {
                presenter.increasePlayerPhase();
                alertIcon = null;
            }

            confirmPopUp = null;
            isMenuOpen = false;
            canPlayerMove = true;
        }
    }

    private void handlePhaseAlertIcon(float eventX, float eventY, GamePresenter presenter) {
        boolean userTouchedIcon = alertIcon.hasBeenTouched(eventX, eventY, presenter, 1);
        if (userTouchedIcon && !isMenuOpen) {
            phaseMsg = presenter.getString(R.string.phaseProgressConfirmation);
            confirmPopUp = new ConfirmPopUp(phaseMsg, presenter, true);
            isMenuOpen = true;
            canPlayerMove = false;
        }
    }

    public void closeSaveMenu() {
        saveMenu.closeMenu();
        canPlayerMove = true;
        handleMenuClosed();
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
        isInBattle = false;
        isOnOverworld = true;
        if (isMenuOpen) {
            canPlayerMove = false;
        } else {
            canPlayerMove = true;
        }
    }

    public String getSelectedSkillForPlayerMenu() {
        return playerMenu.getSelectedSkill();
    }

    public void startBattleTransition() {
        startFadeOut();
        isOnOverworld = false;
        canPlayerMove = false;
        isInBattle = true;
    }

    public void displayBattle() {
        battleView = new BattleView(this.getContext(), presenter, this);
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
        startFadeIn();

        //prevents player from immediately encountering another enemy
        int STOP_GAP = 5000;
        lastEnemyEncounterCheck = System.currentTimeMillis() + STOP_GAP;
        displayOverworld();
        battleView = null;

        //start back up overworld music
        presenter.playOverworldMusic();

        //check if player has progressed phase
        checkIfPlayerProgressedPhase();
    }

    private void checkIfPlayerProgressedPhase() {
        boolean progressedPhase = presenter.hasPlayerProgressedPhase();
        if (progressedPhase) {
            Bitmap alertIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alert_icon);
            int iconX = 50;
            int iconY = 70;
            alertIcon = new Sprite(alertIconBitmap, iconX, iconY);
        }
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

    public void startNewGame() {
        displayOverworld();
        backgroundImage.resetPositions();
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
