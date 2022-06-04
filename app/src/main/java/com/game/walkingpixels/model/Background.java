package com.game.walkingpixels.model;

import android.graphics.Bitmap;

import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.BackgroundVertex;
import com.game.walkingpixels.openGL.vertices.IVertex;
import com.game.walkingpixels.openGL.vertices.WorldVertex;

public class Background {

    private static final float TEXTURE_SLOT = 0.0f;
    private final Texture image;
    private final double loopTime;
    private double currentTime = 0;

    private float offset = 0;
    private float deltaX = 0;

    public Background(Texture image, double loopTime){
        this.image = image;
        this.loopTime = loopTime;
    }

    public void update(double dt, int width, int height){
        currentTime += dt;
        if(currentTime >= loopTime)
            currentTime = 0.0;

        offset = 0.5f *  (float) (currentTime / loopTime);

        deltaX = ((width / (float)height) * image.getHeight()) / (float)image.getWidth();
    }

    public IVertex[] getVertices(){
        BackgroundVertex[] vertices = new BackgroundVertex[4];

        vertices[0] = new BackgroundVertex(
                new float[]{ -1.0f, -1.0f, 0.0f },
                new float[]{ 0.0f + offset, 0.0f },
                TEXTURE_SLOT);

        vertices[1] = new BackgroundVertex(
                new float[]{ 1.0f, -1.0f, 0.0f },
                new float[]{ deltaX + offset, 0.0f },
                TEXTURE_SLOT);

        vertices[2] = new BackgroundVertex(
                new float[]{-1.0f, 1.0f, 0.0f },
                new float[]{ 0.0f + offset, 1.0f },
                TEXTURE_SLOT);

        vertices[3] = new BackgroundVertex(
                new float[]{ 1.0f, 1.0f, 0.0f },
                new float[]{ deltaX + offset, 1.0f },
                TEXTURE_SLOT);

        return vertices;
    }

    public void bind(){
        image.bind();
    }
}
