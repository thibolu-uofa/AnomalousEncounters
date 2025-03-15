package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class PlayerMenu {
    private MenuItem playerCard;
    private MenuItem skills;


    public PlayerMenu(Context context){
        playerCard = new MenuItem(300, 200, 300, 350, "Bobette", context);
        skills = new MenuItem(400, 400, 500, 400, "Skills", context);
        MenuItem level = new MenuItem(400, 400, 500, 400, "LV", context);
        MenuItem items = new MenuItem(400, 400, 500, 400, "Items", context);
        MenuItem amountOfSkills = new MenuItem(400, 400, 500, 400, "#", context);
        MenuItem infoButton = new MenuItem(400, 400, 500, 400, "INFO", context);
    }

    public void update(String playerCardText) {
        playerCard.updateText(playerCardText);
//        skills.updateText("");
    }

    public void draw(Canvas canvas, Paint paint){
        playerCard.draw(canvas, paint);
        skills.draw(canvas, paint);
    }
}
