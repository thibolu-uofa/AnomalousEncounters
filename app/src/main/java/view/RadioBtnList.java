package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

import presenter.GamePresenter;

public class RadioBtnList {
    private final int numberOfBoxes;
    private final ArrayList<RadioBtn> radioBtns = new ArrayList<>();
    private RadioBtn checkedBtn;
    private final ArrayList<String> btnTexts;
    private final int height;
    private final int y;

    public RadioBtnList(int x, int y, Context context, ArrayList<String> stringArrayList, int width, int height) {
        this.height = height;
        this.y = y;
        this.btnTexts = stringArrayList;
        this.numberOfBoxes = btnTexts.size();

        int PADDING = 35;
        int localY = y;

        for (int i = 0; i < numberOfBoxes; i++) {
            RadioBtn radioBtn = new RadioBtn(x, localY, stringArrayList.get(i), width, context);
            radioBtns.add(radioBtn);
            localY += PADDING + radioBtn.getHeight();
        }
    }

    public void checkForBtnPress(float eventX, float eventY, GamePresenter presenter) {
        for (RadioBtn radioBtn: radioBtns) {
            radioBtn.checkForBtnPress(eventX, eventY, presenter);
        }

        for (RadioBtn radioBtn: radioBtns) {
            if (radioBtn.getIsChecked()  && radioBtn != checkedBtn) {
                setCheckedBtn(radioBtn);
            }
        }
    }

    private void setCheckedBtn(RadioBtn radioBtn) {
        if (checkedBtn == null) {
            checkedBtn = radioBtn;
            return;
        }

        checkedBtn.unCheckBtn();
        checkedBtn = radioBtn;
    }

    public void draw(Canvas canvas, Paint paint) {
        for (RadioBtn radioBtn: radioBtns) {
            radioBtn.draw(canvas, paint);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getTopY() {
        RadioBtn lastRadioBtn = radioBtns.get(radioBtns.size() - 1);
        return lastRadioBtn.getY() + lastRadioBtn.getHeight();
    }

    public int getY() {
        return y;
    }
}
