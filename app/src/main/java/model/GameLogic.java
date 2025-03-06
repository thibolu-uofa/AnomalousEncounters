package model;

import android.view.MotionEvent;

public class GameLogic {
    public boolean isInHitbox(int eventX, int eventY, int leftX, int rightX, int topY, int bottomY){
        //switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // User has touched the screen MotionEvent motionEvent
        if (eventX >= leftX && eventX <= rightX && eventY >= bottomY && eventY >= topY){
            return true;
        }
            return false;
    }
}


