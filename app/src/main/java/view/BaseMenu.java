package view;

import static view.ViewConstants.CANVAS_WIDTH;
import static view.ViewConstants.OVERLAY_DARK_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.LinkedHashMap;
import java.util.Map;

import presenter.GamePresenter;

public class BaseMenu {
    protected Context context;
    protected GamePresenter presenter;
    protected Map<String, MenuItem> menuItemsList = new LinkedHashMap<>();
    protected int PLAYER_CARD_WIDTH = 400;
    protected int PLAYER_CARD_HEIGHT = 200;
    protected ConfirmPopUp confirmPopUp;
    protected AlertPopUp alertPopUp;

    protected BaseMenu(GamePresenter presenter, Context context) {
        this.context = context;
        this.presenter = presenter;
        createCloseBtn(context);
    }

    private void createCloseBtn(Context context) {
        int x = CANVAS_WIDTH - 120;
        int y = 80;
        int size = 70;
        String text = "X";
        MenuItem closeButton = new MenuItem(x, y, size, size, text, true, context);
        menuItemsList.put("close_button", closeButton);
    }

    protected void createPlayerCard(Rectangle rect, Context context) {
        String text = "[Player Name]";
        MenuItem playerCard = new MenuItem(rect.x, rect.y, PLAYER_CARD_HEIGHT, PLAYER_CARD_WIDTH, text, false, context);
        menuItemsList.put("playerCard", playerCard);
    }

    protected boolean hasBtnBeenPressed(MenuItem button, int eventX, int eventY, GamePresenter presenter) {
        int[] textBounds = button.getMenuPositionBound();
        int leftX = textBounds[0], rightX = textBounds[1], topY = textBounds[2], bottomY = textBounds[3];
        return presenter.isInHitbox(eventX, eventY, leftX, rightX, topY, bottomY);
    }

    protected void drawOverlay(Canvas canvas, Paint paint){
        canvas.drawColor(OVERLAY_DARK_COLOR);
    }

    protected void drawMenuItems(Canvas canvas, Paint paint){
        for (MenuItem menuitem : menuItemsList.values()) {
            menuitem.draw(canvas, paint);
        }
    }

    protected void drawPopUps(Canvas canvas, Paint paint) {
        if (confirmPopUp != null) {
            confirmPopUp.draw(canvas, paint);
        }
        if (alertPopUp != null){
            alertPopUp.draw(canvas, paint);
        }
    }
}

