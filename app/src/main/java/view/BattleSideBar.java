package view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;

import com.example.anomalousencounters.R;

import java.util.ArrayList;

import presenter.GamePresenter;

public class BattleSideBar {
    private final MenuNinePatch menuNinePatch;
    private final RadioBtnList radioBtnList;
    private final int x;
    private final int y;
    private final int WIDTH = 600;
    private final int HEIGHT = 950;

    public BattleSideBar(int x, int y, Context context, GamePresenter presenter) {
        this.x = x;
        this.y = y;

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable playerInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border2, null);
        menuNinePatch = new MenuNinePatch(playerInfoNinePatchDrawable, x, y, WIDTH, HEIGHT);

        ArrayList<String> skillArrayList = presenter.getSkillNamesArray();
        radioBtnList = new RadioBtnList(x + 40, y + 120, context, skillArrayList, WIDTH, HEIGHT);
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        boolean hasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, x, x + WIDTH, y + HEIGHT, y);
        if (hasBeenPressed) {
            radioBtnList.checkForBtnPress(eventX, eventY, presenter);
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        menuNinePatch.draw(canvas);

        //TESTING
        radioBtnList.draw(canvas, paint);
    }
}
