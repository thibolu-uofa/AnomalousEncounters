package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import presenter.GamePresenter;

public class RadioBtnList {
    private final Context context;
    private ArrayList<RadioBtn> radioButtons = new ArrayList<>();
    private RadioBtn checkedBtn;
    private final int height;
    private final int x;
    private final int y;
    private final int width;

    public RadioBtnList(int x, int y, Context context, ArrayList<String> stringArrayList, int width, int height) {
        this.context = context;
        this.height = height;
        this.x = x;
        this.y = y;
        this.width = width;
        int numberOfBoxes = stringArrayList.size();

        int PADDING = 35;
        int localY = y;

        for (int i = 0; i < numberOfBoxes; i++) {
            RadioBtn radioBtn = new RadioBtn(x, localY, stringArrayList.get(i), width, context);
            radioButtons.add(radioBtn);
            localY += PADDING + radioBtn.getHeight();
        }
    }

    public String checkForBtnPressAndCheckBtn(float eventX, float eventY, GamePresenter presenter) {
        for (RadioBtn radioBtn: radioButtons) {
            radioBtn.checkForBtnPressAndCheckBtn(eventX, eventY, presenter);
        }

        for (RadioBtn radioBtn: radioButtons) {
            if (radioBtn.getIsChecked()  && radioBtn != checkedBtn) {
                setCheckedBtn(radioBtn);
                return radioBtn.getText();
            }
        }
        return "";
    }

    public String getPressedButton(float eventX, float eventY, GamePresenter presenter) {
        for (RadioBtn radioBtn: radioButtons) {
            boolean hasBeenPressed = radioBtn.checkForBtnPress(eventX, eventY, presenter);
            if (hasBeenPressed) {
                return radioBtn.getText();
            }
        }
        return "";
    }

    private void setCheckedBtn(RadioBtn radioBtn) {
        if (checkedBtn == null) {
            checkedBtn = radioBtn;
            return;
        }

        checkedBtn.uncheckBtn();
        checkedBtn = radioBtn;
    }

    public void resetCheckedBtn() {
        if (checkedBtn == null) {
            return;
        }
        checkedBtn.uncheckBtn();
        checkedBtn = null;
    }

    public void draw(Canvas canvas, Paint paint) {
        // use a copy to avoid ConcurrentModificationException
        ArrayList<RadioBtn> buttonsCopy = new ArrayList<>(radioButtons);
        for (RadioBtn radioBtn: buttonsCopy) {
            radioBtn.draw(canvas, paint);
        }
    }

    public void updateRadioBtnList(ArrayList<String> stringArrayList) {
        radioButtons = new ArrayList<>();
        int numberOfBoxes = stringArrayList.size();
        int PADDING = 35;
        int localY = y;

        for (int i = 0; i < numberOfBoxes; i++) {
            RadioBtn radioBtn = new RadioBtn(x, localY, stringArrayList.get(i), width, context);
            radioButtons.add(radioBtn);
            localY += PADDING + radioBtn.getHeight();
        }

        resetCheckedBtn();
    }

    public int getHeight() {
        return height;
    }

    public int getTopY() {
        RadioBtn lastRadioBtn = radioButtons.get(radioButtons.size() - 1);
        return lastRadioBtn.getY() + lastRadioBtn.getHeight();
    }

    public int getBottomY() {
        RadioBtn firstRadioBtn = radioButtons.get(0);
        return firstRadioBtn.getY();
    }

    public int getActualWidth() {
        RadioBtn firstRadioBtn = radioButtons.get(0);
        int maxWidth = firstRadioBtn.getWidth();
        for (RadioBtn radioBtn: radioButtons) {
            if (radioBtn.getWidth() > maxWidth) {
                maxWidth = radioBtn.getWidth();
            }
        }
        return maxWidth;
    }

    public int getY() {
        return y;
    }
}
