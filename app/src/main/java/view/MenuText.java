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
    private int height;
    private StaticLayout textStaticLayout;
    private Context context;
    private boolean hasAutoPadding;
    private int x;
    private int y;

    public MenuText(String text, int fontSize, int color, int width, boolean isCentre, Context context, boolean hasAutoPadding){
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.width = width;
        this.isCentre = isCentre;
        this.context = context;
        this.hasAutoPadding = hasAutoPadding;

        createTextStaticLayout();
    }

    public void createTextStaticLayout() {
        // sets the attributes of the text to draw
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(fontSize * HEADING_DIMENSION);
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

        if (!hasAutoPadding) {
            xTextPadding = yTextPadding = 0;
        }

        textStaticLayout = new StaticLayout(text, textPaint, width, textLayout, 1.0f, 0, false);
        height = textStaticLayout.getHeight();
    }
    /**
     * The implementation of this function has code adapted from:
     * Source: <a href="https://stackoverflow.com/questions/2655402/android-canvas-drawtext">...</a>
     */
    public void draw(Canvas canvas, int x, int y){
        canvas.save();
        canvas.translate(x + xTextPadding, y + yTextPadding);
        textStaticLayout.draw(canvas);
        canvas.restore();

        this.x = x;
        this.y = y;
    }

    public void updateText(String text) {
        this.text = text;
        createTextStaticLayout();
    }

    public int getActualTextWidth() {
        if (textStaticLayout == null) {
            return 0;
        }

        int lineCount = textStaticLayout.getLineCount();
        float maxWidth = 0;

        for (int i = 0; i < lineCount; i++) {
            float lineWidth = textStaticLayout.getLineWidth(i);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }

        return (int) maxWidth;
    }

    public int getHeight() {
        if (textStaticLayout == null) {
            return 0;
        }

        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
