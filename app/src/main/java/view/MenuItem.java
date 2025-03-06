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
    private MenuText menuText;
    private String text;


    public MenuItem(int x, int y, int height, int width, String text){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.text = text;
    }
    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.WHITE);
        Rect borderRect = new Rect(x - borderWeight, y - borderWeight, (x + width) + borderWeight, (y + height) + borderWeight );
        canvas.drawRect(borderRect, paint);
        paint.setColor(Color.BLACK);
        Rect rectangle = new Rect(x, y, x + width, y + height);
        canvas.drawRect(rectangle, paint);

        MenuText menuText = new MenuText(text, 16, Color.WHITE, width);
    }
}


