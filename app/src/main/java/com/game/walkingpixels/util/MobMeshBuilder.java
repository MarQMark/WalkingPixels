package com.game.walkingpixels.util;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Vertex;

import java.util.ArrayList;

public class MobMeshBuilder {

    public static Vertex[] generateMesh(World.Block[][][] renderedWorld, int renderedWorldSize, int worldMaxHeight){
        ArrayList<Vertex> mobs = new ArrayList<>();

        for(int x = 0; x < renderedWorldSize; x ++){
            for(int y = 0; y < renderedWorldSize; y ++) {
                for (int z = 0; z < worldMaxHeight; z++) {

                    if(renderedWorld[x][y][z] == World.Block.PLAYER){
                        getMobVertices(mobs, new Vector3(x, z, y).sub(new Vector3(renderedWorldSize / 2, 0, renderedWorldSize / 2)), renderedWorld[x][y][z]);
                    }

                }
            }
        }

        Vertex[] finishedMesh = new Vertex[mobs.size()];
        for (int i = 0; i < mobs.size(); i++){
            finishedMesh[i] = mobs.get(i);
        }
        return finishedMesh;
    }

    public static void getMobVertices(ArrayList<Vertex> mobs, Vector3 position, World.Block type){
        Vector3 center = new Vector3(position.x + 0.5f, position.y, position.z + 0.5f);


        float mobWidth = 1.0f;
        float textureSlot = 1.0f;
        switch (type){
            case PLAYER:
                mobWidth = 1.0f;
                textureSlot = 1.0f;
                break;
        }

        float deltaX = (float) Math.cos(Math.toRadians(2.0 * Camera.rotationY) * (mobWidth / 2.0f)) / 2.0f;
        float deltaZ = (float) Math.sin(Math.toRadians(2.0 * Camera.rotationY) * (mobWidth / 2.0f)) / 2.0f;

        Vector3 normals = new Vector3(-deltaZ, 0.0f, deltaX);
        normals.normalize();

        mobs.add(new Vertex(
                new float[]{ center.x - deltaX, position.y, center.z - deltaZ },
                new float[]{ 0.0f, 0.0f },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                textureSlot));

        mobs.add(new Vertex(
                new float[]{ center.x + deltaX, position.y, center.z + deltaZ },
                new float[]{ 1.0f, 0.0f },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                textureSlot));

        mobs.add(new Vertex(
                new float[]{ center.x - deltaX, position.y + 2, center.z - deltaZ },
                new float[]{ 0.0f, 1.0f },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                textureSlot));

        mobs.add(new Vertex(
                new float[]{ center.x + deltaX, position.y + 2, center.z + deltaZ },
                new float[]{ 1.0f, 1.0f },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 1.0f, 0.0f, 0.0f, 0.0f },
                textureSlot));
    }
}
