package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Sprite {
    public final Bitmap imageResource;
    private int x;
    private int y;

    public Sprite(Bitmap imageResource, int x, int y){
        this.imageResource = imageResource;
        this.x = x;
        this.y = y;
    }
    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(imageResource, x, y, paint);

    } // end draw
    public int getX() {
        return x;
    }//end getX

    public int getY() {
        return y;
    }//end getY

    public void setX(int x) {
        this.x = x;
    }//end setX

    public void setY(int y) {
        this.y = y;
    } //end setY
}
