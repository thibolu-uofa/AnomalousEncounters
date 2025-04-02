package view;

import static model.Utils.getEnemyImage;
import static view.ViewConstants.ENTITY_ICON_BG_COLOR;
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

import presenter.GamePresenter;
import view.menu.BaseMenu;
import view.menu.MenuEmpty;
import view.menu.MenuItem;

public class IndexMenu extends BaseMenu {
    private final ArrayList<MenuEmpty> iconBackgrounds = new ArrayList<>();
    private final Map<Integer, Sprite> entityIdsAndIcons = new HashMap<>();
    private final int Y = 150;
    private final int MAX_COL = 3;
    private final int MAX_ROW = 4;
    private final int OUTER_PADDING = 30;
    private final int INNER_PADDING = 15;
    private final int MENU_HEIGHT;
    private final int ICON_MENU_WIDTH;
    private final int INFO_MENU_WIDTH = 750;
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
        MENU_HEIGHT = (iconHeight * MAX_ROW) + (OUTER_PADDING * 2) + (INNER_PADDING * (MAX_ROW - 1));

        int totalWidth =  ICON_MENU_WIDTH + INFO_MENU_WIDTH;
        int x = SCREEN_WIDTH/2 - totalWidth/2;


        createIconMenu(x);
        x += ICON_MENU_WIDTH;

        createInfoMenu(x);

    }

    private void createIconMenu(int x) {
        MenuItem iconMenu = new MenuItem(x, Y, MENU_HEIGHT, ICON_MENU_WIDTH, BLANK_TEXT, true, context);
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
        MenuItem infoMenu = new MenuItem(x, Y, MENU_HEIGHT, INFO_MENU_WIDTH, BLANK_TEXT, true, context);
        menuItemsList.put("info_menu", infoMenu);
    }

    public void draw(Canvas canvas, Paint paint){
//        if (isClosed) {
//            return;
//        }

        drawOverlay(canvas, paint);
        drawMenuItems(canvas, paint);
        // draw icons and descriptions ect.
        for (MenuEmpty iconBg: iconBackgrounds) {
            iconBg.draw(canvas, paint);
        }
        for (Sprite icon: entityIdsAndIcons.values()) {
            icon.draw(canvas, paint);
        }
        drawPopUps(canvas, paint);
    }
}
