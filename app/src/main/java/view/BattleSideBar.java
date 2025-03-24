package view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;

import com.example.anomalousencounters.R;

import java.util.ArrayList;
import android.util.Log;
import java.util.Objects;

import presenter.GamePresenter;

public class BattleSideBar {
    GamePresenter presenter;
    BattleView battleView;
    private final MenuNinePatch menuNinePatch;
    private final SkillBar skillBar;
    private final ActionBar actionBar;
    private final MoveBar moveBar;

    private final int x;
    private final int y;
    private final int WIDTH = 600;
    private final int HEIGHT = 950;
    private final ArrayList<String> skillArrayList;
    public enum DisplayOptions{
        ACTION_BAR,
        SKILL_BAR,
        MOVE_BAR,
        ITEM_BAR
    }
    private DisplayOptions currentDisplay;

    public BattleSideBar(int x, int y, Context context, GamePresenter presenter, BattleView battleView) {
        this.x = x;
        this.y = y;
        this.presenter = presenter;
        this.battleView = battleView;

        @SuppressLint("UseCompatLoadingForDrawables") NinePatchDrawable playerInfoNinePatchDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.border2, null);
        menuNinePatch = new MenuNinePatch(playerInfoNinePatchDrawable, x, y, WIDTH, HEIGHT);

        actionBar = new ActionBar(x, y, WIDTH, context);
        skillArrayList = presenter.getSkillNamesArray();
        skillBar = new SkillBar(x, y, WIDTH, HEIGHT, skillArrayList, context);
        moveBar = new MoveBar(x, y, WIDTH, context);

        currentDisplay = DisplayOptions.ACTION_BAR;
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        boolean hasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, x, x + WIDTH, y + HEIGHT, y);
        if (hasBeenPressed) {
            switch(currentDisplay){
                case ACTION_BAR:
                    processActionBarTouch(actionBar.checkForUserTouch(eventX, eventY, presenter));
                    break;
                case SKILL_BAR:
                    String selectedText = skillBar.checkForUserTouch(eventX, eventY, presenter);
                    processGoBack(selectedText);
                    processSkillSelected(selectedText);
                    break;
                case MOVE_BAR:
                    processGoBack(moveBar.checkForUserTouch(eventX, eventY, presenter));
                    break;
            }
        }
    }

    private void processSkillSelected(String selectedText) {
        if (Objects.equals(selectedText, "")) {
            return;
        }

        if (skillArrayList.contains(selectedText)) {
            Log.d("Skill Selected", selectedText);

            // get the tiles affected by the skill from the presenter
            ArrayList<int[]> affectedTiles = presenter.getAffectedTilesForPlayer(selectedText);
            battleView.highlightTiles(affectedTiles);
        }

    }

    private void processActionBarTouch(String selectedText) {
        if (Objects.equals(selectedText, "")) {
            return;
        }

        switch(selectedText){
            case "[ATK]":
                changeDisplay(DisplayOptions.SKILL_BAR);
                break;
            case "[MOVE]":
                changeDisplay(DisplayOptions.MOVE_BAR);
                break;
            case "[USE]":
                Log.d("Button Processing", "USE BTN");
                break;
            case "End Turn":
                Log.d("Button Processing", "END MY TURN");
                break;
        }
    }

    private void processGoBack(String selectedText) {
        if (!Objects.equals(selectedText, "[Go Back]")) {
            return;
        }
        battleView.clearGrid();
        Log.d("User wants to go back", "GO BACK");
        changeDisplay(DisplayOptions.ACTION_BAR);
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
