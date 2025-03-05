package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class HealthBar {
    private Bitmap baseBitmap;
    private Bitmap healthBitmap;
    private int x;
    private int y;

    public HealthBar(Bitmap baseBitmap, Bitmap healthBitmap, int x, int y){
        this.baseBitmap = baseBitmap;
        this.healthBitmap = healthBitmap;
        this.x = x;
        this.y = y;
    }
    public void draw(Canvas canvas, Paint paint, float health){
        canvas.drawBitmap(baseBitmap, x, y, paint);
        int dynamicWidth = (int) (healthBitmap.getWidth() * health);
        Log.d("Dynamic Health", String.valueOf(dynamicWidth));

        Rect clipRect = new Rect(0, 0, dynamicWidth, healthBitmap.getHeight());
        Rect destinationRect = new Rect(x, y, x + dynamicWidth, y + healthBitmap.getHeight());
        canvas.drawBitmap(healthBitmap, clipRect, destinationRect, null);
    }
}
