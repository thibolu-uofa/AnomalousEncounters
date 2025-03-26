package view;

import static view.ViewConstants.BATTLE_BACKGROUND_COLOR;
import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_MEDIUM;
import static view.ViewConstants.OVERLAY_DARK_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Objects;

import presenter.GamePresenter;

public class EndBattleScreen {
    private MenuItem endBattleMsg;
    private MenuText dropAmounts;
    private MenuItem continueButton;
    private boolean isPlayerWinner;

    public EndBattleScreen(boolean isPlayerWinner, Context context){
        this.isPlayerWinner = isPlayerWinner;
        String endBattleText;
        String dropAmountsText;
        String continueButtonText = "Press Onwards";
        if (isPlayerWinner){
            endBattleText = "Entity has been purified\n\nAnomalous Drops\nAnomalous Essence\nAnomalous Shard\nAnomalous Crystal";
            dropAmountsText =  "\nx4\nx2\nx1";
            int x = 850, y = 200, width = 750, height = 450;
            endBattleMsg = new MenuItem(x, y, height, width, endBattleText, false, context);
            dropAmounts = new MenuText(dropAmountsText, FONT_SIZE_MEDIUM, DEFAULT_TEXT_COLOR, width, false, context, false);
            dropAmounts.setXAndY(x + width - 85, y + 130);
            continueButton = new MenuItem(x + (width/4), y + height + 75, height/3, width/2, continueButtonText, true, context);
        } else {
            endBattleText = "You absconded\nfrom the\n In-Between\n\nYou have\nbeen tainted";
            int x = 975, y = 200, width = 500, height = 380;
            endBattleMsg = new MenuItem(x, y, height, width, endBattleText, false, context);
            continueButton = new MenuItem((int) (x +  (width * 0.3)/2), y + height + 75, height/3 + 15, (int) (width * 0.7), continueButtonText, true, context);
        }
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
