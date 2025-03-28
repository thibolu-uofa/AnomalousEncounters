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
    private boolean hasAttacked = false;
    private boolean hasMoved = false;
    private boolean hasUsedItem = false;
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
        String skillCooldowns = presenter.getSkillCooldownsString();
        skillBar = new SkillBar(x, y, WIDTH, HEIGHT, skillArrayList, skillCooldowns, context);
        moveBar = new MoveBar(x, y, WIDTH, context);

        currentDisplay = DisplayOptions.ACTION_BAR;
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        if (isAnimationPlaying) {
            return;
        }

        boolean hasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, x, x + WIDTH, y + HEIGHT, y);
        if (!hasBeenPressed) {
            return;
        }

        String selectedText;
        switch (currentDisplay) {
            case ACTION_BAR:
                processActionBarTouch(actionBar.checkForUserTouch(eventX, eventY, presenter));
                break;
            case SKILL_BAR:
                selectedText = skillBar.checkForUserTouch(eventX, eventY, presenter);
                processSkillSelected(selectedText);
                processConfirm(selectedText, currentDisplay);
                processGoBack(selectedText, currentDisplay);
                break;
            case MOVE_BAR:
                selectedText = moveBar.checkForUserTouch(eventX, eventY, presenter);
                processMoveSelected(selectedText);
                processConfirm(selectedText, currentDisplay);
                processGoBack(selectedText, currentDisplay);
                break;
        }
    }

    private void processActionBarTouch(String selectedText) {
        if (Objects.equals(selectedText, "")) {
            return;
        }

        switch(selectedText){
            case "[ATK]":
                if (!hasAttacked) {
                    skillBar.resetCheckedBtn();
                    changeDisplay(DisplayOptions.SKILL_BAR);
                }
                break;
            case "[MOVE]":
                if (!hasMoved) {
                    moveBar.resetSelectedArrow();
                    changeDisplay(DisplayOptions.MOVE_BAR);
                }
                break;
            case "[USE]":
                if (!hasUsedItem) {
                    Log.d("Button Processing", "USE BTN");
                }
                break;
            case "End Turn":
                presenter.startEnemyTurn();
                Log.d("Button Processing", "END MY TURN");
                break;
        }
    }

    public void resetActionFlags() {
        battleView.checkIfPlayerLoser();
        hasAttacked = false;
        hasMoved = false;
        hasUsedItem = false;
        actionBar.resetButtons();
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

    public void processConfirm(String selectedText, DisplayOptions currentDisplay){
        if (!Objects.equals(selectedText, "[Confirm]")) {
            return;
        }
        switch(currentDisplay){
            case SKILL_BAR:
                Log.d("User wants to use a skill", "Confirm");
                battleView.flashTiles();
                isAnimationPlaying = true;
                hasAttacked = true;
                actionBar.disableButton("[ATK]");
                break;
            case MOVE_BAR:
                Log.d("User wants to move", "Move");
                String movementDirection = moveBar.getSelectedArrowDirection();
                presenter.movePlayer(movementDirection);
                hasMoved = true;
                actionBar.disableButton("[MOVE]");
                break;
        }
        changeDisplay(DisplayOptions.ACTION_BAR);
    }

    private void processGoBack(String selectedText, DisplayOptions currentDisplay) {
        if (!Objects.equals(selectedText, "[Go Back]")) {
            return;
        }
        switch(currentDisplay){
            case SKILL_BAR:
                battleView.clearGrid();
                break;
            case MOVE_BAR:
                int[] playerPos = presenter.getPlayerPosition();
                battleView.updatePlayerPosition(playerPos[0], playerPos[1]);
                break;
        }
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

    public String getSelectedSkill(){
        return skillBar.getSelectedSkill();
    }
}
