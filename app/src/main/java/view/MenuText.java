package view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

public class MenuText {
    private String text;
    private int fontSize;
    private int color;
    private int width;

    public MenuText(String text, int fontSize, int color, int width){
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.width= width;
    }
    public void draw(Canvas canvas){
        TextPaint textPaint = new TextPaint();
    }

}
