package model;

public class GameLogic {

    /**
     * Determines if a point (eventX, eventY) is within a rectangular hitbox.
     *
     * @param eventX   The x-coordinate of the event
     * @param eventY   The y-coordinate of the event
     * @param leftX    The left boundary of the hitbox
     * @param rightX   The right boundary of the hitbox
     * @param topY     The top boundary of the hitbox
     * @param bottomY  The bottom boundary of the hitbox
     * @return true if the event is within the box, false otherwise
     */
    public boolean isInHitbox(int eventX, int eventY, int leftX, int rightX, int topY, int bottomY){
        // Check if eventX is within the horizontal bounds AND eventY is within vertical bounds
        // Y-axis is usually inverted in many graphics systems (0 at top), hence bottomY <= eventY <= topY
        if (eventX >= leftX && eventX <= rightX && eventY >= bottomY && eventY <= topY){
            return true;
        }
        return false;
    }

    /**
     * Determines player's movement state based on where an event occurred in relation to the player.
     *
     * @param eventX    The x-coordinate of the event
     * @param playerX1  The player's left edge (min X)
     * @param playerX2  The player's right edge (max X)
     * @return "Right", "Left", or "Idle"
     */
    public String getPlayerMovementState (int eventX, int playerX1, int playerX2){
        if (eventX >= playerX2 ){
            // If the event is to the right of the player
            return "Right";
        } else if (eventX <= playerX1) {
            // If the event is to the left of the player
            return "Left";
        }
        // Otherwise, the event is within the player's bounds (idle area)
        return "Idle";
    }
}