package view.battle;

import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_SMALL;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Objects;
import presenter.GamePresenter;
import view.RadioBtnList;
import view.menu.MenuText;

public class SkillBar extends BaseMenuBar {
    private final MenuText title;
    private final RadioBtnList radioBtnList;
    private final MenuText skillCooldowns;
    private String selectedSkill = "";

    public SkillBar(int x, int y, int width, int height, ArrayList<String> skillArrayList, String skillCooldownsString, Context context) {
        super(x, y, width, context);

        title = createButton("Skills & Cooldowns", FONT_SIZE_SMALL, DEFAULT_TEXT_COLOR, true, context);
        addNavigationButtons(context);
        radioBtnList = new RadioBtnList(x + 10, y + 155, context, skillArrayList, width, height);
        skillCooldowns = createButton(skillCooldownsString, FONT_SIZE_SMALL, DEFAULT_TEXT_COLOR, false, context);
        skillCooldowns.setLineSpacingMultiplier(1.35f);

        setTextPositions();
    }

    private void setTextPositions() {
        int titleTopPadding = 45;
        title.setXAndY(x, y + titleTopPadding);

        // set positioning for the continue and go back buttons
        int padding = 100;
        int y_pos = radioBtnList.getTopY() + padding;
        for (MenuText btn: textButtons) {
            btn.setXAndY(x, y_pos);
            y_pos += padding;
        }

        //set positioning for skill cooldowns
        int cooldownX = x + radioBtnList.getActualWidth() + 20;
        int cooldownY= radioBtnList.getBottomY() - 10;
        skillCooldowns.setXAndY(cooldownX, cooldownY);
    }

    public String checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        String radioBtnPressed = radioBtnList.getPressedButton(eventX, eventY, presenter);
        if (!Objects.equals(radioBtnPressed, "")) {
            // prevent user from selecting a skill on cooldown
            boolean canPlayerUseSkill = presenter.canPlayerUseSkill(radioBtnPressed);

            if (canPlayerUseSkill) {
                radioBtnList.checkForBtnPressAndCheckBtn(eventX, eventY, presenter);
                selectedSkill = radioBtnPressed;
                return radioBtnPressed;
            }

            resetCheckedBtn();
            return "invalid_skill";
        }

        String textBtnPressed = checkForUserTouchTextButtons(eventX, eventY, presenter);
        if (!Objects.equals(textBtnPressed, "")) {
            //Prevents the user from confirming without selecting a skill
            if (Objects.equals(textBtnPressed, "[Confirm]") && Objects.equals(selectedSkill, "")) {
                return "";
            }
            return textBtnPressed;
        }

        return "";
    }

    public void updateSkillCooldowns(String skillCooldownsString) {
        skillCooldowns.updateText(skillCooldownsString);
    }

    public void draw(Canvas canvas, Paint paint) {
        title.draw(canvas);
        radioBtnList.draw(canvas, paint);
        skillCooldowns.draw(canvas);
        drawTextButtons(canvas);
    }

    public void resetCheckedBtn() {
        radioBtnList.resetCheckedBtn();
        selectedSkill = "";
    }

    public String getSelectedSkill() {
        return selectedSkill;
    }
}
