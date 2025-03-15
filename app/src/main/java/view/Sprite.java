package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import presenter.GamePresenter;

public class Sprite {
    public final Bitmap imageResource;
    private int x;
    private int y;
    private int width;
    private int height;

    public Sprite(Bitmap imageResource, int x, int y){
        this.imageResource = imageResource;
        this.width = imageResource.getWidth();
        this.height = imageResource.getHeight();
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


    public boolean hasBeenTouched(float eventX, float eventY, GamePresenter presenter, int divisor) {
        return presenter.isInHitbox((int) eventX, (int) eventY, x, x + width/divisor, y + height, y);
    }

    public void setX(int x) {
        this.x = x;
    }//end setX

    public void setY(int y) {
        this.y = y;
    } //end setY
}
