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
    private final ArrayList<Sprite> movementArrows = new ArrayList<>();
    private final Sprite squircle;
    int LEFT_PADDING = 40;

    public MoveBar(int x, int y, int width, Context context){
        super(x, y, width, context);
        int centerOfSideBar = x + (width/2);
        int PADDING = 35;

        Bitmap upArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_blank);
        int upArrowY = y + 150;
        movementArrows.add(new Sprite(upArrowBitmap, centerOfSideBar - (upArrowBitmap.getWidth()/2), upArrowY));

        Bitmap squircleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blank_squircle);
        int squircleY = upArrowY + upArrowBitmap.getHeight() + PADDING;
        squircle = new Sprite(squircleBitmap, centerOfSideBar - (squircleBitmap.getWidth()/2), squircleY);

        Bitmap downArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_blank);
        downArrowBitmap = rotateBitmap(downArrowBitmap, 180);
        int downArrowY = squircleY + squircleBitmap.getHeight() + PADDING;
        movementArrows.add(new Sprite(downArrowBitmap, centerOfSideBar - (downArrowBitmap.getWidth()/2), downArrowY));

        Bitmap rightArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_blank);
        rightArrowBitmap = rotateBitmap(rightArrowBitmap, 90);
        int rightArrowX = centerOfSideBar - (rightArrowBitmap.getWidth()/2) + rightArrowBitmap.getWidth();
        movementArrows.add(new Sprite(rightArrowBitmap, rightArrowX + 10, squircleY - 20));

        Bitmap leftArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_blank);
        leftArrowBitmap = rotateBitmap(leftArrowBitmap, 270);
        int leftArrowX = centerOfSideBar - (leftArrowBitmap.getWidth()/2) - leftArrowBitmap.getWidth();
        movementArrows.add(new Sprite(leftArrowBitmap, leftArrowX - 10, squircleY - 20));

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
        return checkForUserTouchTextButtons(eventX, eventY, presenter);
    }

    public void draw(Canvas canvas, Paint paint){
        squircle.draw(canvas, paint);

        for (Sprite arrow: movementArrows) {
            arrow.draw(canvas, paint);
        }

        drawTextButtons(canvas);
    }

}
