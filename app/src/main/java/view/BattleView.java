package view;

import static view.ViewConstants.BATTLE_BACKGROUND_COLOR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;

import com.example.anomalousencounters.R;

import presenter.GamePresenter;

public class BattleView {
    private Sprite grid;
    private Sprite playerIcon;
    private Sprite enemyIcon;
    private MenuNinePatch playerInfo;
    private MenuNinePatch enemyInfo;
    private BattleSideBar sideBar;
    private final int MAX_CARD_WIDTH = 650;
    private GamePresenter presenter;
    private int GRID_BORDER_WEIGHT = 5;

    public BattleView(Context context, GamePresenter presenter){
        this.presenter = presenter;

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

        sideBar = new BattleSideBar(1750, 75, context, presenter);
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
        playerIcon.draw(canvas, paint);
        enemyIcon.draw(canvas, paint);
        playerInfo.draw(canvas);
        enemyInfo.draw(canvas);
        sideBar.draw(canvas, paint);
    }

    public int getBoardWidth() {
        return grid.getWidth();
    }

    public int getBoardHeight() {
        return grid.getHeight();
    }

    public void updatePlayerPosition(int x, int y) {
        playerIcon.setX(grid.getX() + x + GRID_BORDER_WEIGHT);
        playerIcon.setY(grid.getY() + y + GRID_BORDER_WEIGHT);
    }

    public void updateEnemyPosition(int x, int y) {
        enemyIcon.setX(grid.getX() + x + GRID_BORDER_WEIGHT);
        enemyIcon.setY(grid.getY() + y + GRID_BORDER_WEIGHT);
    }

    public void setEnemyIcon(String imageName, Context context) {
        int resourceId = context.getResources().getIdentifier(imageName, "drawable",  context.getPackageName());
        Bitmap enemyIconBitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        enemyIcon.updateBitmap(enemyIconBitmap);
    }
}
