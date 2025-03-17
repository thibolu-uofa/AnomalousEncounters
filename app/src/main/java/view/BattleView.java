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

public class BattleView {
    private Sprite grid;
    private MenuText playerInfoText;
    private MenuText enemyInfoText;
    private MenuNinePatch playerInfo;
    private MenuNinePatch enemyInfo;
    private final int MAX_CARD_WIDTH = 600;

    public BattleView(Context context){
        Bitmap gridBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.battle_grid);
        grid = new Sprite(gridBitmap, 800, 150);

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable playerInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        playerInfo = new MenuNinePatch(playerInfoNinePatchDrawable, 30, 100, "Bobette", MAX_CARD_WIDTH, false, context);

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable enemyInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        enemyInfo = new MenuNinePatch(enemyInfoNinePatchDrawable, 30, 100 + playerInfo.getHeight() + 50, "The Cursed Banana", MAX_CARD_WIDTH, false, context);
    }

    public void draw(Canvas canvas, Paint paint, Context context){
        canvas.drawColor(BATTLE_BACKGROUND_COLOR);
        grid.draw(canvas, paint);
        playerInfo.draw(canvas, context);
        enemyInfo.draw(canvas, context);
    }
}
