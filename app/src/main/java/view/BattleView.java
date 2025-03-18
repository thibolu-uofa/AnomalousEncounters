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
    private MenuNinePatch playerInfo;
    private MenuNinePatch enemyInfo;
    private SideBar sideBar;
    private final int MAX_CARD_WIDTH = 650;
    private GamePresenter presenter;

    public BattleView(Context context, GamePresenter presenter){
        this.presenter = presenter;

        Bitmap gridBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.battle_grid);
        grid = new Sprite(gridBitmap, 720, 170);

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable playerInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        playerInfo = new MenuNinePatch(playerInfoNinePatchDrawable, 30, 75, presenter.getPlayerNameAndHealth(), MAX_CARD_WIDTH, false, context);

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable enemyInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        enemyInfo = new MenuNinePatch(enemyInfoNinePatchDrawable, 30, 75 + playerInfo.getHeight() + 30, "The Strange Triangle\nHP 9/10", MAX_CARD_WIDTH, false, context);

        sideBar = new SideBar(1750, 75, context, presenter);
    }

    public void updateMenuTexts() {
        playerInfo.updateText(presenter.getPlayerNameAndHealth());
        enemyInfo.updateText("The Strange Triangle\nHP 9/10");
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
       sideBar.checkForUserTouch(eventX, eventY, presenter);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawColor(BATTLE_BACKGROUND_COLOR);
        grid.draw(canvas, paint);
        playerInfo.draw(canvas);
        enemyInfo.draw(canvas);
        sideBar.draw(canvas, paint);
    }
}
