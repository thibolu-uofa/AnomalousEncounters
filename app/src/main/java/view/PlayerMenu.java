package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import presenter.GamePresenter;

public class PlayerMenu {
    private Map<String, MenuItem> menuItemsList = new LinkedHashMap<>();
    private boolean isOpen = false;

    public PlayerMenu(Context context){
        int x = 100;
        int y = 200;
        int margin = 50;
        MenuItem playerCard = new MenuItem(x, y, 300, 350, "Bobette", context);
        menuItemsList.put("playerCard", playerCard);
        x += playerCard.getWidth() + margin;

        MenuItem skills = new MenuItem(x, y, 500, 600, "Skills", context);
        menuItemsList.put("skills", skills);
        x += skills.getWidth();

        MenuItem level = new MenuItem(x, y, 500, 150, "LV", context);
        menuItemsList.put("skill_levels", level);
        x += level.getWidth() + margin;

        MenuItem items = new MenuItem(x, y, 500, 700, "Items", context);
        menuItemsList.put("items", items);
        x += items.getWidth();

        MenuItem amountOfItems = new MenuItem(x, y, 500, 150, "#", context);
        menuItemsList.put("item_amounts", amountOfItems);
        x += amountOfItems.getWidth() + margin;

        MenuItem infoButton = new MenuItem(400, y, 500, 400, "INFO", context);
        //menuItemsList.add(infoButton);

        MenuItem closeButton = new MenuItem(x, y, 80, 80, "X", context);
        menuItemsList.put("close_button", closeButton);
    }

    public void updateMenuTexts(GamePresenter presenter) {
        menuItemsList.get("playerCard").updateText(presenter.getPlayerNameHealthAndTokens());
        menuItemsList.get("skills").updateText("Skills\n" + presenter.getSkillNames());
        menuItemsList.get("skill_levels").updateText("LV\n" + presenter.getSkillLevel());
        menuItemsList.get("items").updateText("Items\n" + presenter.getItemNames());
        menuItemsList.get("item_amounts").updateText("#\n" + presenter.getItemAmounts());
    }

    public void draw(Canvas canvas, Paint paint){
        if (isOpen) {
            for (MenuItem menuitem : menuItemsList.values()) {
                menuitem.draw(canvas, paint);
            }
        }
    }

    public void openMenu() {
        isOpen = true;
    }
    public void hasClosedMenu(float eventX, float eventY, GamePresenter presenter){
        int x = Objects.requireNonNull(menuItemsList.get("close_button")).getX();
        int y = Objects.requireNonNull(menuItemsList.get("close_button")).getY();
        int width = Objects.requireNonNull(menuItemsList.get("close_button")).getWidth();
        int height = Objects.requireNonNull(menuItemsList.get("close_button")).getHeight();
        if (presenter.isInHitbox((int) eventX, (int) eventY, x, x + width, y + height, y)) {
            isOpen = false;
        }
    }

    public boolean isOpen() {
        return isOpen;
    }
}
