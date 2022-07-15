package com.game.walkingpixels.util.meshbuilder;

import android.content.Context;

import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.Model3DManager;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.vector.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Model3DBuilder extends MeshBuilder{

    private final Model3DManager modelManager;

    public Model3DBuilder(Context context){
        modelManager = Model3DManager.getInstance(context);
    }

    public WorldVertex[] generateTrees(World world){
        ArrayList<WorldVertex> trees = new ArrayList<>();
        int centerOffset = (int) (world.getBlockGridSize() / 2.0f);

        for(int x = 0; x < world.getBlockGridSize(); x ++){
            for(int y = 0; y < world.getBlockGridSize(); y ++) {
                for (int z = 0; z < world.getWorldMaxHeight(); z++) {
                    if(world.getBlockGrid()[x][y][z] == Block.TREE)
                        trees.addAll(Arrays.asList(modelManager.getModel("tree").getVertices(new Vector3(x - centerOffset, z, y - centerOffset))));
                }
            }
        }

        WorldVertex[] finishedTrees;
        if(trees.size() != 0){
            finishedTrees = new WorldVertex[trees.size()];
            for (int i = 0; i < trees.size(); i++)
                finishedTrees[i] = trees.get(i);
        }else{
            finishedTrees = generateEmptyVertices();
        }

        return finishedTrees;
    }

    public WorldVertex[] generatePlayer(World world, float rotation){
        int animationNumber = getAnimation(400, modelManager.PLAYER_ANIMATIONS) + 1;

        for(int x = 0; x < world.getBlockGridSize(); x ++){
            for(int y = 0; y < world.getBlockGridSize(); y ++) {
                for (int z = 0; z < world.getWorldMaxHeight(); z++) {
                    if(world.getBlockGrid()[x][y][z] == Block.PLAYER)
                        return modelManager.getModel("player" + animationNumber).getVertices(
                                new Vector3(0, z, 0),
                                new Vector3(0, rotation + (float)((System.currentTimeMillis() / 10.0) % 360), 0),
                                new Vector3(0.75f)
                        );
                }
            }
        }

        return generateEmptyVertices();
    }

    public WorldVertex[] generateObelisks(World world){
        ArrayList<WorldVertex> obelisks = new ArrayList<>();
        int centerOffset = (int) (world.getBlockGridSize() / 2.0f);

        for(int x = 0; x < world.getBlockGridSize(); x ++){
            for(int y = 0; y < world.getBlockGridSize(); y ++) {
                for (int z = 0; z < world.getWorldMaxHeight(); z++) {
                    if(world.getBlockGrid()[x][y][z] == Block.BONFIRE)
                        obelisks.addAll(Arrays.asList(
                                modelManager.getModel("obelisk")
                                .getVertices(
                                        new Vector3(x - centerOffset, z, y - centerOffset),
                                        new Vector3(0, (float)((System.currentTimeMillis() / 20.0) % 360), 0)
                                )
                        ));
                }
            }
        }

        WorldVertex[] finishedObelisks;
        if(obelisks.size() != 0){
            finishedObelisks = new WorldVertex[obelisks.size()];
            for (int i = 0; i < obelisks.size(); i++)
                finishedObelisks[i] = obelisks.get(i);
        }else{
            finishedObelisks = generateEmptyVertices();
        }

        return finishedObelisks;
    }

    public WorldVertex[] generateMobs(World world){
        ArrayList<WorldVertex> mobs = new ArrayList<>();
        int centerOffset = (int) (world.getBlockGridSize() / 2.0f);

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

                        /*
                                This causes java.lang.NullPointerException: Attempt to invoke virtual method 'com.game.walkingpixels.model.Block com.game.walkingpixels.model.Enemy.getType()' on a null object reference.
                                I don't know why and I can't fix it. Too bad!
                        */
                        try {
                            getMobVertices(mobs,
                                    new Vector3(mobX, height, mobY).sub(new Vector3(centerOffset, 0, centerOffset)),
                                    world.getEnemyGrid()[x][y].getType(),
                                    world.getEnemyGrid()[x][y].getRotation());
                        }catch (Exception ignored){}
                    }
                }
            }
        }

        WorldVertex[] finishedMobs;
        if(mobs.size() != 0){
            finishedMobs = new WorldVertex[mobs.size()];
            for (int i = 0; i < mobs.size(); i++)
                finishedMobs[i] = mobs.get(i);
        }else{
            finishedMobs = generateEmptyVertices();
        }

        return finishedMobs;
    }

    private void getMobVertices(ArrayList<WorldVertex> mobs, Vector3 position, Block type, float rotation){
        String model = "";
        switch (type){
            case BAT:
                model += "bat" + getAnimation(100, modelManager.BAT_ANIMATIONS);
                break;
            case PLANT:
                model += "flower" + getAnimation(100, modelManager.FLOWER_ANIMATIONS);
                break;
            case EYE:
                model += "eye" + getAnimation(100, modelManager.EYE_ANIMATIONS);
                break;
            case BLUE_SLIME:
                model += "slime" + getAnimation(50, modelManager.SLIME_ANIMATIONS);
                break;
            case GREEN_SLIME:
                model += "slimeGreen" + getAnimation(50, modelManager.SLIME_ANIMATIONS);
                break;
            case PURPLE_SLIME:
                model += "slimePink" + getAnimation(50, modelManager.SLIME_ANIMATIONS);
                break;
            case DANGER_NOODLE:
                model += "snake" + getAnimation(200, modelManager.SNAKE_ANIMATIONS);
                break;
            case GOLEM:
                model += "golem" + getAnimation(200, modelManager.GOLEM_ANIMATIONS);
                break;
            default:
                return;
        }

        mobs.addAll(Arrays.asList(modelManager.getModel(model).getVertices(position, new Vector3(0, rotation, 0))));
    }

    private int getAnimation(int speed, int animationsCount){
        return (int) ((System.currentTimeMillis() / speed) % animationsCount);
    }

    private WorldVertex[] generateEmptyVertices(){
        return new WorldVertex[]{
                new WorldVertex(
                        new float[]{0, 0, 0},
                        new float[]{0, 0},
                        new float[]{0, 0, 0},
                        new float[]{0, 0, 0, 0},
                        0
                ),
                new WorldVertex(
                        new float[]{0, 0, 0},
                        new float[]{0, 0},
                        new float[]{0, 0, 0},
                        new float[]{0, 0, 0, 0},
                        0
                ),
                new WorldVertex(
                        new float[]{0, 0, 0},
                        new float[]{0, 0},
                        new float[]{0, 0, 0},
                        new float[]{0, 0, 0, 0},
                        0
                )
        };
    }
}
