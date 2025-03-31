package view.menu;

import static view.ViewConstants.FONT_SIZE_MEDIUM;
import static view.ViewConstants.DEFAULT_MENU_BACKGROUND_COLOR;
import static view.ViewConstants.DEFAULT_TEXT_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class MenuItem {
    private Context context;
    private int BORDER_WEIGHT = 10;
    private int x;
    private int y;
    private int height;
    private int width;
    private int PADDING = 15;
    private MenuText menuText;
    private String text;
    private boolean isTextCentred;


    public MenuItem(int x, int y, int height, int width, String text, boolean isTextCentred, Context context){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.text = text;
        this.context = context;
        this.isTextCentred = isTextCentred;
        if (isTextCentred) {
            PADDING = 0;
        }
        menuText = new MenuText(text, FONT_SIZE_MEDIUM, DEFAULT_TEXT_COLOR, width - PADDING, isTextCentred, context, true);
        menuText.setXAndY(x, y);
    }


    public void changeFontSize(int fontSize){
        menuText.updateFontSize(fontSize);
    }

    public void changeFontColor(int color) {
        menuText.setColor(color);
    }

    public void updateText(String text){
        menuText.updateText(text);
    }
    public void draw(Canvas canvas, Paint paint){
        // draws a background rectangle that acts as a border
        paint.setColor(Color.WHITE);
        Rect borderRect = new Rect(x - BORDER_WEIGHT, y - BORDER_WEIGHT, (x + width) + BORDER_WEIGHT, (y + height) + BORDER_WEIGHT);
        canvas.drawRect(borderRect, paint);

        //draws the top rectangle where the menu text will be contained
        paint.setColor(DEFAULT_MENU_BACKGROUND_COLOR);
        Rect rectangle = new Rect(x, y, x + width, y + height);
        canvas.drawRect(rectangle, paint);

        menuText.draw(canvas);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        menuText.setLineSpacingMultiplier(lineSpacingMultiplier);
    }

    public int[] getMenuPositionBound() {
        int leftX = x;
        int rightX = x + width;
        int topY = y + height;
        int bottomY = y;
        return new int[]{leftX, rightX, topY, bottomY};
    }

    public String getText() {
        return menuText.getText();
    }
}


