package view;


import static view.ViewConstants.CONFIRM_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_SMALL;
import static view.ViewConstants.OVERLAY_DARK_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.example.anomalousencounters.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import presenter.GamePresenter;

public class ShopMenu extends BaseMenu{
    private MenuItem buyButton;
    private final RadioBtnList itemRadioBtnList;
    private String selectedItem;

    public ShopMenu(GamePresenter presenter, Context context){
        super(presenter, context);
        int x = 250;
        int Y = 150;
        int MARGIN = 50;
        int MENU_HEADING_HEIGHT = 90;
        int MENU_HEIGHT = 600;
        int MENU_WIDTH = 750;
        int RADIO_X_PAD = 15;
        int RADIO_Y_PAD = 20;
        int BUTTON_Y_PAD = 50;

        String ITEM_HEADING = "ITEMS";
        String ITEM_NAME_FILLER = "[Item Name]";
        String BUY_TEXT = "BUY";
        String BLANK_TEXT = "";

        createPlayerCard(new Rectangle(x, Y), context);

        x += PLAYER_CARD_WIDTH + MARGIN;

        MenuItem items_heading = new MenuItem(x, Y, MENU_HEADING_HEIGHT, MENU_WIDTH, ITEM_HEADING, true, context);
        menuItemsList.put("items_heading", items_heading);

        MenuItem items = new MenuItem(x, Y + MENU_HEADING_HEIGHT, MENU_HEIGHT, MENU_WIDTH, BLANK_TEXT, false, context);
        menuItemsList.put("items", items);

        ArrayList<String> itemArrayList = presenter.getShopItemsArray();
        Rectangle itemListRect = new Rectangle(x + RADIO_X_PAD, Y + MENU_HEADING_HEIGHT + RADIO_Y_PAD);
        itemRadioBtnList = new RadioBtnList(itemListRect.x, itemListRect.y, context, itemArrayList, MENU_WIDTH, MENU_HEIGHT);
        x += items.getWidth();

        MenuItem itemName = new MenuItem(x, Y, MENU_HEADING_HEIGHT, MENU_WIDTH, ITEM_NAME_FILLER, true, context);
        menuItemsList.put("item_name", itemName);

        MenuItem itemInfo = new MenuItem(x, Y + MENU_HEADING_HEIGHT, MENU_HEIGHT, MENU_WIDTH, BLANK_TEXT, false, context);
        itemInfo.changeFontSize(FONT_SIZE_SMALL);
        menuItemsList.put("item_info", itemInfo);

        int BUY_BTN_X = x + (MENU_WIDTH/2) - (PLAYER_CARD_WIDTH/2);
        int BUY_BTN_Y = Y + MENU_HEADING_HEIGHT + MENU_HEIGHT + BUTTON_Y_PAD;
        buyButton = new MenuItem(BUY_BTN_X, BUY_BTN_Y, MENU_HEADING_HEIGHT, PLAYER_CARD_WIDTH, BUY_TEXT,true, context);
        buyButton.changeFontColor(CONFIRM_TEXT_COLOR);

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

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        if (confirmPopUp != null) {
            boolean userTouchedPopUp = confirmPopUp.didUserTouchButton(eventX, eventY, presenter);
            if (userTouchedPopUp) {
                boolean didUserConfirm = confirmPopUp.didUserConfirm();
                if (didUserConfirm) {
                    Log.d("Confirm" , "User Confirmed purchase");
                } else {
                    Log.d("Cancel" , "User Cancelled purchase");
                }
                confirmPopUp = null;
            }
            return;
        }

        String radioBtnPressed = itemRadioBtnList.checkForBtnPress(eventX, eventY, presenter);
        if (!Objects.equals(radioBtnPressed, "")) {
            selectedItem = radioBtnPressed;
            updateItemInfo(presenter);
        }

        int[] textBounds = buyButton.getMenuPositionBound();
        int leftX = textBounds[0], rightX = textBounds[1], topY = textBounds[2], bottomY = textBounds[3];
        boolean hasBuyBtnBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, leftX, rightX, topY, bottomY);
        if (hasBuyBtnBeenPressed && selectedItem != null) {
            int price = presenter.getItemPriceByName(selectedItem);
            confirmPopUp = new ConfirmPopUp(context.getString(R.string.purchaseConfirmationMsg, selectedItem, price), context);
//            confirmPopUp.draw(canvas, paint);
        }
    }

    public boolean hasClosedMenu(float eventX, float eventY, GamePresenter presenter){
        int x = Objects.requireNonNull(menuItemsList.get("close_button")).getX();
        int y = Objects.requireNonNull(menuItemsList.get("close_button")).getY();
        int width = Objects.requireNonNull(menuItemsList.get("close_button")).getWidth();
        int height = Objects.requireNonNull(menuItemsList.get("close_button")).getHeight();
        return presenter.isInHitbox((int) eventX, (int) eventY, x, x + width, y + height, y);
    }


    public void draw(Canvas canvas, Paint paint){
        drawOverlay(canvas, paint);
        drawMenuItems(canvas, paint);
        itemRadioBtnList.draw(canvas, paint);
        buyButton.draw(canvas, paint);
        if (confirmPopUp != null) {
            confirmPopUp.draw(canvas, paint);
        }
    }
}
