package view;

import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.DISABLED_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_LARGE;
import static view.ViewConstants.SECONDARY_TEXT_COLOR;

import android.content.Context;
import android.graphics.Canvas;

import presenter.GamePresenter;

public class ActionBar extends BaseMenuBar {
    int LEFT_PADDING = 90;
    int TOP_PADDING = 100;

    public ActionBar(int x, int y, int width, Context context){
        super(x, y, width, context);
        MenuText attack = new MenuText("[ATK]", FONT_SIZE_LARGE, DEFAULT_TEXT_COLOR, width, false, context, true);
        textButtons.add(attack);

        MenuText move = new MenuText("[MOVE]", FONT_SIZE_LARGE, DEFAULT_TEXT_COLOR, width, false, context, true);
        textButtons.add(move);

        MenuText use = new MenuText("[USE]", FONT_SIZE_LARGE, DEFAULT_TEXT_COLOR, width, false, context, true);
        textButtons.add(use);

        MenuText endTurn = new MenuText("End Turn", FONT_SIZE_LARGE, SECONDARY_TEXT_COLOR, width, false, context, true);
        textButtons.add(endTurn);

        setTextPositions();
    }

    private void setTextPositions() {
        int PADDING = 30;
        int text_y = y + PADDING + TOP_PADDING;
        for(MenuText button: textButtons) {
            button.setXAndY(x + LEFT_PADDING, text_y);
            text_y += PADDING + button.getHeight();
        }

        int END_TURN_Y = 550;
        textButtons.get(textButtons.size() - 1).setXAndY(x + LEFT_PADDING, y + END_TURN_Y);
    }

    public String checkForUserTouch(float eventX, float eventY, GamePresenter presenter){
        return checkForUserTouchTextButtons(eventX, eventY, presenter);
    }

    public void draw(Canvas canvas){
        drawTextButtons(canvas);
    }

    public void disableButton(String action) {
        switch(action){
            case "[ATK]":
                textButtons.get(0).setColor(DISABLED_TEXT_COLOR);
                break;
            case "[MOVE]":
                textButtons.get(1).setColor(DISABLED_TEXT_COLOR);
                break;
            case "[USE]":
                textButtons.get(2).setColor(DISABLED_TEXT_COLOR);
                break;
        }
    }

    public void resetButtons() {
        for (int i = 0; i < 3; i++) {
            textButtons.get(i).setColor(DEFAULT_TEXT_COLOR);
        }
    }
}
