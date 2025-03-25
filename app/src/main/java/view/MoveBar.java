package view;

import static view.ViewConstants.DEFAULT_TEXT_COLOR;
import static view.ViewConstants.FONT_SIZE_SMALL;
import static view.ViewConstants.SECONDARY_TEXT_COLOR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.example.anomalousencounters.R;

import java.util.ArrayList;

import presenter.GamePresenter;

public class MoveBar extends BaseMenuBar {
    private final ArrayList<ToggleSprite> movementArrows = new ArrayList<>();
    private ToggleSprite selectedArrow;
    private final Sprite squircle;
    int LEFT_PADDING = 40;

    public MoveBar(int x, int y, int width, Context context){
        super(x, y, width, context);
        int centerOfSideBar = x + (width/2);
        int PADDING = 35;

        Bitmap upArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_blank);
        Bitmap upArrowBitmapSelected = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_fill);
        int upArrowY = y + 150;
        movementArrows.add(new ToggleSprite(upArrowBitmap, upArrowBitmapSelected, centerOfSideBar - (upArrowBitmap.getWidth()/2), upArrowY, "up"));

        Bitmap squircleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blank_squircle);
        int squircleY = upArrowY + upArrowBitmap.getHeight() + PADDING;
        squircle = new Sprite(squircleBitmap, centerOfSideBar - (squircleBitmap.getWidth()/2), squircleY);

        Bitmap downArrowBitmap = rotateBitmap(upArrowBitmap, 180);
        Bitmap downArrowBitmapSelected = rotateBitmap(upArrowBitmapSelected, 180);
        int downArrowY = squircleY + squircleBitmap.getHeight() + PADDING;
        movementArrows.add(new ToggleSprite(downArrowBitmap, downArrowBitmapSelected, centerOfSideBar - (downArrowBitmap.getWidth()/2), downArrowY, "down"));

        Bitmap rightArrowBitmap = rotateBitmap(upArrowBitmap, 90);
        Bitmap rightArrowBitmapSelected = rotateBitmap(upArrowBitmapSelected, 90);
        int rightArrowX = centerOfSideBar - (rightArrowBitmap.getWidth()/2) + rightArrowBitmap.getWidth();
        movementArrows.add(new ToggleSprite(rightArrowBitmap, rightArrowBitmapSelected, rightArrowX + 10, squircleY - 20, "right"));

        Bitmap leftArrowBitmap = rotateBitmap(upArrowBitmap, 270);
        Bitmap leftArrowBitmapSelected = rotateBitmap(upArrowBitmapSelected, 270);
        int leftArrowX = centerOfSideBar - (leftArrowBitmap.getWidth()/2) - leftArrowBitmap.getWidth();
        movementArrows.add(new ToggleSprite(leftArrowBitmap, leftArrowBitmapSelected, leftArrowX - 10, squircleY - 20, "left"));

        addNavigationButtons(context);
        setTextPositions();
    }

    //https://stackoverflow.com/questions/29982528/how-do-i-rotate-a-bitmap-in-android
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void setTextPositions() {
        int TOP_PADDING = 60;
        int PADDING = 25;
        int text_y = movementArrows.get(1).getY() + movementArrows.get(1).getHeight() + TOP_PADDING;
        for (MenuText button: textButtons) {
            button.setXAndY(x + LEFT_PADDING, text_y);
            text_y += PADDING + button.getHeight();
        }
    }

    public String checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        for (ToggleSprite arrow: movementArrows) {
            if (arrow.hasBeenTouched(eventX, eventY, presenter, 1)) {
                arrow.select();
            }
        }

        for (ToggleSprite arrow: movementArrows) {
            if (arrow.getIsSelected()  && arrow != selectedArrow) {
                setSelectedArrow(arrow);
                Log.d("Clicked movement arrow", arrow.getText());
                return arrow.getText();
            }
        }

        return checkForUserTouchTextButtons(eventX, eventY, presenter);
    }

    private void setSelectedArrow(ToggleSprite arrow) {
        if (selectedArrow == null) {
            selectedArrow = arrow;
            return;
        }

        selectedArrow.unSelect();
        selectedArrow = arrow;
    }

    public void resetSelectedArrow() {
        if (selectedArrow == null) {
            return;
        }
        selectedArrow.unSelect();
        selectedArrow = null;
    }

    public void draw(Canvas canvas, Paint paint){
        squircle.draw(canvas, paint);

        for (ToggleSprite arrow: movementArrows) {
            arrow.draw(canvas, paint);
        }

        drawTextButtons(canvas);
    }

    public String getSelectedArrowDirection() {
        return selectedArrow.getText();
    }

}
