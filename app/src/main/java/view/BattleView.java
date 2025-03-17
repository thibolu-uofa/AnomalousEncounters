package view;

import static view.ViewConstants.BATTLE_BACKGROUND_COLOR;
import static view.ViewConstants.DEFAULT_FONT_SIZE;
import static view.ViewConstants.DEFAULT_TEXT_COLOR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;

import com.example.anomalousencounters.R;

import java.util.Objects;

import presenter.GamePresenter;

public class BattleView {
    private Sprite grid;
    private MenuText playerInfoText;
    private MenuText enemyInfoText;
    private MenuNinePatch playerInfo;
    private MenuNinePatch enemyInfo;
    private final int MAX_CARD_WIDTH = 650;
    private GamePresenter presenter;

    public BattleView(Context context, GamePresenter presenter){
        this.presenter = presenter;

        Bitmap gridBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.battle_grid);
        grid = new Sprite(gridBitmap, 800, 150);

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable playerInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        playerInfo = new MenuNinePatch(playerInfoNinePatchDrawable, 30, 100, presenter.getPlayerNameAndHealth(), MAX_CARD_WIDTH, false, context);

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable enemyInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        enemyInfo = new MenuNinePatch(enemyInfoNinePatchDrawable, 30, 100 + playerInfo.getHeight() + 150, "The Strange Triangle\nHP 9/10", MAX_CARD_WIDTH, false, context);
    }

    public void updateMenuTexts() {
        playerInfo.updateText(presenter.getPlayerNameAndHealth());
        enemyInfo.updateText("The Strange Triangle\nHP 9/10");
    }

    public void draw(Canvas canvas, Paint paint, Context context){
        canvas.drawColor(BATTLE_BACKGROUND_COLOR);
        grid.draw(canvas, paint);
        playerInfo.draw(canvas, context);
        enemyInfo.draw(canvas, context);
    }
}
