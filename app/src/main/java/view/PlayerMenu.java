package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Objects;
import presenter.GamePresenter;

public class PlayerMenu extends BaseMenu{
    private RadioBtnList skillRadioBtnList;
    private RadioBtnList itemRadioBtnList;
    private String selectedItem;
    private String selectedSkill;
    private final int Y = 200;
    private final float LINE_SPACE_MULTIPLIER = 1.2f;
    private final int MENU_HEIGHT = 500;
    private final int MENU_WIDTH = 700;
    private final int COL_WIDTH = 100;
    private final int RADIO_X_PAD = 15;
    private final int RADIO_Y_PAD = 90;

    /**
     * Creates a player menu interface with various menu items.
     * Initializes and positions each component with appropriate dimensions and spacing
     * using relative positioning.
     * @param context The application context required for menu item creation
     */
    public PlayerMenu(GamePresenter presenter, Context context){
        super(presenter, context);
        int x = 100;
        int MARGIN = 50;

        createPlayerCard(new Rectangle(x, Y), context);
        x += PLAYER_CARD_WIDTH + MARGIN;

        createSkillMenu(x);
        x += MENU_WIDTH + COL_WIDTH + MARGIN;

        createItemMenu(x);
    }

    private void createSkillMenu(int x) {
        String SKILL_HEADING = "SKILLS";
        MenuItem skills = new MenuItem(x, Y, MENU_HEIGHT, MENU_WIDTH, SKILL_HEADING, false, context);
        menuItemsList.put("skills", skills);

        ArrayList<String> skillArrayList = presenter.getSkillNamesArray();
        Rectangle skillListRect = new Rectangle(x + RADIO_X_PAD, Y + RADIO_Y_PAD);
        skillRadioBtnList = new RadioBtnList(skillListRect.x, skillListRect.y, context, skillArrayList, MENU_WIDTH, MENU_HEIGHT);

        x += MENU_WIDTH;
        createLevelColumn(x);
    }

    private void createLevelColumn(int x) {
        String LEVEL_HEADING = "LV";

        MenuItem level = new MenuItem(x, Y, MENU_HEIGHT, COL_WIDTH, LEVEL_HEADING, true, context);
        level.setLineSpacingMultiplier(LINE_SPACE_MULTIPLIER);
        menuItemsList.put("skill_levels", level);
    }

    private void createItemMenu(int x) {
        String ITEM_HEADING = "ITEMS";

        MenuItem items = new MenuItem(x, Y, MENU_HEIGHT, MENU_WIDTH, ITEM_HEADING, false, context);
        menuItemsList.put("items", items);

        ArrayList<String> itemArrayList = presenter.getPlayerItemNamesArray();
        Rectangle itemListRect = new Rectangle(x + RADIO_X_PAD, Y + RADIO_Y_PAD);
        itemRadioBtnList = new RadioBtnList(itemListRect.x, itemListRect.y, context, itemArrayList, MENU_WIDTH, MENU_HEIGHT);

        x += items.getWidth();
        createItemAmountColumn(x);
    }

    private void createItemAmountColumn(int x) {
        String ITEM_AMOUNT_HEADING = "#";

        MenuItem amountOfItems = new MenuItem(x, Y, MENU_HEIGHT, COL_WIDTH, ITEM_AMOUNT_HEADING, true, context);
        amountOfItems.setLineSpacingMultiplier(LINE_SPACE_MULTIPLIER);
        menuItemsList.put("item_amounts", amountOfItems);
    }

    public void updateMenuTexts(GamePresenter presenter) {
        Objects.requireNonNull(menuItemsList.get("playerCard")).updateText(presenter.getPlayerNameHealthAndTokens());
        Objects.requireNonNull(menuItemsList.get("skill_levels")).updateText("LV\n" + presenter.getSkillLevelsString());
        Objects.requireNonNull(menuItemsList.get("item_amounts")).updateText("#\n" + presenter.getItemAmounts());
    }

    public String checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        String itemBtnPressed = itemRadioBtnList.checkForBtnPress(eventX, eventY, presenter);
        String skillBtnPressed = skillRadioBtnList.checkForBtnPress(eventX, eventY, presenter);
        if (!Objects.equals(itemBtnPressed, "")) {
            selectedItem = itemBtnPressed;
            return itemBtnPressed;
        } else if (!Objects.equals(skillBtnPressed, "")) {
            selectedSkill = skillBtnPressed;
            return skillBtnPressed;
        }

        return "";
    }

    public void draw(Canvas canvas, Paint paint){
        drawOverlay(canvas, paint);
        drawMenuItems(canvas, paint);
        skillRadioBtnList.draw(canvas, paint);
        itemRadioBtnList.draw(canvas, paint);
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
