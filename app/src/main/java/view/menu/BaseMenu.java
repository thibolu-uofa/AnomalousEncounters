package view.menu;

import static view.ViewConstants.CANVAS_WIDTH;
import static view.ViewConstants.OVERLAY_DARK_COLOR;
import static view.ViewConstants.SCREEN_HEIGHT;
import static view.ViewConstants.SCREEN_WIDTH;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import presenter.GamePresenter;
import view.AlertPopUp;
import view.ConfirmPopUp;
import view.Rectangle;

public abstract class BaseMenu {
    protected Context context;
    protected GamePresenter presenter;
    protected Map<String, MenuItem> menuItemsList = new LinkedHashMap<>();
    protected int PLAYER_CARD_WIDTH = 400;
    protected int PLAYER_CARD_HEIGHT = 200;
    protected ConfirmPopUp confirmPopUp;
    protected AlertPopUp alertPopUp;
    protected boolean isClosed = true;

    protected BaseMenu(GamePresenter presenter, Context context) {
        this.context = context;
        this.presenter = presenter;
        createCloseBtn(context);
    }

    private void createCloseBtn(Context context) {
        int x = (int) (SCREEN_WIDTH * 0.95);
        int y = (int) (SCREEN_HEIGHT * 0.1);
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

    public abstract void updateMenuTexts(GamePresenter presenter);

    public abstract void checkForUserTouch(float eventX, float eventY, GamePresenter presenter);

    public abstract void draw(Canvas canvas, Paint paint);

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

    public void openMenu() {
        isClosed = false;
    }

    public void closeMenu() {
        isClosed = true;
    }

    public boolean isMenuClosed() {
        return isClosed;
    }

    /**
     * Checks if the user has tapped the close button of the menu.
     * @param eventX The x-coordinate of the touch event
     * @param eventY The y-coordinate of the touch event
     * @param presenter The GamePresenter instance used to check hitbox collision
     * @return true if the touch event is within the close button's hitbox, false otherwise
     */
    public boolean hasClosedMenu(float eventX, float eventY, GamePresenter presenter){
        int x = Objects.requireNonNull(menuItemsList.get("close_button")).getX();
        int y = Objects.requireNonNull(menuItemsList.get("close_button")).getY();
        int width = Objects.requireNonNull(menuItemsList.get("close_button")).getWidth();
        int height = Objects.requireNonNull(menuItemsList.get("close_button")).getHeight();
        return presenter.isInHitbox((int) eventX, (int) eventY, x, x + width, y + height, y);
    }
}

