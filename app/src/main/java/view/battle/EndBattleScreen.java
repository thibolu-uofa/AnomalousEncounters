package view.battle;

import static view.ViewConstants.BATTLE_BACKGROUND_COLOR;
import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_MEDIUM;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

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
            initializeLoseBattleScreen(context);
        }
    }

    private void initializeWinBattleScreen(Context context, GamePresenter presenter) {
        String endBattleText = presenter.getEnemyDropsString();
        String dropAmountsText = presenter.getEnemyDropAmountsString();

        int x = 850, y = 200, width = 750, height = 450;

        endBattleMsg = new MenuItem(x, y, height, width, endBattleText, false, context);
        dropAmounts = new MenuText(dropAmountsText, FONT_SIZE_MEDIUM, DEFAULT_TEXT_COLOR, width, false, context, false);
        dropAmounts.setXAndY(x + width - 85, y + 130);

        createContinueButton(context, x, y, width, height);
    }

    private void initializeLoseBattleScreen(Context context) {
        String endBattleText = "You absconded\nfrom the\n In-Between\n\nYou have\nbeen tainted";

        int x = 975, y = 200, width = 500, height = 380;

        endBattleMsg = new MenuItem(x, y, height, width, endBattleText, false, context);
        createContinueButton(context, x, y, width, height);
    }

    private void createContinueButton(Context context, int x, int y, int width, int height) {
        String continueButtonText = "Press Onwards";

        int buttonWidth, buttonX, buttonHeight;

        if (isPlayerWinner) {
            buttonWidth = width/2;
            buttonX = x + (width/4);
            buttonHeight = height/3;
        } else {
            buttonWidth = (int) (width * 0.7);
            buttonX = (int) (x + (width * 0.3)/2);
            buttonHeight = height/3 + 15;
        }

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
