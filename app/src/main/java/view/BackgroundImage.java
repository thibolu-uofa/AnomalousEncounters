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
        this.groundX1 = 0;
        this.groundX2 = groundBitmap.getWidth();
        this.y = y;
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(skyBitmap, skyX, y, paint);

        canvas.drawBitmap(groundBitmap, groundX1, y, paint);
        canvas.drawBitmap(groundBitmap, groundX2, y, paint);
    }

    /**
     * Updates the ground position for an endless scroll effect.
     *
     * @param fps Current frames per second for speed calculation
     * @param canMove Boolean flag determining if the background is allowed to move
     */
    public void update(long fps, boolean canMove) {
        if (!canMove) {
            return;
        }

        // default to 18fps if fps value is not set properly
        if (fps == 0) {fps = 18;}

        // when direction is zero, that means the background is not moving, so exit
        if (direction == 0) {
            return;
        }

        // update x-pos of both ground images
        groundX1 += (speed * direction / (int) fps);
        groundX2 += (speed * direction / (int) fps);

        // check if the sky has scrolled off left of the canvas, if so reset the x coordinate
        if (direction == -1) {
            if (groundX1 <= -groundBitmap.getWidth()) {
                groundX1 = groundX2 + groundBitmap.getWidth();
            }
            if (groundX2 <= -groundBitmap.getWidth()) {
                groundX2 = groundX1 + groundBitmap.getWidth();
            }
        }

        // check if the sky has scrolled off right of the canvas, if so reset the x coordinate
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
        this.direction = direction;
    }

}
