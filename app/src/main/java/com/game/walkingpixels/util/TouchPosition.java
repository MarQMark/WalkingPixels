package com.game.walkingpixels.util;

import com.game.walkingpixels.util.vector.Vector2;

import java.util.LinkedList;
import java.util.Queue;

public class TouchPosition {
    private static final Queue<Vector2> positions = new LinkedList<>();
    public static Vector2 lastPosition = new Vector2(-1.0f, -1.0f);

    public static void addPosition(Vector2 position){
        positions.add(position);
        if(positions.size() > 10)
            positions.remove();
    }
    public static Vector2 getPosition(){
        return positions.remove();
    }
    public static int getPositionsSize(){
        return positions.size();
    }
}
