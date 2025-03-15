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
    private int xTextPadding = 15;
    private int yTextPadding = 15;
    private final int HEADING_DIMENSION = 3;
    private final boolean isCentre;

    public MenuText(String text, int fontSize, int color, int width, boolean isCentre){
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.width= width;
        this.isCentre = isCentre;
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

        Layout.Alignment textLayout = Layout.Alignment.ALIGN_NORMAL;
        if (isCentre) {
            textLayout = Layout.Alignment.ALIGN_CENTER;
            xTextPadding = 0;
        }

        StaticLayout staticLayout = new StaticLayout(text, textPaint, width, textLayout, 1.0f, 0, false);

        canvas.save();
        canvas.translate(x + xTextPadding, y + yTextPadding);
        staticLayout.draw(canvas);
        canvas.restore();
    }

}
