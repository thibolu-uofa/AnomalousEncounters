package view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlayerSprite extends Sprite {
    private static final Map<String, List<int[]>> ANIMATIONS = new LinkedHashMap<>() {{
        put("walk_right", Arrays.asList(new int[]{0,0}, new int[]{0,1}, new int[]{0,2},
                new int[]{0,3}, new int[]{0,4}, new int[]{0,5}));
        put("walk_left", Arrays.asList(new int[]{1,0}, new int[]{1,1}, new int[]{1,2},
                new int[]{1,3}, new int[]{1,4}, new int[]{1,5}));
        put("idle_right", Arrays.asList(new int[]{2,0}, new int[]{2,1}));
        put("idle_left", Arrays.asList(new int[]{3,0}, new int[]{3,1}));
    }};

    private List[] frameRectangles;
    private int spriteWidth;
    private int spriteHeight;
    private int currentAnimation = 0; // row of the current animation
    private int currentAnimationFrame = 0;
    private long lastFrameTime;
    private int fps = 8; // animation speed in frames per second

    private String lastDirection = "walk_right";

    public PlayerSprite(Bitmap imageResource, int x, int y) {
        super(imageResource, x, y);
        setupFrameRectangles();
    }

    private void setupFrameRectangles() {
        // calculate frame dimensions, because player image gets scaled by android studio
        int columns = 6;
        int rows = 4;
        spriteWidth = imageResource.getWidth() / columns;
        spriteHeight = imageResource.getHeight() / rows;

        // initialize frame rectangles for each animation
        frameRectangles = new List[ANIMATIONS.size()];
        int animationIndex = 0;

        // make a rectangle for each frame for each animation in the animations sprite sheet
        for (String animationName : ANIMATIONS.keySet()) {
            List<Rect> rects = new ArrayList<>(Objects.requireNonNull(ANIMATIONS.get(animationName)).size());
            for (int[] coord : Objects.requireNonNull(ANIMATIONS.get(animationName))) {
                int row = coord[0];
                int col = coord[1];
                int x = col * spriteWidth;
                int y = row * spriteHeight;
                rects.add(new Rect(x, y, x + spriteWidth, y + spriteHeight));
            }
            frameRectangles[animationIndex++] = rects;
        }
    }


    /**
     * Updates the current frame of animation based on elapsed time.
     *
     * @param currentTime System time in milliseconds for frame timing
     * @param canMove Flag determining if the player should be animated
     */
    public void update(long currentTime, boolean canMove) {
        if (!canMove) {
            return;
        }

        // initialize frame timing on first update
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
            return;
        }

        // go to next frame if enough time has passed (based on fps)
        long deltaTime = currentTime - lastFrameTime;
        if (deltaTime >= (1000 / fps)) {
            currentAnimationFrame = (currentAnimationFrame + 1) % frameRectangles[currentAnimation].size();
            lastFrameTime = currentTime;
        }
    }

    public void draw(Canvas canvas) {
        // the srcRect is a rectangle that covers which part of the sprite sheet to draw
        Rect srcRect = (Rect) frameRectangles[currentAnimation].get(currentAnimationFrame);

        // the destRect is a rectangle that covers where to draw the player sprite
        RectF destRect = new RectF(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);

        canvas.drawBitmap(imageResource, srcRect, destRect, null);
    }

    public void setAnimation(String animationName) {
        int newAnimationIndex = -1; // -1 means no animation is set
        int animationIndex = 0;

        //sets idle appropriately based on last direction
        if (Objects.equals(animationName, "idle")) {
            if (Objects.equals(lastDirection, "walk_left")){
                animationName = "idle_left";
            } else {
                animationName = "idle_right";
            }
        } else {
            lastDirection = animationName;
        }

        //find the index of requested animation
        for (String animation : ANIMATIONS.keySet()) {
            if (animation.equals(animationName)) {
                newAnimationIndex = animationIndex;
                break;
            }
            animationIndex++;
        }

        //if animation exists and is different from current one, update animation state
        if (newAnimationIndex != -1 && newAnimationIndex != currentAnimation) {
            currentAnimation = newAnimationIndex;
            currentAnimationFrame = 0;
            lastFrameTime = 0;
        }
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }
}
