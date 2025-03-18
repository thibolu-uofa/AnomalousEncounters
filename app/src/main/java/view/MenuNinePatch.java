package view;

import static view.ViewConstants.FONT_SIZE_SMALL;
import static view.ViewConstants.DEFAULT_TEXT_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;

public class MenuNinePatch {
    private final NinePatchDrawable ninePatchDrawable;
    private final int x;
    private final int y;
    private int width;
    private int height;
    private Rect bounds;
    private MenuText menuText;
    private final int X_BORDER_WEIGHT = 30;
    private final int Y_BORDER_WEIGHT = 40;
    private int PADDING = 100;
    private boolean hasText;


    public MenuNinePatch(NinePatchDrawable ninePatchDrawable, int x, int y, String text,  int maxWidth, boolean isCentred, Context context){
        menuText = new MenuText(text, FONT_SIZE_SMALL, DEFAULT_TEXT_COLOR, maxWidth, isCentred, context, true);

        this.ninePatchDrawable = ninePatchDrawable;
        this.x = x;
        this.y = y;
        this.width = menuText.getActualTextWidth() + PADDING;
        this.height = menuText.getHeight() + PADDING;
        this.bounds = new Rect(x, y, x + width, y + height);
        ninePatchDrawable.setBounds(bounds);

//        Log.d("Text Width and Height", menuText.getActualTextWidth() + " " +  menuText.getHeight());
        hasText = true;
    }

    public MenuNinePatch(NinePatchDrawable ninePatchDrawable, int x, int y, int width, int height) {
        this.ninePatchDrawable = ninePatchDrawable;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.bounds = new Rect(x, y, x + width, y + height);
        ninePatchDrawable.setBounds(bounds);

        hasText = false;
    }

    public void updateText(String text){
        if (!hasText) {
            return;
        }
        menuText.updateText(text);
        width = menuText.getActualTextWidth() + PADDING;
        height = menuText.getHeight() + PADDING;
        bounds = new Rect(x, y, x + width, y + height);
        ninePatchDrawable.setBounds(bounds);
    }

    public void draw(Canvas canvas){
        ninePatchDrawable.draw(canvas);

        if (hasText) {
            menuText.draw(canvas, x + X_BORDER_WEIGHT, y + Y_BORDER_WEIGHT);
        }
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
