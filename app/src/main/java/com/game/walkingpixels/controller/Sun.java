package com.game.walkingpixels.controller;

import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

public class Sun {
    private static final float maxHeight = 50.0f;
    private static final Vector4 clearColor = new Vector4(0.4f, 0.6f, 1.0f, 1.0f);

    private final Vector3 position = new Vector3(0.0f, maxHeight, 0.0f);

    public Sun(){
    }

    public Vector3 getPosition(){
        //update in-game time (1 day = 24 min)
        float sunRotation = (float) (System.currentTimeMillis() / 4000.0) % 360;
        position.z = (float) (Math.cos(Math.toRadians(sunRotation)) * maxHeight);
        position.y = (float) (Math.sin(Math.toRadians(sunRotation)) * maxHeight);
        return position;
    }

    public Vector4 getColor(){
        float diffuse = 0.0f;
        Vector4 color = new Vector4(clearColor);
        if(getPosition().y > 0){
            Vector3 nLightPosition = new Vector3(position);
            nLightPosition.normalize();
            diffuse = nLightPosition.dot(new Vector3(0.0f, 1.0f, 0.0f));
            diffuse = Math.max(diffuse, 0.0f);
        }
        color.scale(diffuse + 0.2f);

        return color;
    }
}
