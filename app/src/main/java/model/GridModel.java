package model;

public class GridModel {
    private final int columnCount = 8, rowCount = 6;
    private float selectedX = -1, selectedY = -1;

    public int getColumnCount() { return columnCount; }
    public int getRowCount() { return rowCount; }

    // Define the hitbox check method for tile selection
    public boolean updateSelectedCoordinate(int eventX, int eventY, int gridWidth, int gridHeight) {
        int cellWidth = gridWidth / columnCount;
        int cellHeight = gridHeight / rowCount;

        // Loop over each grid position to see if the event is inside the bounds
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                int leftX = col * cellWidth;
                int rightX = leftX + cellWidth;
                int topY = row * cellHeight;
                int bottomY = topY + cellHeight;

                // Check if the touch is inside the tile's hitbox
                if (isInHitbox(eventX, eventY, leftX, rightX, topY, bottomY)) {
                    selectedX = (leftX + rightX) / 2;  // Center of the selected tile
                    selectedY = (topY + bottomY) / 2;
                    return true;  // A valid coordinate is selected
                }
            }
        }
        return false; // No valid tile selected
    }

    // Check if the touch is inside a tile (hitbox check)
    public boolean isInHitbox(int eventX, int eventY, int leftX, int rightX, int topY, int bottomY) {
        return eventX >= leftX && eventX <= rightX && eventY >= topY && eventY <= bottomY;
    }

    public float getSelectedX() {
        return selectedX;
    }
    public float getSelectedY() {
        return selectedY;
    }
}
