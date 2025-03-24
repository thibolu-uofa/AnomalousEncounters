package view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;

import com.example.anomalousencounters.R;

import java.util.ArrayList;
import android.util.Log;

import java.util.Arrays;
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
    private boolean isAnimationPlaying = false;

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
        if (isAnimationPlaying) {
            return;
        }

        boolean hasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, x, x + WIDTH, y + HEIGHT, y);
        String selectedText;
        if (hasBeenPressed) {
            switch(currentDisplay){
                case ACTION_BAR:
                    processActionBarTouch(actionBar.checkForUserTouch(eventX, eventY, presenter));
                    break;
                case SKILL_BAR:
                    selectedText = skillBar.checkForUserTouch(eventX, eventY, presenter);
                    processSkillSelected(selectedText);
                    processConfirmSkill(selectedText);
                    processGoBack(selectedText);
                    break;
                case MOVE_BAR:
                    selectedText = moveBar.checkForUserTouch(eventX, eventY, presenter);
                    processMoveSelected(selectedText);
                    processConfirmMove(selectedText);
                    processGoBack(selectedText);
                    break;
            }
        }
    }

    private void processSkillSelected(String selectedText) {
        if (Objects.equals(selectedText, "")) {
            return;
        }

        if (skillArrayList.contains(selectedText)) {
            ArrayList<int[]> affectedTiles = presenter.getAffectedTilesForPlayer(selectedText);
            battleView.highlightTiles(affectedTiles);
        }
    }

    private void processMoveSelected(String selectedText) {
        if (Objects.equals(selectedText, "")) {
            return;
        }

        String[] moves = {"up", "down", "left", "right"};
        if (Arrays.asList(moves).contains(selectedText)) {
            int[] newPlayerPosition = presenter.getTemporaryPlayerPosition(selectedText);
            battleView.updatePlayerPosition(newPlayerPosition[0], newPlayerPosition[1]);
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

    public void processConfirmSkill(String selectedText){
        if (!Objects.equals(selectedText, "[Confirm]")) {
            return;
        }
        Log.d("User wants to use a skill", "Confirm");
        battleView.flashTiles();
        isAnimationPlaying = true;
    }

    public void processConfirmMove(String selectedText){
        if (!Objects.equals(selectedText, "[Confirm]")) {
            return;
        }
        Log.d("User wants to move", "Move");

        String movementDirection = moveBar.getSelectedArrowDirection();
        presenter.movePlayer(movementDirection);
        changeDisplay(DisplayOptions.ACTION_BAR);
    }

    private void processGoBack(String selectedText) {
        if (!Objects.equals(selectedText, "[Go Back]")) {
            return;
        }
        battleView.clearGrid();
        Log.d("User wants to go back", "GO BACK");

        int[] playerPos = presenter.getPlayerPosition();
        battleView.updatePlayerPosition(playerPos[0], playerPos[1]);
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

    public void endAnimation() {
        isAnimationPlaying = false;
    }
}
