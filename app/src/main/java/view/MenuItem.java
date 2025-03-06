package view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class MenuItem {
    private int borderWeight = 10;
    private int x;
    private int y;
    private int height;
    private int width;

    public MenuItem(int x, int y, int height, int width){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }
    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.WHITE);
        Rect borderRect = new Rect(x - borderWeight, y - borderWeight, (x + width) + borderWeight, (y + height) + borderWeight );
        canvas.drawRect(borderRect, paint);
        paint.setColor(Color.BLACK);
        Rect rectangle = new Rect(x, y, x + width, y + height);
        canvas.drawRect(rectangle, paint);
    }
}


