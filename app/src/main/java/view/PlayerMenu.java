package view;

import static view.ViewConstants.GREEN_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_MEDIUM;
import static view.ViewConstants.YELLOW_TEXT_COLOR;
import static view.ViewConstants.SCREEN_WIDTH;
import static view.ViewConstants.BLUE_TEXT_COLOR;
import static view.menu.MenuText.calculateMinHeightRequired;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.anomalousencounters.R;

import java.util.ArrayList;
import java.util.Objects;

import presenter.GamePresenter;
import view.menu.BaseMenu;
import view.menu.MenuItem;

public class PlayerMenu extends BaseMenu {
    private RadioBtnList skillRadioBtnList;
    private RadioBtnList itemRadioBtnList;
    private MenuItem skillInfo;
    private MenuItem forgetSkill;
    private MenuItem itemInfo;
    private MenuItem sellItem;
    private MenuItem useItem;
    private String selectedItem;
    private String selectedSkill;
    private final int Y = 150;
    private final float LINE_SPACE_MULTIPLIER = 1.2f;
    private final int MENU_HEIGHT = 500;
    private final int SKILL_MENU_WIDTH = 630;
    private final int ITEM_MENU_WIDTH = 700;
    private final int COL_WIDTH = 100;
    private final int RADIO_X_PAD = 15;
    private final int RADIO_Y_PAD = 90;
    private final int BUTTON_WIDTH = 250;
    private final int BUTTON_HEIGHT = 75;
    private final int BUTTON_PADDING_Y = 40;
    private final int BUTTON_X_PADDING = 60;
    private final String INFO_TEXT = "INFO";
    private String forgetSkillMsg;
    private String sellItemMsg;
    private String useItemMsg;

    /**
     * Creates a player menu interface with various menu items.
     * Initializes and positions each component with appropriate dimensions and spacing
     * using relative positioning.
     * @param context The application context required for menu item creation
     */
    public PlayerMenu(GamePresenter presenter, Context context){
        super(presenter, context);
        int MARGIN = 50;
        int totalWidth = PLAYER_CARD_WIDTH + MARGIN + SKILL_MENU_WIDTH + COL_WIDTH + MARGIN + ITEM_MENU_WIDTH + COL_WIDTH;
        int x = SCREEN_WIDTH/2 - totalWidth/2;

        createPlayerCard(new Rectangle(x, Y), context);
        x += PLAYER_CARD_WIDTH + MARGIN;

        createSkillMenu(x);
        x += SKILL_MENU_WIDTH + COL_WIDTH + MARGIN;

        createItemMenu(x);
    }

    private void createSkillMenu(int x) {
        String SKILL_HEADING = "SKILLS";
        MenuItem skills = new MenuItem(x, Y, MENU_HEIGHT, SKILL_MENU_WIDTH, SKILL_HEADING, false, context);
        menuItemsList.put("skills", skills);

        ArrayList<String> skillArrayList = presenter.getSkillNamesArray();
        Rectangle skillListRect = new Rectangle(x + RADIO_X_PAD, Y + RADIO_Y_PAD);
        skillRadioBtnList = new RadioBtnList(skillListRect.x, skillListRect.y, context, skillArrayList, SKILL_MENU_WIDTH, MENU_HEIGHT);

        x += SKILL_MENU_WIDTH;
        createLevelColumn(x);

        int buttonStartingX = skills.getX();
        createSkillButtons(buttonStartingX);
    }

    private void createLevelColumn(int x) {
        String LEVEL_HEADING = "LV";

        MenuItem level = new MenuItem(x, Y, MENU_HEIGHT, COL_WIDTH, LEVEL_HEADING, true, context);
        level.setLineSpacingMultiplier(LINE_SPACE_MULTIPLIER);
        menuItemsList.put("skill_levels", level);
    }

    private void createSkillButtons(int startingX){
        int BUTTONS_Y = Y + MENU_HEIGHT + BUTTON_PADDING_Y;
        Rectangle infoRect = new Rectangle(startingX, BUTTONS_Y);

        int forgetRectX = startingX + BUTTON_WIDTH + BUTTON_X_PADDING;
        Rectangle forgetRect = new Rectangle(forgetRectX, BUTTONS_Y);

        String FORGET_SKILL_TEXT = "FORGET";
        skillInfo = new MenuItem(infoRect.x, infoRect.y, BUTTON_HEIGHT, BUTTON_WIDTH,INFO_TEXT, true, context );
        forgetSkill = new MenuItem(forgetRect.x, forgetRect.y, BUTTON_HEIGHT, BUTTON_WIDTH, FORGET_SKILL_TEXT,true, context);
        forgetSkill.changeFontColor(YELLOW_TEXT_COLOR);
    }

    private void createItemMenu(int x) {
        String ITEM_HEADING = "ITEMS";
        ArrayList<String> itemArrayList = presenter.getPlayerItemNamesArray();
        String itemListString = String.join("\n", itemArrayList);

        int ITEMS_HEIGHT = MENU_HEIGHT;
        int PADDING = 120;
        int minHeightRequired = calculateMinHeightRequired(ITEM_HEADING + '\n' + itemListString, FONT_SIZE_MEDIUM, ITEM_MENU_WIDTH, false, context);
        if (minHeightRequired > ITEMS_HEIGHT) {
            ITEMS_HEIGHT = minHeightRequired + PADDING;
        }
        MenuItem items = new MenuItem(x, Y, ITEMS_HEIGHT, ITEM_MENU_WIDTH, ITEM_HEADING, false, context);
        menuItemsList.put("items", items);

        Rectangle itemListRect = new Rectangle(x + RADIO_X_PAD, Y + RADIO_Y_PAD);
        itemRadioBtnList = new RadioBtnList(itemListRect.x, itemListRect.y, context, itemArrayList, ITEM_MENU_WIDTH, ITEMS_HEIGHT);

        x += items.getWidth();
        createItemAmountColumn(x, ITEMS_HEIGHT);

        int startingButtonX = items.getX();
        createItemButtons(startingButtonX, ITEMS_HEIGHT);
    }

    private void createItemAmountColumn(int x, int height) {
        String ITEM_AMOUNT_HEADING = "#";

        MenuItem amountOfItems = new MenuItem(x, Y, height, COL_WIDTH, ITEM_AMOUNT_HEADING, true, context);
        amountOfItems.setLineSpacingMultiplier(LINE_SPACE_MULTIPLIER);
        menuItemsList.put("item_amounts", amountOfItems);
    }

    private void createItemButtons(int startingX, int height){
        int BUTTONS_Y = Y + height + BUTTON_PADDING_Y;
        Rectangle infoRect = new Rectangle(startingX, BUTTONS_Y);

        int sellRectX = startingX + BUTTON_WIDTH + BUTTON_X_PADDING;
        Rectangle sellRect = new Rectangle(sellRectX, BUTTONS_Y);

        int useRectX = sellRectX + BUTTON_WIDTH + BUTTON_X_PADDING;
        Rectangle useRect = new Rectangle(useRectX, BUTTONS_Y);

        itemInfo = new MenuItem(infoRect.x, infoRect.y, BUTTON_HEIGHT, BUTTON_WIDTH,INFO_TEXT, true, context );

        String SELL_ITEM_TEXT = "SELL";
        sellItem = new MenuItem(sellRect.x, sellRect.y, BUTTON_HEIGHT, BUTTON_WIDTH, SELL_ITEM_TEXT,true, context);
        sellItem.changeFontColor(GREEN_TEXT_COLOR);

        String USE_ITEM_TEXT = "USE";
        useItem = new MenuItem(useRect.x, useRect.y, BUTTON_HEIGHT, BUTTON_WIDTH, USE_ITEM_TEXT,true, context);
        useItem.changeFontColor(BLUE_TEXT_COLOR);
    }

    public void updateMenuTexts(GamePresenter presenter) {
        Objects.requireNonNull(menuItemsList.get("playerCard")).updateText(presenter.getPlayerNameHealtsTokensAndPhase());
        Objects.requireNonNull(menuItemsList.get("skill_levels")).updateText("LV\n" + presenter.getSkillLevelsString());
        Objects.requireNonNull(menuItemsList.get("item_amounts")).updateText("#\n" + presenter.getItemAmounts());
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        if (isClosed) {
            return;
        }

        if (handlePopUps(eventX, eventY, presenter)) {
            return;
        }

        handleSelections(eventX, eventY, presenter);
        handleButtonPresses(eventX, eventY, presenter);
    }

    private boolean handlePopUps(float eventX, float eventY, GamePresenter presenter) {
        if (confirmPopUp != null) {
            handleConfirmPopUp(eventX, eventY, presenter);
            return true;
        }

        if (alertPopUp != null) {
            boolean userClosePopUp = alertPopUp.didUserClosePopUp(eventX, eventY, presenter);
            if (userClosePopUp) {
                alertPopUp = null;
            }
            return true;
        }

        return false;
    }

    private void handleConfirmPopUp(float eventX, float eventY, GamePresenter presenter) {
        boolean userTouchedPopUp = confirmPopUp.didUserTouchButton(eventX, eventY, presenter);
        if (userTouchedPopUp) {
            boolean didUserConfirm = confirmPopUp.didUserConfirm();
            if (!didUserConfirm) {
                confirmPopUp = null;
                return;
            }

            String message = confirmPopUp.getMessage();
            confirmPopUp = null;

            boolean isForgetSkill = Objects.equals(message, forgetSkillMsg);
            boolean isSellItem = Objects.equals(message, sellItemMsg);
            boolean isUseItem = Objects.equals(message, useItemMsg);

            if (isForgetSkill) {
                handleForgetSkillConfirmation(presenter);
            } else if (isSellItem) {
                handleSellItemConfirmation(presenter);
            } else if (isUseItem){
                handleUseItemConfirmation(presenter);
            }

            updateSkillListVisuals();
            updateItemListVisuals();
            selectedItem = null;
            selectedSkill = null;
        }
    }

    private void handleForgetSkillConfirmation(GamePresenter presenter) {
        presenter.removePlayerSkill(selectedSkill);

        String alertMsg = context.getString(R.string.successfullyForgotSkill, selectedSkill);
        alertPopUp = new AlertPopUp(alertMsg, context, true);
    }

    private void handleSellItemConfirmation(GamePresenter presenter) {
        double DISCOUNT_FACTOR = 0.8;
        int price = (int) (presenter.getItemPriceByName(selectedItem) * DISCOUNT_FACTOR);
        presenter.sellPlayerItem(selectedItem, price);

        String alertMsg = context.getString(R.string.successfullySoldItem, selectedItem, price);
        alertPopUp = new AlertPopUp(alertMsg, context, true);
    }

    private void handleUseItemConfirmation(GamePresenter presenter) {
        presenter.useItem(selectedItem);

        String alertMsg = context.getString(R.string.successfullyUsedItem, selectedItem);
        alertPopUp = new AlertPopUp(alertMsg, context, true);
    }

    private void handleSelections(float eventX, float eventY, GamePresenter presenter) {
        String itemBtnPressed = itemRadioBtnList.checkForBtnPressAndCheckBtn(eventX, eventY, presenter);
        String skillBtnPressed = skillRadioBtnList.checkForBtnPressAndCheckBtn(eventX, eventY, presenter);

        if (!Objects.equals(itemBtnPressed, "")) {
            selectedItem = itemBtnPressed;
        } else if (!Objects.equals(skillBtnPressed, "")) {
            selectedSkill = skillBtnPressed;
        }
    }

    private void handleButtonPresses(float eventX, float eventY, GamePresenter presenter) {
        handleSkillButtons(eventX, eventY, presenter);
        handleItemButtons(eventX, eventY, presenter);
    }

    private void handleSkillButtons(float eventX, float eventY, GamePresenter presenter) {
        boolean hasSkillInfoBeenPressed = hasBtnBeenPressed(skillInfo, (int) eventX, (int) eventY, presenter);
        boolean hasForgetSkillBeenPressed = hasBtnBeenPressed(forgetSkill, (int) eventX, (int) eventY, presenter);

        if ((hasSkillInfoBeenPressed || hasForgetSkillBeenPressed) && selectedSkill == null) {
            showSelectSkillAlertMsg();
            return;
        }

        if (hasSkillInfoBeenPressed) {
            String alertMsg = presenter.getSkillDescriptionByName(selectedSkill);
            alertPopUp = new AlertPopUp(alertMsg, context, false);
        }

        if (hasForgetSkillBeenPressed) {
            boolean hasMinAmountOfSkills= presenter.hasMinAmountOfSkills();
            if (hasMinAmountOfSkills) {
                String alertMsg = context.getString(R.string.minSkillLimitReached);
                alertPopUp = new AlertPopUp(alertMsg, context, true);
                return;
            }

            forgetSkillMsg = context.getString(R.string.forgetSkillConfirmationMsg, selectedSkill);
            confirmPopUp = new ConfirmPopUp(forgetSkillMsg, context, true);
        }
    }

    private void showSelectSkillAlertMsg() {
        String alertMsg = context.getString(R.string.selectSkillAlert);
        alertPopUp = new AlertPopUp(alertMsg, context, true);
    }

    private void handleItemButtons(float eventX, float eventY, GamePresenter presenter) {
        boolean hasItemInfoBeenPressed = hasBtnBeenPressed(itemInfo, (int) eventX, (int) eventY, presenter);
        boolean hasSellItemBeenPressed = hasBtnBeenPressed(sellItem, (int) eventX, (int) eventY, presenter);
        boolean hasUsedItemBeenPressed = hasBtnBeenPressed(useItem, (int) eventX, (int) eventY, presenter);

        if ((hasItemInfoBeenPressed || hasSellItemBeenPressed || hasUsedItemBeenPressed) && selectedItem == null) {
            showSelectItemAlertMsg();
            return;
        }

        if (hasItemInfoBeenPressed) {
            String alertMsg = presenter.getItemInfoByName(selectedItem);
            alertPopUp = new AlertPopUp(alertMsg, context, false);
        }

        if (hasSellItemBeenPressed) {
            double DISCOUNT_FACTOR = 0.8;
            int price = (int) (presenter.getItemPriceByName(selectedItem) * DISCOUNT_FACTOR);
            sellItemMsg = context.getString(R.string.sellItemConfirmationMsg, selectedItem, price);
            confirmPopUp = new ConfirmPopUp(sellItemMsg, context, true);
        }

        if (hasUsedItemBeenPressed){
            if (selectedItem.contains("Book of Skills")){
                if (Objects.equals(selectedSkill, "") || selectedSkill == null  ) {
                    String alertMsg = context.getString(R.string.noSkillSelectedMsg);
                    alertPopUp = new AlertPopUp(alertMsg, context, true);
                } else {
                    useItemMsg = context.getString(R.string.useSkillBookConfirmationMsg, selectedItem, selectedSkill);
                    confirmPopUp = new ConfirmPopUp(useItemMsg, context, true);
                }
                return;
            }
            boolean hasMaxHealth = presenter.hasMaxHealth();
            if (selectedItem.contains("Health Rune") && hasMaxHealth) {
                String alertMsg = context.getString(R.string.cannotUseRuneMaxHealth);
                alertPopUp = new AlertPopUp(alertMsg, context, true);
                return;
            }
            if (selectedItem.contains("Anomalous")){
                String alertMsg = context.getString(R.string.cannotUseItem);
                alertPopUp = new AlertPopUp(alertMsg, context, true);
                return;
            }

            boolean hasReachedMaxSkills = presenter.hasReachedMaxSkillLimit();
            if (selectedItem.contains("Skill Stone") && hasReachedMaxSkills) {
                String alertMsg = context.getString(R.string.skillLimitReached);
                alertPopUp = new AlertPopUp(alertMsg, context, true);
                return;
            }
            useItemMsg = context.getString(R.string.useItemConfirmationMsg, selectedItem);
            confirmPopUp = new ConfirmPopUp(useItemMsg, context, true);
        }
    }

    private void showSelectItemAlertMsg() {
        String alertMsg = context.getString(R.string.selectItemAlert);
        alertPopUp = new AlertPopUp(alertMsg, context, true);
    }

    private void updateSkillListVisuals() {
        ArrayList<String> skillArrayList = presenter.getSkillNamesArray();
        skillRadioBtnList.updateRadioBtnList(skillArrayList);
    }

    private void updateItemListVisuals() {
        ArrayList<String> itemArrayList = presenter.getPlayerItemNamesArray();
        itemRadioBtnList.updateRadioBtnList(itemArrayList);
    }

    public void draw(Canvas canvas, Paint paint){
        if (isClosed) {
            return;
        }

        drawOverlay(canvas, paint);
        drawMenuItems(canvas, paint);
        skillRadioBtnList.draw(canvas, paint);
        itemRadioBtnList.draw(canvas, paint);
        skillInfo.draw(canvas, paint);
        forgetSkill.draw(canvas, paint);
        itemInfo.draw(canvas, paint);
        sellItem.draw(canvas, paint);
        useItem.draw(canvas, paint);
        drawPopUps(canvas, paint);
    }

    public String getSelectedSkill() {
        return selectedSkill;
    }
}
