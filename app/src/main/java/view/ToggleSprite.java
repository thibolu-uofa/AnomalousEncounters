package view;

import android.graphics.Bitmap;

public class ToggleSprite extends Sprite {
    Bitmap defaultBitmap;
    Bitmap changedBitmap;
    String text;
    public ToggleSprite(Bitmap imageResource, Bitmap changedBitmap, int x, int y, String text) {
        super(imageResource, x, y);
        this.defaultBitmap = imageResource;
        this.changedBitmap = changedBitmap;
        this.text = text;
    }

    public void toggleSpriteImage() {
        if (imageResource == defaultBitmap) {
            imageResource = changedBitmap;
        } else {
            imageResource = defaultBitmap;
        }
    }

    public void select() {
        imageResource = changedBitmap;
    }


    public void unSelect() {
        imageResource = defaultBitmap;
    }

    public boolean getIsSelected() {
        return imageResource == changedBitmap;
    }

    public String getText() {
        return text;
    }
}
