package view;

import static view.ViewConstants.OVERLAY_DARK_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import presenter.GamePresenter;

public class PlayerMenu {
    private Map<String, MenuItem> menuItemsList = new LinkedHashMap<>();
    private boolean isOpen = false;

    /**
     * Creates a player menu interface with various menu items.
     * Initializes and positions each component with appropriate dimensions and spacing
     * using relative positioning.
     * @param context The application context required for menu item creation
     */
    public PlayerMenu(Context context){
        int x = 100;
        int y = 200;
        int margin = 50;
        MenuItem playerCard = new MenuItem(x, y, 200, 350, "Bobette", false, context);
        menuItemsList.put("playerCard", playerCard);
        x += playerCard.getWidth() + margin;

        MenuItem skills = new MenuItem(x, y, 500, 600, "Skills", false, context);
        menuItemsList.put("skills", skills);
        x += skills.getWidth();

        MenuItem level = new MenuItem(x, y, 500, 130, "LV", true, context);
        menuItemsList.put("skill_levels", level);
        x += level.getWidth() + margin;

        MenuItem items = new MenuItem(x, y, 500, 700, "Items", false, context);
        menuItemsList.put("items", items);
        x += items.getWidth();

        MenuItem amountOfItems = new MenuItem(x, y, 500, 130, "#", true, context);
        menuItemsList.put("item_amounts", amountOfItems);
        x += amountOfItems.getWidth() + margin;

        MenuItem closeButton = new MenuItem(x, y, 80, 80, "X", true, context);
        menuItemsList.put("close_button", closeButton);

        MenuItem infoButton = new MenuItem(400, y, 500, 400, "INFO", true, context);
        //menuItemsList.add(infoButton);
    }

    public void updateMenuTexts(GamePresenter presenter) {
        Objects.requireNonNull(menuItemsList.get("playerCard")).updateText(presenter.getPlayerNameHealthAndTokens());
        Objects.requireNonNull(menuItemsList.get("skills")).updateText("Skills\n" + presenter.getSkillNamesString());
        Objects.requireNonNull(menuItemsList.get("skill_levels")).updateText("LV\n" + presenter.getSkillLevelsString());
        Objects.requireNonNull(menuItemsList.get("items")).updateText("Items\n" + presenter.getItemNames());
        Objects.requireNonNull(menuItemsList.get("item_amounts")).updateText("#\n" + presenter.getItemAmounts());
    }

    public void draw(Canvas canvas, Paint paint){
        if (isOpen) {
            canvas.drawColor(OVERLAY_DARK_COLOR);

            // draws each menu element
            for (MenuItem menuitem : menuItemsList.values()) {
                menuitem.draw(canvas, paint);
            }
        }
    }

    public void openMenu() {
        isOpen = true;
    }

    public void closeMenu() {
        isOpen = false;
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

    public boolean isOpen() {
        return isOpen;
    }
}
