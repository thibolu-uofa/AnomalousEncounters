package view;

import static view.ColorConstants.BATTLE_BACKGROUND_COLOR;

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
    private NinePatchImage playerInfo;
    private NinePatchImage enemyInfo;

    public BattleView(Context context){
        Bitmap gridBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.battle_grid);
        grid = new Sprite(gridBitmap, 700, 200);

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable playerInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        playerInfo = new NinePatchImage(playerInfoNinePatchDrawable, 50, 100, 400, 200);

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable enemyInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border1, null);
        enemyInfo = new NinePatchImage(enemyInfoNinePatchDrawable, 50, 100 + playerInfo.getHeight() + 50, 400, 200);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawColor(BATTLE_BACKGROUND_COLOR);
        grid.draw(canvas, paint);
        playerInfo.draw(canvas);
        enemyInfo.draw(canvas);
    }
}
