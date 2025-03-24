package view;

import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_MEDIUM;
import static view.ViewConstants.FONT_SIZE_SMALL;
import static view.ViewConstants.SECONDARY_TEXT_COLOR;

import java.util.ArrayList;
import presenter.GamePresenter;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

public abstract class BaseMenuBar {
    protected int x;
    protected int y;
    protected int width;
    protected ArrayList<MenuText> textButtons = new ArrayList<>();
    public BaseMenuBar(int x, int y, int width, Context context) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    protected MenuText createButton(String text, int fontSize, int textColor, boolean centered, Context context) {
        return new MenuText(text, fontSize, textColor, width, centered, context, true);
    }

    protected void addNavigationButtons(Context context) {
        MenuText confirmBtn = createButton("[Confirm]", FONT_SIZE_MEDIUM, DEFAULT_TEXT_COLOR, false, context);
        textButtons.add(confirmBtn);

        MenuText goBackBtn = createButton("[Go Back]", FONT_SIZE_MEDIUM, SECONDARY_TEXT_COLOR, false, context);
        textButtons.add(goBackBtn);
    }

    protected String checkForUserTouchTextButtons(float eventX, float eventY, GamePresenter presenter) {
        for (MenuText btn: textButtons) {
            int[] textBounds = btn.getTextPositionBound();
            int leftX = textBounds[0], rightX = textBounds[1], topY = textBounds[2], bottomY = textBounds[3];
            boolean hasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, leftX, rightX, topY, bottomY);
            if (hasBeenPressed) {
                Log.d("Button Pressed", "Button: " + btn.getText());
                return btn.getText();
            }
        }
        return "";
    }

    protected void drawTextButtons(Canvas canvas){
        for (MenuText button: textButtons) {
            button.draw(canvas);
        }
    }
}
