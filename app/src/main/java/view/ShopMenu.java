package view;

import static view.ViewConstants.OVERLAY_DARK_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import presenter.GamePresenter;

public class ShopMenu {
    private Map<String, MenuItem> menuItemsList = new LinkedHashMap<>();
    private MenuItem buyButton;
    private RadioBtnList skillRadioBtnList;
    private String selectedSkill;

    public ShopMenu(Context context){
        int x = 350;
        int y = 200;
        int margin = 50;

        MenuItem playerCard = new MenuItem(x, y, 200, 350, "Bobette", false, context);
        menuItemsList.put("playerCard", playerCard);
        x += playerCard.getWidth() + margin;

        MenuItem items = new MenuItem(x, y, 500, 600, "ITEMS", false, context);
        menuItemsList.put("items", items);
        x += items.getWidth();

        MenuItem itemInfo = new MenuItem(x, y, 500, 600, "[Item Name]", false, context);
        menuItemsList.put("item_info", itemInfo);
        x += itemInfo.getWidth() + margin;

        MenuItem closeButton = new MenuItem(x, y, 80, 80, "X", true, context);
        menuItemsList.put("close_button", closeButton);

    }

    public void updateMenuTexts(GamePresenter presenter) {
        Objects.requireNonNull(menuItemsList.get("playerCard")).updateText(presenter.getPlayerNameHealthAndTokens());
    }

    public void updateItemInfo(String info, GamePresenter presenter){
        Objects.requireNonNull(menuItemsList.get("items")).updateText("Skill Tome\nAn iten that can be used to increase the experience of any skill\n\nPrice: 10 tokens");
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawColor(OVERLAY_DARK_COLOR);

        // draws each menu element
        for (MenuItem menuitem : menuItemsList.values()) {
            menuitem.draw(canvas, paint);
        }
    }
}
