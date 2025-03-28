package view;

import static view.ViewConstants.BATTLE_BACKGROUND_COLOR;
import static view.ViewConstants.PLAYER_TILE_HIGHLIGHT_COLOR;
import static view.ViewConstants.TRANSPARENT_COLOR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;

import com.example.anomalousencounters.R;

import java.util.ArrayList;

import presenter.GamePresenter;

public class BattleView {
    private GamePresenter presenter;
    private GameView view;
    private Sprite grid;
    private Sprite playerIcon;
    private Sprite enemyIcon;
    private MenuNinePatch playerInfo;
    private MenuNinePatch enemyInfo;
    private BattleSideBar sideBar;
    private ArrayList<MenuEmpty> tileHighlights = new ArrayList<>();
    private final int MAX_CARD_WIDTH = 650;
    private int GRID_BORDER_WEIGHT = 5;
    boolean isFlashingTiles = false;
    private int timeInterval = 250; // animation speed in frames per milliseconds
    int current_color = PLAYER_TILE_HIGHLIGHT_COLOR;
    private long lastFrameTime = 0;
    private int ticks = 6;

    public BattleView(Context context, GamePresenter presenter, GameView view){
        this.presenter = presenter;
        this.view = view;

        Bitmap gridBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.battle_grid);
        int gridX = 720;
        int gridY = 170;
        grid = new Sprite(gridBitmap, gridX, gridY);

        Bitmap playerIconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_heart);
        playerIcon = new Sprite(playerIconBitmap, gridX + 100, gridY + 100);

        Bitmap enemyIconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.depressed_mustache);
        enemyIcon = new Sprite(enemyIconBitmap, gridX + GRID_BORDER_WEIGHT, gridY + GRID_BORDER_WEIGHT);


        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable playerInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        playerInfo = new MenuNinePatch(playerInfoNinePatchDrawable, 30, 75, presenter.getPlayerNameAndHealth(), MAX_CARD_WIDTH, false, context);

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable enemyInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        enemyInfo = new MenuNinePatch(enemyInfoNinePatchDrawable, 30, 75 + playerInfo.getHeight() + 30, "The Strange Triangle\nHP 9/10", MAX_CARD_WIDTH, false, context);

        sideBar = new BattleSideBar(1750, 75, context, presenter, this);
    }

    public void updateMenuTexts() {
        playerInfo.updateText(presenter.getPlayerNameAndHealth());
        enemyInfo.updateText(presenter.getEnemyNameAndHealth());
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
       sideBar.checkForUserTouch(eventX, eventY, presenter);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawColor(BATTLE_BACKGROUND_COLOR);
        grid.draw(canvas, paint);

        if (isFlashingTiles) {
            drawFlashingTiles(canvas, paint);
        } else {
            drawTiles(canvas, paint);
        }

        drawTiles(canvas, paint);
        playerIcon.draw(canvas, paint);
        enemyIcon.draw(canvas, paint);
        playerInfo.draw(canvas);
        enemyInfo.draw(canvas);
        sideBar.draw(canvas, paint);
    }

    private void drawTiles(Canvas canvas, Paint paint) {
        for (MenuEmpty tileHighlight: tileHighlights) {
            tileHighlight.draw(canvas, paint);
        }
    }

    public void updatePlayerPosition(int x, int y) {
        playerIcon.setX(grid.getX() + x + GRID_BORDER_WEIGHT);
        playerIcon.setY(grid.getY() + y + GRID_BORDER_WEIGHT);
    }

    public void updateEnemyPosition(int x, int y) {
        enemyIcon.setX(grid.getX() + x + GRID_BORDER_WEIGHT);
        enemyIcon.setY(grid.getY() + y + GRID_BORDER_WEIGHT);
    }

    public void setEnemyIcon(Bitmap enemyImage) {
        enemyIcon.updateBitmap(enemyImage);
    }

    public void highlightTiles(ArrayList<int[]> tileList) {
        clearGrid();
        for (int [] tile: tileList) {
            int x = grid.getX() + tile[0];
            int y = grid.getY() + tile[1];
            MenuEmpty tileHighlight = new MenuEmpty(x, y, enemyIcon.getHeight(), enemyIcon.getWidth(), PLAYER_TILE_HIGHLIGHT_COLOR);
            tileHighlights.add(tileHighlight);
        }
    }

    public void flashTiles() {
        isFlashingTiles = true;
        lastFrameTime = 0;
        ticks = 5;
    }

    public void stopFlashingTiles(){
        isFlashingTiles = false;
        current_color = PLAYER_TILE_HIGHLIGHT_COLOR;
        clearGrid();
        sideBar.endAnimation();
        useSkill();

        checkIfPlayerWinner();
    }

    private void checkIfPlayerWinner() {
        boolean isPlayerWinner = presenter.isPlayerWinner();
        if (isPlayerWinner) {
            view.endBattle(true);
        }
    }

    public void checkIfPlayerLoser() {
        boolean isPlayerLoser = presenter.isPlayerLoser();
        if (isPlayerLoser) {
            view.endBattle(false);
        }
    }


    public void drawFlashingTiles(Canvas canvas, Paint paint) {
        long currentTime = System.currentTimeMillis();

        // If this is the first frame of flashing
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
        }

        long deltaTime = currentTime - lastFrameTime;

        if (deltaTime >= timeInterval) {
            // Toggle color
            current_color = (current_color == PLAYER_TILE_HIGHLIGHT_COLOR) ? TRANSPARENT_COLOR : PLAYER_TILE_HIGHLIGHT_COLOR;

            // Update each tile's color
            for (MenuEmpty tileHighlight : tileHighlights) {
                tileHighlight.setColor(current_color);
                tileHighlight.draw(canvas, paint);
            }

            // Reset last frame time and decrement ticks
            lastFrameTime = currentTime;
            ticks--;
        }

        // Stop flashing when ticks reach 0
        if (ticks <= 0) {
            stopFlashingTiles();
        }
    }

    public void useSkill(){
        String skillName = sideBar.getSelectedSkill();
        presenter.usePlayerSkill(skillName);
    }

    public void resetActionFlags() {
        sideBar.resetActionFlags();
    }

    public void clearGrid() {
        tileHighlights = new ArrayList<>();
    }

    public int getBoardWidth() {
        return grid.getWidth();
    }

    public int getBoardHeight() {
        return grid.getHeight();
    }
}
