package view;


import static view.ViewConstants.CONFIRM_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_SMALL;
import static view.ViewConstants.SCREEN_WIDTH;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.anomalousencounters.R;

import java.util.ArrayList;
import java.util.Objects;

import presenter.GamePresenter;
import view.menu.BaseMenu;
import view.menu.MenuItem;

public class ShopMenu extends BaseMenu {
    private MenuItem buyButton;
    private RadioBtnList itemRadioBtnList;
    private String selectedItem;
    private final int Y = 150;
    private final int MENU_HEADING_HEIGHT = 90;
    private final int MENU_HEIGHT = 600;
    private final int MENU_WIDTH = 750;
    private final String BLANK_TEXT = "";

    public ShopMenu(GamePresenter presenter, Context context){
        super(presenter, context);
        int MARGIN = 50;
        int totalWidth = PLAYER_CARD_WIDTH + MARGIN + MENU_WIDTH + MARGIN + MENU_WIDTH;
        int x = SCREEN_WIDTH/2 - totalWidth/2;

        createPlayerCard(new Rectangle(x, Y), context);
        x += PLAYER_CARD_WIDTH + MARGIN;

        createItemMenu(x);
        x += MENU_WIDTH;

        createItemInfoMenu(x);
        createBuyButton(x);
    }

    private void createItemMenu(int x) {
        String ITEM_HEADING = "ITEMS";

        MenuItem items_heading = new MenuItem(x, Y, MENU_HEADING_HEIGHT, MENU_WIDTH, ITEM_HEADING, true, context);
        menuItemsList.put("items_heading", items_heading);

        MenuItem items = new MenuItem(x, Y + MENU_HEADING_HEIGHT, MENU_HEIGHT, MENU_WIDTH, BLANK_TEXT, false, context);
        menuItemsList.put("items", items);

        createItemList(x);
    }

    private void createItemList(int x) {
        ArrayList<String> itemArrayList = presenter.getShopItemsArray();
        int RADIO_X_PAD = 15;
        int RADIO_Y_PAD = 20;
        Rectangle itemListRect = new Rectangle(x + RADIO_X_PAD, Y + MENU_HEADING_HEIGHT + RADIO_Y_PAD);
        itemRadioBtnList = new RadioBtnList(itemListRect.x, itemListRect.y, context, itemArrayList, MENU_WIDTH, MENU_HEIGHT);
    }

    private void createItemInfoMenu(int x) {
        String ITEM_NAME_FILLER = "[Item Name]";

        MenuItem itemName = new MenuItem(x, Y, MENU_HEADING_HEIGHT, MENU_WIDTH, ITEM_NAME_FILLER, true, context);
        menuItemsList.put("item_name", itemName);

        MenuItem itemInfo = new MenuItem(x, Y + MENU_HEADING_HEIGHT, MENU_HEIGHT, MENU_WIDTH, BLANK_TEXT, false, context);
        itemInfo.changeFontSize(FONT_SIZE_SMALL);
        menuItemsList.put("item_info", itemInfo);
    }

    private void createBuyButton(int x) {
        String BUY_TEXT = "BUY";
        int BUY_BTN_X = x + (MENU_WIDTH/2) - (PLAYER_CARD_WIDTH/2);
        int BUTTON_Y_PAD = 50;
        int BUY_BTN_Y = Y + MENU_HEADING_HEIGHT + MENU_HEIGHT + BUTTON_Y_PAD;

        buyButton = new MenuItem(BUY_BTN_X, BUY_BTN_Y, MENU_HEADING_HEIGHT, PLAYER_CARD_WIDTH, BUY_TEXT,true, context);
        buyButton.changeFontColor(CONFIRM_TEXT_COLOR);
    }

    public void updateMenuTexts(GamePresenter presenter) {
        Objects.requireNonNull(menuItemsList.get("playerCard")).updateText(presenter.getPlayerNameHealthAndTokens());
    }

    private void updateItemInfo(GamePresenter presenter){
        Objects.requireNonNull(menuItemsList.get("item_name")).updateText(selectedItem);
        Objects.requireNonNull(menuItemsList.get("item_info")).updateText(presenter.getItemShopInfo(selectedItem));
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        if (isClosed) {
            return;
        }

        if (confirmPopUp != null) {
            handleConfirmPopUp(eventX, eventY, presenter);
            return;
        }

        if (alertPopUp != null) {
            handleAlertPopUp(eventX, eventY, presenter);
            return;
        }

        handleRadioBtnInteraction(eventX, eventY, presenter);

        handleBuyButton((int) eventX, (int) eventY, presenter);
    }

    private void handleConfirmPopUp(float eventX, float eventY, GamePresenter presenter) {
        boolean userTouchedPopUp = confirmPopUp.didUserTouchButton(eventX, eventY, presenter);
        if (userTouchedPopUp) {
            boolean didUserConfirm = confirmPopUp.didUserConfirm();
            if (didUserConfirm) {
                presenter.buySingleItem(selectedItem);
                String alertMsg = context.getString(R.string.successfulPurchase, selectedItem);
                alertPopUp = new AlertPopUp(alertMsg, context, true);
            }
            confirmPopUp = null;
        }
    }

    private void handleAlertPopUp(float eventX, float eventY, GamePresenter presenter) {
        boolean userClosePopUp = alertPopUp.didUserClosePopUp(eventX, eventY, presenter);

        if (userClosePopUp){
            alertPopUp = null;
        }
    }

    private void handleRadioBtnInteraction(float eventX, float eventY, GamePresenter presenter) {
        String radioBtnPressed = itemRadioBtnList.checkForBtnPressAndCheckBtn(eventX, eventY, presenter);
        if (!Objects.equals(radioBtnPressed, "")) {
            selectedItem = radioBtnPressed;
            updateItemInfo(presenter);
        }
    }

    private void handleBuyButton(int eventX, int eventY, GamePresenter presenter) {
        boolean hasBuyBtnBeenPressed = hasBtnBeenPressed(buyButton, eventX, eventY, presenter);
        if (hasBuyBtnBeenPressed && selectedItem != null) {
            int price = presenter.getItemPriceByName(selectedItem);
            boolean canAfford = presenter.canPlayerAffordItem(selectedItem);
            if (canAfford) {
                showPurchaseConfirmation(price);
            } else {
                showPurchaseError(price);
            }
        }
    }

    private void showPurchaseConfirmation(int price) {
        String confirmMsg = context.getString(R.string.purchaseConfirmationMsg, selectedItem, price);
        confirmPopUp = new ConfirmPopUp(confirmMsg, context, true);
    }

    private void showPurchaseError(int price) {
        String alertMsg = context.getString(R.string.notEnoughToken, price);
        alertPopUp = new AlertPopUp(alertMsg, context, true);
    }

    public boolean hasClosedMenu(float eventX, float eventY, GamePresenter presenter){
        int x = Objects.requireNonNull(menuItemsList.get("close_button")).getX();
        int y = Objects.requireNonNull(menuItemsList.get("close_button")).getY();
        int width = Objects.requireNonNull(menuItemsList.get("close_button")).getWidth();
        int height = Objects.requireNonNull(menuItemsList.get("close_button")).getHeight();
        return presenter.isInHitbox((int) eventX, (int) eventY, x, x + width, y + height, y);
    }


    public void draw(Canvas canvas, Paint paint){
        if (isClosed) {
            return;
        }

        drawOverlay(canvas, paint);
        drawMenuItems(canvas, paint);
        itemRadioBtnList.draw(canvas, paint);
        buyButton.draw(canvas, paint);
        drawPopUps(canvas, paint);
    }
}
