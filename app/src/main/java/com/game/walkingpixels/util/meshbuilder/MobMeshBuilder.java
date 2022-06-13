package com.game.walkingpixels.util.meshbuilder;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.model.atlas.AnimationTextureAtlas;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.util.vector.Vector3;

import java.util.ArrayList;

import javax.xml.transform.Source;

public class MobMeshBuilder {

    public static final AnimationTextureAtlas textureAtlas = new AnimationTextureAtlas(400, 832);

    public static WorldVertex[] generateMesh(World world, Camera camera, boolean adjust){
        ArrayList<WorldVertex> mobs = new ArrayList<>();

        //Add "Blocks"
        for(int x = 0; x < world.getBlockGridSize(); x ++){
            for(int y = 0; y < world.getBlockGridSize(); y ++) {
                for (int z = 0; z < world.getWorldMaxHeight(); z++) {

                    if(world.getBlockGrid()[x][y][z].ordinal() > Block.AIR.ordinal()){
                        getMobVertices(mobs, new Vector3(x, z, y).sub(new Vector3(world.getBlockGridSize() / 2.0f, 0, world.getBlockGridSize() / 2.0f)), world.getBlockGrid()[x][y][z], camera, adjust);
                    }


                }
            }
        }

        //Add Enemies
        for(int x = 0; x < world.getEnemyGridSize(); x++){
            for(int y = 0; y <  world.getEnemyGridSize(); y++) {
                if(world.getEnemyGrid()[x][y] != null && world.insideCircle(x - 5, y - 5,  world.getBlockGridSize() / 2.0f)){

                    int mobX = x - 5;
                    int mobY = y - 5;

                    if(mobX > 0 && mobY > 0){
                        int height = 0;
                        for (int z = 0; z < world.getWorldMaxHeight(); z++) {
                            if(world.getBlockGrid()[mobX][mobY][z] == Block.AIR){
                                height = z;
                                break;
                            }
                        }

                        getMobVertices(mobs,new Vector3(mobX, height, mobY).sub(new Vector3(world.getBlockGridSize() / 2.0f, 0, world.getBlockGridSize() / 2.0f)), Block.SLIME, camera, adjust);
                    }
                }
            }
        }

        WorldVertex[] finishedMesh = new WorldVertex[mobs.size()];
        for (int i = 0; i < mobs.size(); i++){
            finishedMesh[i] = mobs.get(i);
        }
        return finishedMesh;
    }

    public static void getMobVertices(ArrayList<WorldVertex> mobs, Vector3 position, Block type, Camera camera, boolean adjust){
        Vector3 center = new Vector3(position.x + 0.5f, position.y, position.z + 0.5f);


        float mobWidth = 1.0f;
        float mobHeight = 1.0f;
        float textureSlot = 1.0f;
        Vector2[] textureCoordinates = new Vector2[]{
                new Vector2(0.0f, 0.0f),
                new Vector2(1.0f, 0.0f),
                new Vector2(0.0f, 1.0f),
                new Vector2(1.0f, 1.0f),
        };
        switch (type){
            case PLAYER:
                mobWidth = 1.0f;
                mobHeight = 2.0f;
                textureSlot = 1.0f;
                textureCoordinates = textureAtlas.getTextureCoordinates(0, 0);
                break;
            case SLIME:
                mobWidth = 1.0f;
                mobHeight = 1.0f;
                textureSlot = 1.0f;
                textureCoordinates = textureAtlas.getTextureCoordinates(1, 0);
                break;
            case TREE:
                mobWidth = 3.0f;
                mobHeight = 5.0f;
                textureSlot = 2.0f;
                break;
            case BONFIRE:
                mobWidth = 1.0f;
                mobHeight = 1.0f;
                textureSlot = 3.0f;
                break;
        }

        float deltaX = (float) Math.cos(Math.toRadians(camera.rotationY)) * (mobWidth / 2.0f);
        float deltaZ = (float) Math.sin(Math.toRadians(camera.rotationY)) * (mobWidth / 2.0f);

        Vector3 normals = new Vector3(-deltaZ, 0.0f, deltaX);
        normals.normalize();

        Vector3 centerTop;
        if (adjust){
            centerTop = new Vector3(normals);
            centerTop.scale((float) (-Math.cos(Math.toRadians(camera.rotationX)) * mobHeight));
            centerTop.y += Math.sin(Math.toRadians(camera.rotationX)) * mobHeight;
            mobHeight = 0.0f;
        }
        else {
            centerTop = new Vector3(0.0f, 0.0f, 0.0f);
        }


        mobs.add(new WorldVertex(
                new float[]{ center.x - deltaX, position.y, center.z - deltaZ },
                new float[]{ textureCoordinates[0].x, textureCoordinates[0].y },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                textureSlot));

        mobs.add(new WorldVertex(
                new float[]{ center.x + deltaX, position.y, center.z + deltaZ },
                new float[]{ textureCoordinates[1].x, textureCoordinates[1].y },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                textureSlot));

        mobs.add(new WorldVertex(
                new float[]{ center.x + centerTop.x - deltaX, position.y + centerTop.y + mobHeight, center.z +  centerTop.z - deltaZ },
                new float[]{ textureCoordinates[2].x, textureCoordinates[2].y },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                textureSlot));

        mobs.add(new WorldVertex(
                new float[]{ center.x + centerTop.x + deltaX, position.y + centerTop.y + mobHeight,  center.z + centerTop.z + deltaZ },
                new float[]{ textureCoordinates[3].x, textureCoordinates[3].y },
                new float[] {normals.x, normals.y, normals.z},
                new float[]{ 1.0f, 0.0f, 0.0f, 0.0f },
                textureSlot));
    }
}
