package com.game.walkingpixels.model;

import android.graphics.Bitmap;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.worldVertex;
import com.game.walkingpixels.util.vector.Vector3;

public class RenderedSpell {

    private final int TEXTURE_SLOT = 3;

    private final Texture texture;
    private final int damage;
    private final double initialTTL;
    private double TTL;

    private boolean isEnabled = true;
    private boolean isFinished = false;

    public RenderedSpell(Bitmap spell, double TTL, int damage){
        this.texture = new Texture(spell, TEXTURE_SLOT);
        this.initialTTL = TTL;
        this.TTL = TTL;
        this.damage = damage;
        bind();
    }

    public void update(double dt){
        TTL -= dt;
        if(TTL < 0.0){
            TTL = 0.0;
            isFinished = true;
        }
    }

    public worldVertex[] getVertices(Vector3 position, Camera camera){
        worldVertex[] vertices = new worldVertex[4];

        Vector3 center = new Vector3(position.x + 0.5f, position.y, position.z + 0.5f);

        float width = 1.0f;
        float height = 1.0f;

        float deltaX = (float) Math.cos(Math.toRadians(2.0 * camera.rotationY) * (width / 2.0f)) / 2.0f;
        float deltaZ = (float) Math.sin(Math.toRadians(2.0 * camera.rotationY) * (width / 2.0f)) / 2.0f;

        Vector3 normals = new Vector3(-deltaZ, 0.0f, deltaX);
        normals.normalize();

        Vector3 distance = new Vector3(normals);
        if(TTL / initialTTL < 0.5)
            distance.scale((float) (TTL / initialTTL));
        center = center.add(distance);

        vertices[0] = new worldVertex(
                new float[]{ center.x - deltaX, position.y, center.z - deltaZ },
                new float[]{ 0.0f, 0.0f },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                TEXTURE_SLOT);

        vertices[1] = new worldVertex(
                new float[]{ center.x + deltaX, position.y, center.z + deltaZ },
                new float[]{ 1.0f, 0.0f },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                TEXTURE_SLOT);

        vertices[2] = new worldVertex(
                new float[]{ center.x - deltaX, position.y + height, center.z - deltaZ },
                new float[]{ 0.0f, 1.0f },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                TEXTURE_SLOT);

        vertices[3] = new worldVertex(
                new float[]{ center.x + deltaX, position.y + height, center.z + deltaZ },
                new float[]{ 1.0f, 1.0f },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 1.0f, 0.0f, 0.0f, 0.0f },
                TEXTURE_SLOT);

        return vertices;
    }

    public boolean isFinished(){
        if(isFinished)
            isEnabled = false;

        return isFinished;
    }

    public boolean isEnabled(){
        return isEnabled;
    }

    public void bind(){
        texture.bind(TEXTURE_SLOT);
    }
    public void unbind(){
        texture.unbind();
    }

    public int getDamage(){
        return damage;
    }
}
