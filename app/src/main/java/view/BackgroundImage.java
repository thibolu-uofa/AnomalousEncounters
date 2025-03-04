package view;

import android.graphics.Bitmap;

public class BackgroundImage extends Sprite{
    private int direction;
    private int speed = 150; //moving speed is 150 pixels per second
    private int fps = 10;

    BackgroundImage(Bitmap imageResource, int x, int y) {
        super(imageResource, x, y);
    }

    public void update() {
        int newX = getX() + (speed * direction / fps);
        setX(newX);
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
