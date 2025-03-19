package view;

import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_LARGE;
import static view.ViewConstants.FONT_SIZE_SMALL;
import static view.ViewConstants.SECONDARY_TEXT_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;

import presenter.GamePresenter;

public class ActionBar {
    private int x;
    private int y;
    private ArrayList<MenuText> actionButtons = new ArrayList<>();
    private MenuText endTurn;
    int LEFT_PADDING = 90;
    int TOP_PADDING = 100;

    public ActionBar(int x, int y, int width, Context context){
        this.x = x;
        this.y = y;
        MenuText attack = new MenuText("[ATK]", FONT_SIZE_LARGE, DEFAULT_TEXT_COLOR, width, false, context, true);
        actionButtons.add(attack);

        MenuText use = new MenuText("[USE]", FONT_SIZE_LARGE, DEFAULT_TEXT_COLOR, width, false, context, true);
        actionButtons.add(use);

        MenuText move = new MenuText("[MOVE]", FONT_SIZE_LARGE, DEFAULT_TEXT_COLOR, width, false, context, true);
        actionButtons.add(move);

        endTurn = new MenuText("End Turn", FONT_SIZE_LARGE, SECONDARY_TEXT_COLOR, width, false, context, true);
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter){
        ArrayList<MenuText> allButtons = new ArrayList<>(actionButtons);
        allButtons.add(endTurn);

        for (int i = 0; i < allButtons.size(); i++) {
            MenuText button = allButtons.get(i);
            int leftX = x + LEFT_PADDING;
            int rightX = leftX + button.getActualTextWidth();
            int bottomY = button.getY();
            int topY = bottomY + button.getHeight();
            boolean actionBtnHasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, leftX, rightX, topY, bottomY);
            if (actionBtnHasBeenPressed) {
                Log.d("You Pressed a Btn", "You pressed a btn");
            }
        }
    }

    public void draw(Canvas canvas){
        int PADDING = 30;
        int text_y = y + PADDING + TOP_PADDING;
        for(MenuText actionButton: actionButtons) {
            actionButton.draw(canvas, x + LEFT_PADDING, text_y);
            text_y += PADDING + actionButton.getHeight();
        }
        int END_TURN_Y = 550;
        endTurn.draw(canvas, x + LEFT_PADDING, y + END_TURN_Y);
    }
}
