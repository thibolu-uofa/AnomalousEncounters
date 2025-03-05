package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BackgroundImage {
    Bitmap skyBitmap;
    Bitmap groundBitmap;
    private int x;
    private int y;
    private int direction;
    private int speed = 150; //moving speed is 150 pixels per second
    private int fps = 10;

    BackgroundImage(Bitmap skyBitmap,  Bitmap groundBitmap, int x, int y) {
        this.skyBitmap = skyBitmap;
        this.groundBitmap = groundBitmap;
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(skyBitmap, x, y, paint);
        canvas.drawBitmap(groundBitmap, x, y, paint);
    }

    public void update() {
        x += (speed * direction / fps);
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
