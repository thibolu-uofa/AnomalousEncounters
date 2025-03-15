package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.example.anomalousencounters.R;

public class MenuText {
    private String text;
    private int fontSize;
    private int color;
    private int width;
    private int textPadding = 15;
    private final int HEADING_DIMENSION = 3;

    public MenuText(String text, int fontSize, int color, int width){
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.width= width;
    }

    /**
     * The implementation of this function has code adapted from:
     * Source: <a href="https://stackoverflow.com/questions/2655402/android-canvas-drawtext">...</a>
     */
    public void draw(Canvas canvas, int x, int y, Context context){
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(32 * HEADING_DIMENSION);
//        Log.d("Distort by", String.valueOf(context.getResources().getDisplayMetrics().density));
        textPaint.setColor(color);
        Typeface typeface = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            typeface = context.getResources().getFont(R.font.pixeltype);
        }
        textPaint.setTypeface(typeface);

        StaticLayout staticLayout = new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);

        canvas.save();
        canvas.translate(x + textPadding, y + textPadding);
        staticLayout.draw(canvas);
        canvas.restore();
    }

}
