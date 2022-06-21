package com.game.walkingpixels.util.meshbuilder;

import com.game.walkingpixels.model.Camera;
import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.model.atlas.AnimationTextureAtlas;
import com.game.walkingpixels.model.atlas.GridTextureAtlas;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.util.vector.Vector3;

import java.util.ArrayList;

public class MobMeshBuilder extends MeshBuilder{

    public MobMeshBuilder(){
        registerGridTextureAtlas("blocks", new GridTextureAtlas(128, 64, 32));
        registerAnimationTextureAtlas("mob", new AnimationTextureAtlas());
        animationTextureAtlas("mob").addAnimation(64, 128, 4);
        animationTextureAtlas("mob").addAnimation(64, 128, 4);
        animationTextureAtlas("mob").addAnimation(32, 39, 13);
        animationTextureAtlas("mob").addAnimation(32, 39, 13);
        animationTextureAtlas("mob").addAnimation(32, 39, 13);
        animationTextureAtlas("mob").addAnimation(32, 32, 4);
        animationTextureAtlas("mob").addAnimation(32, 32, 4);
        animationTextureAtlas("mob").addAnimation(32, 32, 4);
        animationTextureAtlas("mob").addAnimation(32, 32, 4);
        animationTextureAtlas("mob").addAnimation(32, 32, 6);
    }

    public WorldVertex[] generateMesh(World world, Camera camera, boolean adjust){
        ArrayList<WorldVertex> mobs = new ArrayList<>();

        //Add "Blocks"
        for(int x = 0; x < world.getBlockGridSize(); x ++){
            for(int y = 0; y < world.getBlockGridSize(); y ++) {
                for (int z = 0; z < world.getWorldMaxHeight(); z++) {
                    if(world.getBlockGrid()[x][y][z].ordinal() > Block.AIR.ordinal()){
                        getMobVertices(mobs, new Vector3(x, z, y).sub(new Vector3(world.getBlockGridSize() / 2.0f, 0, world.getBlockGridSize() / 2.0f)), world.getBlockGrid()[x][y][z], camera, adjust);
                        if(world.getBlockGrid()[x][y][z].ordinal() > Block.TREE.ordinal())
                            getShadowVertices(mobs, new Vector3(x, z, y).sub(new Vector3(world.getBlockGridSize() / 2.0f, 0, world.getBlockGridSize() / 2.0f)), world.heightToBlock(z - 1));
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

                        try {
                            /*
                                This causes java.lang.NullPointerException: Attempt to invoke virtual method 'com.game.walkingpixels.model.Block com.game.walkingpixels.model.Enemy.getType()' on a null object reference.
                                I don't know why and I can't fix it. Too bad!
                            */
                            getMobVertices(mobs,new Vector3(mobX, height, mobY).sub(new Vector3(world.getBlockGridSize() / 2.0f, 0, world.getBlockGridSize() / 2.0f)), world.getEnemyGrid()[x][y].getType(), camera, adjust);
                            getShadowVertices(mobs, new Vector3(mobX, height, mobY).sub(new Vector3(world.getBlockGridSize() / 2.0f, 0, world.getBlockGridSize() / 2.0f)), world.heightToBlock(height - 1));
                        }catch (Exception ignored){}
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

    private void getShadowVertices(ArrayList<WorldVertex> mobs, Vector3 position, Block block){

        int id = 3;
        if(block == Block.SNOW)
            id = 7;

        Vector2[] texture = gridTextureAtlas("blocks").getTextureCoordinates(id);

        mobs.add(new WorldVertex(
                new float[]{ position.x, position.y + 0.0001f, position.z},
                new float[]{ texture[0].x, texture[0].y },
                new float[] {0, 1, 0},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                0));

        mobs.add(new WorldVertex(
                new float[]{ position.x + 1, position.y + 0.0001f, position.z},
                new float[]{ texture[1].x, texture[1].y },
                new float[] {0, 1, 0},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                0));

        mobs.add(new WorldVertex(
                new float[]{ position.x, position.y + 0.0001f, position.z + 1},
                new float[]{ texture[2].x, texture[2].y },
                new float[] {0, 1, 0},
                new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
                0));

        mobs.add(new WorldVertex(
                new float[]{ position.x + 1, position.y + 0.0001f,  position.z + 1 },
                new float[]{ texture[3].x, texture[3].y },
                new float[] {0, 1, 0},
                new float[]{ 1.0f, 0.0f, 0.0f, 0.0f },
                0));
    }

    public void getMobVertices(ArrayList<WorldVertex> mobs, Vector3 position, Block type, Camera camera, boolean adjust){
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
                mobHeight = 2.0f;
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(0, (int) (System.currentTimeMillis() / 300 % animationTextureAtlas("mob").getAnimationNumberOfFrames(0)));
                break;
            case BLUE_SLIME:
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(2, (int) (System.currentTimeMillis() / 100 % animationTextureAtlas("mob").getAnimationNumberOfFrames(2)));
                break;
            case GREEN_SLIME:
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(3, (int) (System.currentTimeMillis() / 100 % animationTextureAtlas("mob").getAnimationNumberOfFrames(3)));
                break;
            case PURPLE_SLIME:
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(4, (int) (System.currentTimeMillis() / 100 % animationTextureAtlas("mob").getAnimationNumberOfFrames(4)));
                break;
            case BAT:
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(5, (int) (System.currentTimeMillis() / 200 % animationTextureAtlas("mob").getAnimationNumberOfFrames(5)));
                break;
            case PLANT:
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(6, (int) (System.currentTimeMillis() / 200 % animationTextureAtlas("mob").getAnimationNumberOfFrames(6)));
                break;
            case GOLEM:
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(7, (int) (System.currentTimeMillis() / 250 % animationTextureAtlas("mob").getAnimationNumberOfFrames(7)));
                break;
            case DANGER_NOODLE:
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(8, (int) (System.currentTimeMillis() / 250 % animationTextureAtlas("mob").getAnimationNumberOfFrames(8)));
                break;
            case EYE:
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(9, (int) (System.currentTimeMillis() / 200 % animationTextureAtlas("mob").getAnimationNumberOfFrames(9)));
                break;
            case TREE:
                mobWidth = 3.0f;
                mobHeight = 5.0f;
                textureSlot = 2.0f;
                break;
            case BONFIRE:
                mobHeight = 2.0f;
                textureCoordinates = animationTextureAtlas("mob").getTextureCoordinates(1, (int) (System.currentTimeMillis() / 300 % animationTextureAtlas("mob").getAnimationNumberOfFrames(1)));
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
