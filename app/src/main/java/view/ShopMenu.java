package view;

import static view.ViewConstants.FONT_SIZE_EXTRA_SMALL;
import static view.ViewConstants.FONT_SIZE_SMALL;
import static view.ViewConstants.OVERLAY_DARK_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.anomalousencounters.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import presenter.GamePresenter;

public class ShopMenu {
    private Map<String, MenuItem> menuItemsList = new LinkedHashMap<>();
    private MenuItem buyButton;
    private RadioBtnList itemRadioBtnList;
    private String selectedItem;

    public ShopMenu(GamePresenter presenter, Context context){
        int x = 250;
        int y = 150;
        int margin = 50;

        MenuItem playerCard = new MenuItem(x, y, 200, 350, "Bobette", false, context);
        menuItemsList.put("playerCard", playerCard);
        x += playerCard.getWidth() + margin;

        int MENU_HEADING_HEIGHT = 90;
        int MENU_HEIGHT = 600;
        int MENU_WIDTH = 750;
        MenuItem items_heading = new MenuItem(x, y, MENU_HEADING_HEIGHT, MENU_WIDTH, "ITEMS", true, context);
        MenuItem items = new MenuItem(x, y + MENU_HEADING_HEIGHT, MENU_HEIGHT, MENU_WIDTH, "", false, context);
        menuItemsList.put("items_heading", items_heading);
        menuItemsList.put("items", items);

        ArrayList<String> itemArrayList = presenter.getShopItemsArray();
        itemRadioBtnList = new RadioBtnList(x + 15, y + MENU_HEADING_HEIGHT + 20, context, itemArrayList, MENU_WIDTH, MENU_HEIGHT);
        x += items.getWidth();

        MenuItem itemName = new MenuItem(x, y, MENU_HEADING_HEIGHT, MENU_WIDTH, "[Item Name]", true, context);
        MenuItem itemInfo = new MenuItem(x, y + MENU_HEADING_HEIGHT, MENU_HEIGHT, MENU_WIDTH, "", false, context);
        itemInfo.changeFontSize(FONT_SIZE_SMALL);
        menuItemsList.put("item_name", itemName);
        menuItemsList.put("item_info", itemInfo);

        x += itemInfo.getWidth() + margin;

        MenuItem closeButton = new MenuItem(x, y, 70, 60, "X", false, context);
        menuItemsList.put("close_button", closeButton);

//        ConfirmPopUp confirmPopUp = new ConfirmPopUp(getContext().getString(R.string.purchaseConfirmationMsg, "shark",12), getContext());
//        confirmPopUp.draw(canvas, paint);
    }

    public void updateMenuTexts(GamePresenter presenter) {
        Objects.requireNonNull(menuItemsList.get("playerCard")).updateText(presenter.getPlayerNameHealthAndTokens());
    }

    private void updateItemInfo(GamePresenter presenter){
        Objects.requireNonNull(menuItemsList.get("item_name")).updateText(selectedItem);
        Objects.requireNonNull(menuItemsList.get("item_info")).updateText(presenter.getItemShopInfo(selectedItem));
    }

    public String checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        String radioBtnPressed = itemRadioBtnList.checkForBtnPress(eventX, eventY, presenter);
        if (!Objects.equals(radioBtnPressed, "")) {
            selectedItem = radioBtnPressed;
            updateItemInfo(presenter);
            return radioBtnPressed;
        }

        return "";
    }

    public boolean hasClosedMenu(float eventX, float eventY, GamePresenter presenter){
        int x = Objects.requireNonNull(menuItemsList.get("close_button")).getX();
        int y = Objects.requireNonNull(menuItemsList.get("close_button")).getY();
        int width = Objects.requireNonNull(menuItemsList.get("close_button")).getWidth();
        int height = Objects.requireNonNull(menuItemsList.get("close_button")).getHeight();
        return presenter.isInHitbox((int) eventX, (int) eventY, x, x + width, y + height, y);
    }


    public void draw(Canvas canvas, Paint paint){
        canvas.drawColor(OVERLAY_DARK_COLOR);

        // draws each menu element
        for (MenuItem menuitem : menuItemsList.values()) {
            menuitem.draw(canvas, paint);
        }

        itemRadioBtnList.draw(canvas, paint);
    }
}
