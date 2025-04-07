package view;

import static model.Utils.getEnemyImage;
import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.ENTITY_ICON_BG_COLOR;
import static view.ViewConstants.FONT_SIZE_MEDIUM;
import static view.ViewConstants.FONT_SIZE_SMALL;
import static view.ViewConstants.SCREEN_WIDTH;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.anomalousencounters.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import presenter.GamePresenter;
import view.menu.BaseMenu;
import view.menu.MenuEmpty;
import view.menu.MenuItem;
import view.menu.MenuText;

public class IndexMenu extends BaseMenu {
    private final ArrayList<MenuEmpty> iconBackgrounds = new ArrayList<>();
    private final Map<Integer, Sprite> entityIdsAndIcons = new HashMap<>();
    private MenuEmpty selectedIconBg;
    private Sprite selectedEntityIcon;
    private MenuText selectedEntityNameAndType;
    private final int Y = 150;
    private final int INITIAL_X;
    private final int MAX_COL = 3;
    private final int MAX_ROW = 5;
    private final int OUTER_PADDING = 30;
    private final int INNER_PADDING = 15;
    private final int ICON_MENU_HEIGHT;
    private final int HEADING_HEIGHT;
    private final int INFO_MENU_HEIGHT;
    private final int ICON_MENU_WIDTH;
    private final int INFO_MENU_WIDTH = 900;
    private final int iconWidth;
    private final int iconHeight;
    private final int NUMBER_OF_ENEMIES;
    private final String BLANK_TEXT = "";

    public IndexMenu(GamePresenter presenter, Context context) {
        super(presenter, context);

        NUMBER_OF_ENEMIES = presenter.getNumberOfEnemies();

        Bitmap entityIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.entity_icon_bg);
        iconWidth = entityIcon.getWidth();
        iconHeight = entityIcon.getHeight();

        ICON_MENU_WIDTH = (iconWidth * MAX_COL) + (OUTER_PADDING * 2) + (INNER_PADDING * (MAX_COL - 1));
        ICON_MENU_HEIGHT = (iconHeight * MAX_ROW) + (OUTER_PADDING * 2) + (INNER_PADDING * (MAX_ROW - 1));
        HEADING_HEIGHT = (OUTER_PADDING * 2) + (iconHeight * 2) + 10;
        INFO_MENU_HEIGHT = ICON_MENU_HEIGHT - HEADING_HEIGHT;

        int totalWidth =  ICON_MENU_WIDTH + INFO_MENU_WIDTH;
        int x = SCREEN_WIDTH/2 - totalWidth/2;
        INITIAL_X = x;


        createIconMenu(x);
        x += ICON_MENU_WIDTH;

        createInfoMenu(x);

    }

    private void createIconMenu(int x) {
        MenuItem iconMenu = new MenuItem(x, Y, ICON_MENU_HEIGHT, ICON_MENU_WIDTH, BLANK_TEXT, true, context);
        menuItemsList.put("icon_menu", iconMenu);

        createIconsAndBases(x);
    }

    private void createIconsAndBases(int x) {
        int y = Y + OUTER_PADDING;
        x += OUTER_PADDING;
        int currentId = 0;

        for (int row = 0; row < MAX_ROW; row ++) {
            for (int col = 0; col < MAX_COL; col++) {
                int iconBgX = x + col * (iconWidth + INNER_PADDING);
                MenuEmpty iconBg = new MenuEmpty(iconBgX, y, iconHeight, iconWidth, ENTITY_ICON_BG_COLOR);
                iconBackgrounds.add(iconBg);

                if (currentId < NUMBER_OF_ENEMIES) {
                    Bitmap entityBitmap = getEnemyImage(currentId, context);
                    Sprite entityIcon = new Sprite(entityBitmap, iconBgX, y);
                    entityIdsAndIcons.put(currentId, entityIcon);
                    currentId++;
                }
            }
            y += iconHeight + INNER_PADDING;
        }
    }

    private void createInfoMenu(int x) {
        String helpMsg = context.getString(R.string.indexHelpMsg);
        MenuItem infoHeaderMenu = new MenuItem(x, Y, HEADING_HEIGHT, INFO_MENU_WIDTH, helpMsg, false, context);
        menuItemsList.put("info_header", infoHeaderMenu);

        int infoY = Y + HEADING_HEIGHT;
        MenuItem infoMenu = new MenuItem(x, infoY, INFO_MENU_HEIGHT, INFO_MENU_WIDTH, BLANK_TEXT, false, context);
        infoMenu.changeFontSize(FONT_SIZE_SMALL);
        menuItemsList.put("info_menu", infoMenu);
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        if (isClosed) {
            return;
        }

        handleEntityIconInteraction(eventX, eventY, presenter);
    }

    private void handleEntityIconInteraction(float eventX, float eventY, GamePresenter presenter) {
        for (int i = 0; i < NUMBER_OF_ENEMIES; i++) {
            Sprite icon = entityIdsAndIcons.get(i);
            assert icon != null;
            boolean hasBeenPressed = icon.hasBeenTouched(eventX, eventY, presenter, 1);
            if (hasBeenPressed) {
                updateEntityHeading(presenter, i);
                updateEntityInfo(presenter, i);
            }
        }
    }

    private void updateEntityHeading(GamePresenter presenter, int id) {
        int iconX = INITIAL_X + ICON_MENU_WIDTH + OUTER_PADDING;
        int iconY = Y + OUTER_PADDING;
        selectedIconBg = new MenuEmpty(iconX, iconY, iconHeight * 2, iconWidth * 2, ENTITY_ICON_BG_COLOR);

        Bitmap originalEntityBitmap = getEnemyImage(id, context);

        Bitmap scaledEntityBitmap = Bitmap.createScaledBitmap(
                originalEntityBitmap,
                originalEntityBitmap.getWidth() * 2,
                originalEntityBitmap.getHeight() * 2,
                false
        );

        selectedEntityIcon = new Sprite(scaledEntityBitmap, iconX, iconY);

        String entityInfo = presenter.getEnityNameAndType(id);
        int infoX = iconX + selectedIconBg.getWidth() + OUTER_PADDING;
        int width = INFO_MENU_WIDTH - (iconHeight * 2) - (OUTER_PADDING * 2);
        selectedEntityNameAndType = new MenuText(entityInfo, FONT_SIZE_SMALL, DEFAULT_TEXT_COLOR, width, false, context, false);
        selectedEntityNameAndType.setXAndY(infoX, iconY);
    }

    private void updateEntityInfo(GamePresenter presenter, int id){
        Objects.requireNonNull(menuItemsList.get("info_header")).updateText(BLANK_TEXT);
        Objects.requireNonNull(menuItemsList.get("info_menu")).updateText(presenter.getEnemyDescription(id));
    }

    public void draw(Canvas canvas, Paint paint){
        if (isClosed) {
            return;
        }

        drawOverlay(canvas, paint);
        drawMenuItems(canvas, paint);
        // draw icons and descriptions ect.
        for (MenuEmpty iconBg: iconBackgrounds) {
            iconBg.draw(canvas, paint);
        }
        for (Sprite icon: entityIdsAndIcons.values()) {
            icon.draw(canvas, paint);
        }
        if (selectedIconBg != null) {
            selectedIconBg.draw(canvas, paint);
        }
        if (selectedEntityIcon != null) {
            selectedEntityIcon.draw(canvas, paint);
        }
        if (selectedEntityNameAndType != null) {
            selectedEntityNameAndType.draw(canvas);
        }
        drawPopUps(canvas, paint);
    }
}
