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
    private final SkillBar skillBar;
    private final ActionBar actionBar;
    private final MoveBar moveBar;

    private final int x;
    private final int y;
    private final int WIDTH = 600;
    private final int HEIGHT = 950;
    public enum DisplayOptions{
        ACTION_BAR,
        SKILL_BAR,
        MOVE_BAR,
        ITEM_BAR
    }
    private DisplayOptions currentDisplay;

    public BattleSideBar(int x, int y, Context context, GamePresenter presenter) {
        this.x = x;
        this.y = y;

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable playerInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border2, null);
        menuNinePatch = new MenuNinePatch(playerInfoNinePatchDrawable, x, y, WIDTH, HEIGHT);

        actionBar = new ActionBar(x, y, WIDTH, context);
        skillBar = new SkillBar(x, y, WIDTH, HEIGHT, presenter, context);
        moveBar = new MoveBar(x, y, WIDTH, context);

        currentDisplay = DisplayOptions.ACTION_BAR;
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        boolean hasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, x, x + WIDTH, y + HEIGHT, y);
        if (hasBeenPressed) {
            switch(currentDisplay){
                case ACTION_BAR:
                    actionBar.checkForUserTouch(eventX, eventY, presenter);
                    break;
                case SKILL_BAR:
                    skillBar.checkForUserTouch(eventX, eventY, presenter);
                    break;
            }
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        menuNinePatch.draw(canvas);
        switch(currentDisplay){
            case ACTION_BAR:
                actionBar.draw(canvas);
                break;
            case SKILL_BAR:
                skillBar.draw(canvas, paint);
                break;
            case MOVE_BAR:
                moveBar.draw(canvas, paint);
        }
    }

    public void changeDisplay(DisplayOptions displayOption){
        currentDisplay = displayOption;
    }
}
