package model;

public class GameLogic {
    public boolean isInHitbox(int eventX, int eventY, int leftX, int rightX, int topY, int bottomY){
        if (eventX >= leftX && eventX <= rightX && eventY >= bottomY && eventY <= topY){
            return true;
        }
        return false;
    }
    public String getPlayerMovementState (int eventX, int playerX1, int playerX2){
        if (eventX >= playerX2 ){
            return "Right";
        } else if (eventX<= playerX1) {
            return "Left";
        }
        return "Idle";
    }
}


