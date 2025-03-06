package view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
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
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(16);
        textPaint.setColor(color);

        StaticLayout staticLayout = new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);

        canvas.save();
        canvas.translate(1100, 448);
        canvas.restore();

    }

}
