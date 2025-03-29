package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class BackgroundImage {
    private final Bitmap skyBitmap;
    private final Bitmap groundBitmap;
    private int groundX1;
    private int groundX2;
    int groundSpeed = 150; // moving speed in pixels per second
    private int skyX1;
    private int skyX2;
    private int y;
    private int direction = 0;

    BackgroundImage(Bitmap skyBitmap, Bitmap groundBitmap, int x, int y) {
        this.skyBitmap = skyBitmap;
        this.skyX1 = 0;
        this.skyX2 = skyBitmap.getWidth();

        this.groundBitmap = groundBitmap;
        this.groundX1 = 0;
        this.groundX2 = groundBitmap.getWidth();
        this.y = y;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(skyBitmap, skyX1, y, paint);
        canvas.drawBitmap(skyBitmap, skyX2, y, paint);
        canvas.drawBitmap(groundBitmap, groundX1, y, paint);
        canvas.drawBitmap(groundBitmap, groundX2, y, paint);
    }

    /**
     * Updates the ground position for an endless scroll effect.
     *
     * @param fps Current frames per second for speed calculation
     * @param canPlayerMove Boolean flag determining if the background is allowed to move
     */
    public void update(long fps, boolean canPlayerMove) {
        if (!canPlayerMove) {
            return;
        }
        // Default to 18fps if fps value is not set properly
        fps = (fps == 0) ? 18 : fps;

        updateSky(fps);
        updateGround(fps);
    }


    private void updateSky(long fps) {
        // update x-pos of both sky images, but make the sky move relative to the ground
        int skySpeed = 30;
        int skyDirection = -1;
        boolean isGroundMoving = direction != 0;

        if (isGroundMoving) {
//            if (direction == -1) {
//                skySpeed += groundSpeed;
//            } else {
//                skySpeed -= groundSpeed + skySpeed + skySpeed;
//            }
            skyDirection = direction;
            skySpeed += groundSpeed;
            int deltaX = (skySpeed * skyDirection) / (int) fps;
            updateSkyPositions(deltaX);
        } else {
            int deltaX = (skySpeed * skyDirection) / (int) fps;
            updateSkyPositions(deltaX);
        }

        // makes sure that sky images wrap around when needed
        wrapSkyImages();
    }


    private void updateSkyPositions(int deltaX) {
        skyX1 += deltaX;
        skyX2 += deltaX;
    }

    private void wrapSkyImages() {
        int skyWidth = skyBitmap.getWidth();

        // check if the sky has scrolled off the canvas, if so reset the x coordinate
        if (skyX1 <= -skyWidth) {
            skyX1 = skyX2 + skyWidth;
        }
        if (skyX2 <= -skyWidth) {
            skyX2 = skyX1 + skyWidth;
        }
        if (skyX1 >= skyWidth) {
            skyX1 = skyX2 - skyWidth;
        }
        if (skyX2 >= skyWidth) {
            skyX2 = skyX1 - skyWidth;
        }

    }

    private void updateGround(long fps) {
        // when direction is zero, that means the background is not moving, so exit
        if (direction == 0) {
            return;
        }

        // update x-pos of both ground images
        int deltaX = (int) ((groundSpeed * direction) / fps);
        updateGroundPositions(deltaX);

        // makes sure that ground images wrap around when needed
        wrapGroundImages();
    }

    private void updateGroundPositions(int deltaX) {
        groundX1 += deltaX;
        groundX2 += deltaX;
    }

    private void wrapGroundImages() {
        int groundWidth = groundBitmap.getWidth();

        // check if the ground has scrolled off the canvas, if so reset the x coordinate
        if (direction == -1) {
            if (groundX1 <= -groundWidth) {
                groundX1 = groundX2 + groundWidth;
            }
            if (groundX2 <= -groundWidth) {
                groundX2 = groundX1 + groundWidth;
            }
        } else if (direction == 1) {
            if (groundX1 >= groundWidth) {
                groundX1 = groundX2 - groundWidth;
            }
            if (groundX2 >= groundWidth) {
                groundX2 = groundX1 - groundWidth;
            }
        }
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getX() {
        return groundX1;
    }
}