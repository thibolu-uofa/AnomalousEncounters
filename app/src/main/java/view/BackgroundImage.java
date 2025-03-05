package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BackgroundImage {
    Bitmap skyBitmap;
    Bitmap groundBitmap;
    private int skyX1;
    private int skyX2;

    private int groundX;
    private int y;
    private int direction = 0;
    private int speed = 150; //moving speed in pixels per second

    BackgroundImage(Bitmap skyBitmap,  Bitmap groundBitmap, int x, int y) {
        this.skyBitmap = skyBitmap;
        this.groundBitmap = groundBitmap;
        this.groundX = x;
        this.y = y;
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(skyBitmap, skyX1, y, paint);
        canvas.drawBitmap(skyBitmap, skyX2, y, paint);

        canvas.drawBitmap(groundBitmap, groundX, y, paint);
    }

    public void update(long currentTime, long fps) {
        if (direction != 0) {
            skyX1 += (speed * direction / (int) fps);
            skyX2 += (speed * direction / (int) fps);
        }

        // check if the sky has scrolled off the canvas, if so reset the x coordinate
        if (direction == -1) {
            if (skyX1 <= -skyBitmap.getWidth()) {
                skyX1 = skyX2 + skyBitmap.getWidth();
            }
            if (skyX2 <= -skyBitmap.getWidth()) {
                skyX2 = skyX1 + skyBitmap.getWidth();
            }
        }

        if (direction == 1) {
            if (skyX1 >= skyBitmap.getWidth()) {
                skyX1 = skyX2 - skyBitmap.getWidth();
            }
            if (skyX2 >= skyBitmap.getWidth()) {
                skyX2 =  skyX1 - skyBitmap.getWidth();
            }
        }
    }

    public void setDirection(int direction) {
        this.skyX1 = 0;
        switch (direction) {
            case -1:
                this.skyX2 = skyX1 + skyBitmap.getWidth();
            case 1:
                this.skyX2 = skyX1 - skyBitmap.getWidth();
        }
        this.direction = direction;
    }
}
