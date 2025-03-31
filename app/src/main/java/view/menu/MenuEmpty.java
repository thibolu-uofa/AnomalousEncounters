package view.menu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class MenuEmpty {
    private final int x;
    private final int y;
    private final int height;
    private final int width;
    private int color;


    public MenuEmpty(int x, int y, int height, int width, int color){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.color = color;
    }

    public void draw(Canvas canvas, Paint paint){
        int currentColor = paint.getColor();
        paint.setColor(color);
        Rect borderRect = new Rect(x, y , x + width, y + height);
        canvas.drawRect(borderRect, paint);
        paint.setColor(currentColor);
    }

    public void setColor(int color){
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
