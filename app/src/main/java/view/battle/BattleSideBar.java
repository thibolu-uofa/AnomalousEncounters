package view.battle;

import static view.ViewConstants.PLAYER_TILE_HIGHLIGHT_COLOR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;

import com.example.anomalousencounters.R;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Objects;

import presenter.GamePresenter;
import view.AlertPopUp;
import view.ConfirmPopUp;
import view.menu.MenuNinePatch;

public class BattleSideBar {
    GamePresenter presenter;
    BattleView battleView;
    private ConfirmPopUp confirmPopUp;
    private AlertPopUp alertPopUp;
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
    }
    private DisplayOptions currentDisplay;
    private boolean hasAttacked = false;
    private boolean hasMoved = false;
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

        if (alertPopUp != null) {
            boolean userClosedPopUp = alertPopUp.didUserClosePopUp(eventX, eventY, presenter);
            if (userClosedPopUp) {
                alertPopUp = null;
            }
            return;
        }

        if (confirmPopUp != null) {
            handleConfirmPopUp(eventX, eventY, presenter);
            return;
        }

        boolean hasNotBeenPressed = !presenter.isInHitbox((int) eventX, (int) eventY, x, x + WIDTH, y + HEIGHT, y);
        if (hasNotBeenPressed) {
            return;
        }

        switch (currentDisplay) {
            case ACTION_BAR:
                handleActionBarTouch(eventX, eventY, presenter);
                break;
            case SKILL_BAR:
                handleSkillBarTouch(eventX, eventY, presenter);
                break;
            case MOVE_BAR:
                handleMoveBarTouch(eventX, eventY, presenter);
                break;
        }
    }

    private void handleConfirmPopUp(float eventX, float eventY, GamePresenter presenter) {
        boolean userTouchedPopUp = confirmPopUp.didUserTouchButton(eventX, eventY, presenter);
        if (userTouchedPopUp) {
            boolean didUserConfirm = confirmPopUp.didUserConfirm();
            if (didUserConfirm) {
                battleView.fleeBattle();
            }
            confirmPopUp = null;
        }
    }

    private void handleActionBarTouch(float eventX, float eventY, GamePresenter presenter) {
        String selectedText = actionBar.checkForUserTouch(eventX, eventY, presenter);
        processActionBarTouch(selectedText);
    }

    private void handleSkillBarTouch(float eventX, float eventY, GamePresenter presenter) {
        String selectedText = skillBar.checkForUserTouch(eventX, eventY, presenter);
        processSkillSelected(selectedText);
        processSkillInfoSelected(selectedText);
        processConfirm(selectedText, DisplayOptions.SKILL_BAR);
        processGoBack(selectedText, DisplayOptions.SKILL_BAR);
    }


    private void handleMoveBarTouch(float eventX, float eventY, GamePresenter presenter) {
        String selectedText = moveBar.checkForUserTouch(eventX, eventY, presenter);
        processMoveSelected(selectedText);
        processConfirm(selectedText, DisplayOptions.MOVE_BAR);
        processGoBack(selectedText, DisplayOptions.MOVE_BAR);
    }

    private void processActionBarTouch(String selectedText) {
        if (Objects.equals(selectedText, "")) {
            return;
        }

        switch (selectedText) {
            case "[ATK]":
                handleAttackAction();
                break;
            case "[MOVE]":
                handleMoveAction();
                break;
            case "End Turn":
                handleEndTurnAction();
                break;
            case "Withdraw":
                handleWithdrawAction();
                break;
        }
    }


    private void handleAttackAction() {
        if (!hasAttacked) {
            skillBar.resetCheckedBtn();
            String skillCooldowns = presenter.getSkillCooldownsString();
            skillBar.updateSkillCooldowns(skillCooldowns);
            changeDisplay(DisplayOptions.SKILL_BAR);
        }
    }

    private void handleMoveAction() {
        if (!hasMoved) {
            moveBar.resetSelectedArrow();
            changeDisplay(DisplayOptions.MOVE_BAR);
        }
    }

    private void handleEndTurnAction() {
        presenter.endPlayerTurn();
        presenter.startEnemyTurn();
    }

    private void handleWithdrawAction() {
        String confirmMsg = presenter.getString(R.string.withdrawMessage);
        confirmPopUp = new ConfirmPopUp(confirmMsg, presenter, true);
    }

    public void resetActionFlags() {
        hasAttacked = false;
        hasMoved = false;
        actionBar.resetButtons();
    }

    private void processSkillSelected(String selectedText) {
        if (Objects.equals(selectedText, "")) {
            return;
        }

        if (Objects.equals(selectedText, "invalid_skill")) {
            battleView.clearGrid();
            return;
        }

        if (skillArrayList.contains(selectedText)) {
            ArrayList<int[]> affectedTiles = presenter.getAffectedTilesForPlayer(selectedText);
            battleView.highlightTiles(affectedTiles);
        }
    }

    private void processSkillInfoSelected(String selectedText) {
        if (!Objects.equals(selectedText, "[Skill Info]")) {
            return;
        }
        String name = skillBar.getSelectedSkill();
        String alertMsg;
        if (name == null || name.isEmpty() || name.equals("invalid_skill")) {
            alertMsg = presenter.getString(R.string.selectSkillForInfoAlert);
        } else {
            alertMsg = presenter.getSkillBattleDescriptionByName(name);
        }
        alertPopUp = new AlertPopUp(alertMsg, presenter, false);
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

    private void processConfirm(String selectedText, DisplayOptions currentDisplay) {
        if (!Objects.equals(selectedText, "[Confirm]")) {
            return;
        }

        switch (currentDisplay) {
            case SKILL_BAR:
                handleSkillConfirmation();
                break;
            case MOVE_BAR:
                handleMoveConfirmation();
                break;
        }

        changeDisplay(DisplayOptions.ACTION_BAR);
    }

    private void handleSkillConfirmation() {
        battleView.setTileHighlightColor(PLAYER_TILE_HIGHLIGHT_COLOR);
        battleView.flashTiles();
        hasAttacked = true;
        actionBar.disableButton("[ATK]");
    }

    private void handleMoveConfirmation() {
        String movementDirection = moveBar.getSelectedArrowDirection();
        presenter.movePlayer(movementDirection);
        hasMoved = true;
        actionBar.disableButton("[MOVE]");
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

        if (confirmPopUp != null) {
            confirmPopUp.draw(canvas, paint);
        }

        if (alertPopUp != null) {
            alertPopUp.draw(canvas, paint);
        }
    }

    public void changeDisplay(DisplayOptions displayOption){
        currentDisplay = displayOption;
    }

    public void startAnimation() {
        isAnimationPlaying = true;
    }

    public void endAnimation() {
        isAnimationPlaying = false;
    }

    public String getSelectedSkill(){
        return skillBar.getSelectedSkill();
    }
}
