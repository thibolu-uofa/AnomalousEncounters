package view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class MenuText {
    private String text;
    private int fontSize;
    private int color;
    private int width;
    private int textPadding = 15;

    public MenuText(String text, int fontSize, int color, int width){
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.width= width;
    }
    public void draw(Canvas canvas, int x, int y, Context context){
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(16 * context.getResources().getDisplayMetrics().density);
        textPaint.setColor(color);

        StaticLayout staticLayout = new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);

        canvas.save();
        canvas.translate(x + textPadding, y + textPadding);
        staticLayout.draw(canvas);
        canvas.restore();
    }

}
