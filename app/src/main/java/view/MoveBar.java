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

public class MoveBar {
    private Sprite upArrow;
    private Sprite rightArrow;
    private Sprite downArrow;
    private Sprite leftArrow;
    private Sprite squircle;
    private final ArrayList<MenuText> textButtons = new ArrayList<>();
    private int x;
    private int y;
    private int width;
    int LEFT_PADDING = 40;

    public MoveBar(int x, int y, int width, Context context){
        this.x = x;
        this.y = y;
        this.width = width;
        int centerOfSideBar = x + (width/2);
        int PADDING = 35;

        Bitmap upArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_blank);
        int upArrowY = y + 150;
        upArrow = new Sprite(upArrowBitmap, centerOfSideBar - (upArrowBitmap.getWidth()/2), upArrowY);

        Bitmap squircleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blank_squircle);
        int squircleY = upArrowY + upArrowBitmap.getHeight() + PADDING;
        squircle = new Sprite(squircleBitmap, centerOfSideBar - (squircleBitmap.getWidth()/2), squircleY);

        Bitmap downArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_blank);
        downArrowBitmap = rotateBitmap(downArrowBitmap, 180);
        int downArrowY = squircleY + squircleBitmap.getHeight() + PADDING;
        downArrow = new Sprite(downArrowBitmap, centerOfSideBar - (downArrowBitmap.getWidth()/2), downArrowY);

        Bitmap rightArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_blank);
        rightArrowBitmap = rotateBitmap(rightArrowBitmap, 90);
        int rightArrowX = centerOfSideBar - (rightArrowBitmap.getWidth()/2) + rightArrowBitmap.getWidth();
        rightArrow = new Sprite(rightArrowBitmap, rightArrowX + 10, squircleY - 20);

        Bitmap leftArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow_blank);
        leftArrowBitmap = rotateBitmap(leftArrowBitmap, 270);
        int leftArrowX = centerOfSideBar - (leftArrowBitmap.getWidth()/2) - leftArrowBitmap.getWidth();
        leftArrow = new Sprite(leftArrowBitmap, leftArrowX - 10, squircleY - 20);

        MenuText confirmBtn = new MenuText("[Confirm]", FONT_SIZE_SMALL, DEFAULT_TEXT_COLOR, width, false, context, true);
        textButtons.add(confirmBtn);

        MenuText goBackBtn = new MenuText("[Go Back]", FONT_SIZE_SMALL, SECONDARY_TEXT_COLOR, width, false, context, true);
        textButtons.add(goBackBtn);


    }
    //https://stackoverflow.com/questions/29982528/how-do-i-rotate-a-bitmap-in-android
    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public String checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        for (MenuText btn: textButtons) {
            int rightX = btn.getX() + btn.getActualTextWidth();
            int topY = btn.getY() + btn.getHeight();
            boolean hasBeenPressed = presenter.isInHitbox((int) eventX, (int) eventY, btn.getX(), rightX, topY, btn.getY());
            if (hasBeenPressed) {
                Log.d("Pressed a button >.<", "You pressed a button");
                return btn.getText();
            }
        }
        return "";
    }

    public void draw(Canvas canvas, Paint paint){
        squircle.draw(canvas, paint);
        upArrow.draw(canvas, paint);
        downArrow.draw(canvas, paint);
        rightArrow.draw(canvas, paint);
        leftArrow.draw(canvas, paint);

        int TOP_PADDING = 60;
        int PADDING = 25;
        int text_y = downArrow.getY() + downArrow.getHeight() + TOP_PADDING;
        for(MenuText button: textButtons) {
            button.draw(canvas, x + LEFT_PADDING, text_y);
            text_y += PADDING + button.getHeight();
        }
    }

}
