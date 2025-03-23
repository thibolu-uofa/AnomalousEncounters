package view;

import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_SMALL;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import java.util.ArrayList;
import java.util.Objects;
import presenter.GamePresenter;

public class SkillBar extends BaseMenuBar {
    private final MenuText title;
    private final RadioBtnList radioBtnList;

    public SkillBar(int x, int y, int width, int height, GamePresenter presenter, Context context) {
        super(x, y, width, context);

        title = createButton("Skills & Cooldowns", FONT_SIZE_SMALL, DEFAULT_TEXT_COLOR, true, context);
        addNavigationButtons(context);

        ArrayList<String> skillArrayList = presenter.getSkillNamesArray();
        radioBtnList = new RadioBtnList(x + 40, y + 120, context, skillArrayList, width, height);

        setTextPositions();
    }

    private void setTextPositions() {
        int titleTopPadding = 35;
        title.setXAndY(x, y + titleTopPadding);

        int padding = 70;
        int y_pos = radioBtnList.getTopY() + padding;
        for (MenuText btn: textButtons) {
            btn.setXAndY(x, y_pos);
            y_pos += padding;
        }
    }

    public String checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        String radioBtnPressed = radioBtnList.checkForBtnPress(eventX, eventY, presenter);
        if (!Objects.equals(radioBtnPressed, "")) {return radioBtnPressed;}

        String textBtnPressed = checkForUserTouchTextButtons(eventX, eventY, presenter);
        if (!Objects.equals(textBtnPressed, "")) {return textBtnPressed;}

        return "";
    }

    public void draw(Canvas canvas, Paint paint) {
        title.draw(canvas);
        radioBtnList.draw(canvas, paint);
        drawTextButtons(canvas);
    }
}
