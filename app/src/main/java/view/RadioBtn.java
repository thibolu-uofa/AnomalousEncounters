package view;

import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_SMALL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.anomalousencounters.R;

import presenter.GamePresenter;

public class RadioBtn {
    private final Sprite uncheckedBtn;
    private final Sprite checkedBtn;
    private Sprite currentBtn;
    private final MenuText menuText;
    private final int BTN_RIGHT_PADDING = 25;
    private final int x;
    private final int y;

    public RadioBtn(int x, int y, String text, int textWidth, Context context) {
        this.x = x;
        this.y = y;

        Bitmap uncheckedBtnBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.unchecked_btn);
        uncheckedBtn = new Sprite(uncheckedBtnBitmap, x, y);

        Bitmap checkedBtnBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.checked_btn);
        checkedBtn = new Sprite(checkedBtnBitmap, x, y);

        currentBtn = uncheckedBtn;

        boolean isTextCentred = false;
        menuText = new MenuText(text, FONT_SIZE_SMALL, DEFAULT_TEXT_COLOR, textWidth, isTextCentred, context, false);
        menuText.setXAndY(x + uncheckedBtn.getWidth() + BTN_RIGHT_PADDING, y);
    }

    public void draw(Canvas canvas, Paint paint) {
        currentBtn.draw(canvas, paint);
        menuText.draw(canvas);
    }

    public void checkForBtnPress(float eventX, float eventY, GamePresenter presenter) {
        int rightX = x + BTN_RIGHT_PADDING + uncheckedBtn.getWidth() + menuText.getActualTextWidth();
        int topY = y + menuText.getHeight();
        boolean hasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, x, rightX, topY, y);
        if (hasBeenPressed) {
            checkBtn();
        }
    }

    public void checkBtn() {
        if (currentBtn == checkedBtn) {
            return;
        }
        currentBtn = checkedBtn;
    }

    public void uncheckBtn() {
        if (currentBtn == uncheckedBtn) {
            return;
        }
        currentBtn = uncheckedBtn;
    }

    public boolean getIsChecked() {
        return currentBtn == checkedBtn;
    }

    public int getHeight() {
        return checkedBtn.getHeight();
    }

    public int getY() {
        return y;
    }

    public String getText() {
        return menuText.getText();
    }
}
