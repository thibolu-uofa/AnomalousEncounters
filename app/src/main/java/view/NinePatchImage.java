package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;

public class NinePatchImage {
    private final NinePatchDrawable ninePatchDrawable;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Rect bounds;

    public NinePatchImage(NinePatchDrawable ninePatchDrawable, int x, int y, int width, int height){
        this.ninePatchDrawable = ninePatchDrawable;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rect(x, y, x + width, y + height);
        ninePatchDrawable.setBounds(bounds);
    }

    public void draw(Canvas canvas){
        ninePatchDrawable.draw(canvas);
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
