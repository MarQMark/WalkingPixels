package com.game.walkingpixels.util;

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
        float mobWidth = 1;
        float delta = (float) ((mobWidth / 2.0f) / Math.sqrt(2));

        float textureSlot = 1.0f;
        float rotates = 1.0f;

        mobs.add(new Vertex(
                new float[]{ center.x - delta, position.y, center.z - delta },
                new float[]{ 0.0f, 0.0f },
                new float[]{-(float) (1.0f/ Math.sqrt(2.0)), 0.0f, (float) -(1.0f/ Math.sqrt(2.0))},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                textureSlot,
                rotates));

        mobs.add(new Vertex(
                new float[]{ center.x + delta, position.y, center.z + delta },
                new float[]{ 1.0f, 0.0f },
                new float[]{-(float) (1.0f/ Math.sqrt(2.0)), 0.0f, -(float) (1.0f/ Math.sqrt(2.0))},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                textureSlot,
                rotates));

        mobs.add(new Vertex(
                new float[]{ center.x - delta, position.y + 1, center.z - delta },
                new float[]{ 0.0f, 1.0f },
                new float[]{-(float) (1.0f/ Math.sqrt(2.0)), 0.0f, -(float) (1.0f/ Math.sqrt(2.0))},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                textureSlot,
                rotates));

        mobs.add(new Vertex(
                new float[]{ center.x + delta, position.y + 1, center.z + delta },
                new float[]{ 1.0f, 1.0f },
                new float[]{-(float) (1.0f/ Math.sqrt(2.0)), 0.0f,- (float) (1.0f/ Math.sqrt(2.0))},
                new float[]{ 1.0f, 0.0f, 0.0f, 0.0f },
                textureSlot,
                rotates));

        /*mobs.add(new Vertex(
                new float[]{ center.x - 1, position.y, position.z },
                new float[]{ 0.0f, 0.0f },
                new float[]{0.0f, 0.0f, 1.0f},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                1.0f,
                1.0f));

        mobs.add(new Vertex(
                new float[]{ center.x + 1, position.y, position.z },
                new float[]{ 1.0f, 0.0f },
                new float[]{0.0f, 0.0f, 1.0f},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                1.0f,
                1.0f));

        mobs.add(new Vertex(
                new float[]{ center.x - 1, position.y + 1, position.z },
                new float[]{ 0.0f, 1.0f },
                new float[]{0.0f, 0.0f, 1.0f},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                1.0f,
                1.0f));

        mobs.add(new Vertex(
                new float[]{ center.x + 1, position.y + 1, position.z },
                new float[]{ 1.0f, 1.0f },
                new float[]{0.0f, 0.0f, 1.0f},
                new float[]{ 1.0f, 0.0f, 0.0f, 0.0f },
                1.0f,
                1.0f));*/
    }
}
