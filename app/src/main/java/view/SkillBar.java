package view;

import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_SMALL;
import static view.ViewConstants.SECONDARY_TEXT_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

import presenter.GamePresenter;

public class SkillBar {
    private final int x;
    private final int y;
    private final MenuText title;
    private final ArrayList<MenuText> buttons = new ArrayList<>();
    private final RadioBtnList radioBtnList;

    public SkillBar(int x, int y, int width, int height, GamePresenter presenter, Context context) {
        this.x = x;
        this.y = y;

        title = new MenuText("Skills & Cooldowns", FONT_SIZE_SMALL, DEFAULT_TEXT_COLOR, width, true, context, true);

        MenuText confirmBtn = new MenuText("[Confirm]", FONT_SIZE_SMALL, DEFAULT_TEXT_COLOR, width, false, context, true);
        buttons.add(confirmBtn);

        MenuText goBackBtn = new MenuText("[Go Back]", FONT_SIZE_SMALL, SECONDARY_TEXT_COLOR, width, false, context, true);
        buttons.add(goBackBtn);

        ArrayList<String> skillArrayList = presenter.getSkillNamesArray();
        radioBtnList = new RadioBtnList(x + 40, y + 120, context, skillArrayList, width, height);
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        radioBtnList.checkForBtnPress(eventX, eventY, presenter);

        for (MenuText btn: buttons) {
            int rightX = btn.getX() + btn.getActualTextWidth();
            int topY = btn.getY() + btn.getHeight();
            boolean hasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, btn.getX(), rightX, topY, btn.getY());
            if (hasBeenPressed) {
                Log.d("Pressed a button >.<", "You pressed a button");
            }
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        int titleTopPadding = 35;
        title.draw(canvas, x, y + titleTopPadding);

        radioBtnList.draw(canvas, paint);

        int padding = 70;
        int y_pos = radioBtnList.getTopY() + padding;
        for (MenuText btn: buttons) {
            btn.draw(canvas, x, y_pos);
            y_pos += padding;
        }
    }
}
