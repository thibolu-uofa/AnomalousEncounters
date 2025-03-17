package view;

import static view.ViewConstants.DEFAULT_FONT_SIZE;
import static view.ViewConstants.DEFAULT_TEXT_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;

public class MenuNinePatch {
    private final NinePatchDrawable ninePatchDrawable;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Rect bounds;
    private MenuText menuText;
    private final int X_BORDER_WEIGHT = 30;
    private final int Y_BORDER_WEIGHT = 40;

    public MenuNinePatch(NinePatchDrawable ninePatchDrawable, int x, int y, String text,  int maxWidth, boolean isCentred, Context context){
        int padding = 100;

        menuText = new MenuText(text, DEFAULT_FONT_SIZE, DEFAULT_TEXT_COLOR, maxWidth, isCentred, context);

        this.ninePatchDrawable = ninePatchDrawable;
        this.x = x;
        this.y = y;
        this.width = menuText.getActualTextWidth() + padding;
        this.height = menuText.getHeight() + padding;
        this.bounds = new Rect(x, y, x + width, y + height);
        ninePatchDrawable.setBounds(bounds);

        Log.d("Text Width and Height", menuText.getActualTextWidth() + " " +  menuText.getHeight());
    }

    public void draw(Canvas canvas, Context context){
        ninePatchDrawable.draw(canvas);
        menuText.draw(canvas, x + X_BORDER_WEIGHT, y + Y_BORDER_WEIGHT);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
