package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class HealthBar {
    private final Bitmap baseBitmap;
    private final Bitmap healthBitmap;
    private final int x;
    private final int y;

    public HealthBar(Bitmap baseBitmap, Bitmap healthBitmap, int x, int y){
        this.baseBitmap = baseBitmap;
        this.healthBitmap = healthBitmap;
        this.x = x;
        this.y = y;
    }

    /**
     * Draws a health bar at the specified position with current health percentage.
     * Also draws a base background bar with a dynamic health overlay.
     * @param canvas The canvas to draw on
     * @param paint The paint object to use for base bitmap drawing
     * @param health The current health as a float between 0.0 and 1.0
     */
    public void draw(Canvas canvas, Paint paint, float health){
        canvas.drawBitmap(baseBitmap, x, y, paint);
        int dynamicWidth = (int) (healthBitmap.getWidth() * health);

        // the clipRect is a rectangle that covers which part of the health bar to draw
        Rect clipRect = new Rect(0, 0, dynamicWidth, healthBitmap.getHeight());

        // the destinationRect is a rectangle that covers where to draw the health bar
        Rect destinationRect = new Rect(x, y, x + dynamicWidth, y + healthBitmap.getHeight());

        canvas.drawBitmap(healthBitmap, clipRect, destinationRect, null);
    }
}
