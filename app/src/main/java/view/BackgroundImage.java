package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BackgroundImage {
    Bitmap skyBitmap;
    Bitmap groundBitmap;
    private int groundX1;
    private int groundX2;

    private int skyX;
    private int y;
    private int direction = 0;
    private int speed = 150; //moving speed in pixels per second

    BackgroundImage(Bitmap skyBitmap,  Bitmap groundBitmap, int x, int y) {
        this.skyBitmap = skyBitmap;
        this.groundBitmap = groundBitmap;
        this.skyX = x;
        this.y = y;
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(skyBitmap, skyX, y, paint);

        canvas.drawBitmap(groundBitmap, groundX1, y, paint);
        canvas.drawBitmap(groundBitmap, groundX2, y, paint);
    }

    public void update(long fps) {
        if (fps == 0) { //default case
            fps = 18;
        }

        if (direction == 0) {
            return;
        }

        groundX1 += (speed * direction / (int) fps);
        groundX2 += (speed * direction / (int) fps);

        // check if the sky has scrolled off the canvas, if so reset the x coordinate
        if (direction == -1) {
            if (groundX1 <= -groundBitmap.getWidth()) {
                groundX1 = groundX2 + groundBitmap.getWidth();
            }
            if (groundX2 <= -groundBitmap.getWidth()) {
                groundX2 = groundX1 + groundBitmap.getWidth();
            }
        }

        if (direction == 1) {
            if (groundX1 >= groundBitmap.getWidth()) {
                groundX1 = groundX2 - groundBitmap.getWidth();
            }
            if (groundX2 >= groundBitmap.getWidth()) {
                groundX2 =  groundX1 - groundBitmap.getWidth();
            }
        }

    }

    public void setDirection(int direction) {
        this.groundX1 = 0;
        switch (direction) {
            case -1:
                this.groundX2 = groundX1 + groundBitmap.getWidth();
                break;
            case 1:
                this.groundX2 = groundX1 - groundBitmap.getWidth();
                break;
        }
        this.direction = direction;
    }
}
