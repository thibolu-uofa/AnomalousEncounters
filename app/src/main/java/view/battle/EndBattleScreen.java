package view.battle;

import static view.ViewConstants.BATTLE_BACKGROUND_COLOR;
import static view.ViewConstants.CANVAS_WIDTH;
import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_MEDIUM;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import presenter.GamePresenter;
import view.menu.MenuItem;
import view.menu.MenuText;

public class EndBattleScreen {
    private MenuItem endBattleMsg;
    private MenuText dropAmounts;
    private MenuItem continueButton;
    private final boolean isPlayerWinner;

    public EndBattleScreen(boolean isPlayerWinner, Context context, GamePresenter presenter) {
        this.isPlayerWinner = isPlayerWinner;
        initializeEndBattleComponents(context, presenter);
    }

    private void initializeEndBattleComponents(Context context, GamePresenter presenter) {
        if (isPlayerWinner) {
            initializeWinBattleScreen(context, presenter);
        } else {
            initializeLoseBattleScreen(context, presenter);
        }
    }

    private void initializeWinBattleScreen(Context context, GamePresenter presenter) {
        String[] endBattleTextAndDropAmounts = presenter.getEndBattleTextAndDropAmounts();
        String endBattleText = endBattleTextAndDropAmounts[0];
        String dropAmountsText = endBattleTextAndDropAmounts[1];

        int width = 750, height = 380, x = CANVAS_WIDTH/2 - width/2, y = 200;

        endBattleMsg = new MenuItem(x, y, height, width, endBattleText, false, context);
        dropAmounts = new MenuText(dropAmountsText, FONT_SIZE_MEDIUM, DEFAULT_TEXT_COLOR, width, false, context, false);
        dropAmounts.setXAndY(x + width - 75, y + 200);

        createContinueButton(context, x, y, width, height);
    }

    //TODO: make endBattleText a string resource
    private void initializeLoseBattleScreen(Context context, GamePresenter presenter) {
        String tokensLost = String.valueOf(presenter.getTokensLost());
        String endBattleText = "You absconded from the In-Between.\n\nYou have been charged a Death Tax of " + tokensLost + " tokens";

        int width = 750, height = 350, x = CANVAS_WIDTH/2 - width/2, y = 200;
        float lineSpacing = 1.1f;

        endBattleMsg = new MenuItem(x, y, height, width, endBattleText, false, context);
        endBattleMsg.setLineSpacingMultiplier(lineSpacing);
        createContinueButton(context, x, y, width, height);
    }

    private void createContinueButton(Context context, int x, int y, int width, int height) {
        String continueButtonText = "Press Onwards";
        int buttonWidth = 375;
        int buttonX = x + (width/2) - (buttonWidth/2);
        int buttonHeight = 150;

        continueButton = new MenuItem(buttonX, y + height + 75, buttonHeight, buttonWidth, continueButtonText, true, context);
    }

    public boolean hasPressedContinueButton(float eventX, float eventY, GamePresenter presenter){
        int x = continueButton.getX();
        int y = continueButton.getY();
        int width = continueButton.getWidth();
        int height = continueButton.getHeight();
        return presenter.isInHitbox((int) eventX, (int) eventY, x, x + width, y + height, y);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawColor(BATTLE_BACKGROUND_COLOR);
        endBattleMsg.draw(canvas, paint);
        if (isPlayerWinner) {
            dropAmounts.draw(canvas);
        }
        continueButton.draw(canvas, paint);
    }
}
