package view;

import static view.menu.MenuText.calculateMinHeightRequired;
import static view.ViewConstants.CANVAS_HEIGHT;
import static view.ViewConstants.CANVAS_WIDTH;
import static view.ViewConstants.FONT_SIZE_MEDIUM;
import static view.ViewConstants.OVERLAY_DARK_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import presenter.GamePresenter;
import view.menu.MenuItem;

abstract class BasePopUp {
    protected final MenuItem messageBox;
    protected ArrayList<MenuItem> buttons = new ArrayList<>();
    private final String message;

    protected BasePopUp(String message, Context context, boolean isTextCentred) {
        this.message = message;
        int centerX = CANVAS_WIDTH / 2;
        int WIDTH = 1000;
        int x = centerX - (WIDTH / 2);
        int y = (int) (CANVAS_HEIGHT * 0.15);

        int HEIGHT = 250;
        int PADDING = 90;
        int minHeightRequired = calculateMinHeightRequired(message, FONT_SIZE_MEDIUM, WIDTH, isTextCentred, context);
        if (minHeightRequired + PADDING > HEIGHT) {
            HEIGHT = minHeightRequired + PADDING;
        }

        messageBox = new MenuItem(x, y, HEIGHT, WIDTH, message, isTextCentred, context);
    }

    protected boolean checkForUserTouchOnButton(MenuItem button, float eventX, float eventY, GamePresenter presenter) {
        int[] textBounds = button.getMenuPositionBound();
        int leftX = textBounds[0], rightX = textBounds[1], topY = textBounds[2], bottomY = textBounds[3];
        return presenter.isInHitbox((int) eventX, (int) eventY, leftX, rightX, topY, bottomY);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawColor(OVERLAY_DARK_COLOR);
        messageBox.draw(canvas, paint);
        for(MenuItem button: buttons){
            button.draw(canvas, paint);
        }
    }

    public String getMessage() {
        return message;
    }
}
