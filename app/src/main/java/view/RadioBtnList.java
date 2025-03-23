package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

import presenter.GamePresenter;

public class RadioBtnList {
    private final ArrayList<RadioBtn> radioButtons = new ArrayList<>();
    private RadioBtn checkedBtn;
    private final int height;
    private final int y;

    public RadioBtnList(int x, int y, Context context, ArrayList<String> stringArrayList, int width, int height) {
        this.height = height;
        this.y = y;
        int numberOfBoxes = stringArrayList.size();

        int PADDING = 35;
        int localY = y;

        for (int i = 0; i < numberOfBoxes; i++) {
            RadioBtn radioBtn = new RadioBtn(x, localY, stringArrayList.get(i), width, context);
            radioButtons.add(radioBtn);
            localY += PADDING + radioBtn.getHeight();
        }
    }

    public String checkForBtnPress(float eventX, float eventY, GamePresenter presenter) {
        for (RadioBtn radioBtn: radioButtons) {
            radioBtn.checkForBtnPress(eventX, eventY, presenter);
        }

        for (RadioBtn radioBtn: radioButtons) {
            if (radioBtn.getIsChecked()  && radioBtn != checkedBtn) {
                setCheckedBtn(radioBtn);
                Log.d("Clicked radio button", radioBtn.getText());
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

    public void draw(Canvas canvas, Paint paint) {
        for (RadioBtn radioBtn: radioButtons) {
            radioBtn.draw(canvas, paint);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getTopY() {
        RadioBtn lastRadioBtn = radioButtons.get(radioButtons.size() - 1);
        return lastRadioBtn.getY() + lastRadioBtn.getHeight();
    }

    public int getY() {
        return y;
    }
}
