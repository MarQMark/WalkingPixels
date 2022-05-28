package com.game.walkingpixels.model;

import com.game.walkingpixels.util.NoiseGenerator;
import com.game.walkingpixels.util.vector.Vector2;

public class World {

    public enum Block{
        AIR,
        DIRT,
        WATER,
        GRASS,
        SNOW,
        PLAYER
    }

    public final int worldMaxHeight = 5;
    public int renderedWorldSize = 24;
    public Block[][][] renderedWorld = new Block[renderedWorldSize][renderedWorldSize][worldMaxHeight];
    private final Vector2 playerPosition = new Vector2(0,0);

    private final NoiseGenerator noiseGenerator;


    public World(double seed){
        noiseGenerator = new NoiseGenerator(seed);
        generateRenderedWorld();
    }

    public void movePlayerPosition(int dx, int dy){
        playerPosition.x += dx;
        playerPosition.y += dy;
        generateRenderedWorld();
    }

    public void SetRenderedSize(int renderedWorldSize){
        this.renderedWorldSize = renderedWorldSize;
        renderedWorld = new Block[renderedWorldSize][renderedWorldSize][worldMaxHeight];
        generateRenderedWorld();
    }

    private void generateRenderedWorld(){
        for (int x = 0; x < renderedWorldSize; x++){
            for (int y = 0; y < renderedWorldSize; y++) {

                int height = generateHeight((int) (x + playerPosition.x), (int) (y + playerPosition.y));

                for(int z = 0; z < worldMaxHeight; z++){
                    if(z < height){
                        renderedWorld[x][y][z] = Block.DIRT;
                    } else if(z == height){
                        renderedWorld[x][y][z] = heightToBlock(height);
                    } else {
                        renderedWorld[x][y][z] = Block.AIR;
                    }
                }

            }
        }

        renderedWorld[renderedWorldSize / 2][renderedWorldSize / 2][worldMaxHeight - 2] = Block.PLAYER;
        //renderedWorld[renderedWorldSize / 2][renderedWorldSize / 2][worldMaxHeight - 2] = Block.DIRT;
        //renderedWorld[renderedWorldSize / 2][renderedWorldSize / 2][worldMaxHeight - 3] = Block.DIRT;
        renderedWorld[renderedWorldSize / 2 + 4][renderedWorldSize / 2][worldMaxHeight - 1] = Block.DIRT;
    }

    private Block heightToBlock(int height){
        switch (height){
            case 0: return Block.WATER;
            case 1:
            case 2:
                return Block.GRASS;
            case 3: return Block.SNOW;
        }

        return Block.AIR;
    }

    private int generateHeight(int x, int y)
    {
        double perlinScale = 1f;
        double perlinNoise = noiseGenerator.noise(x * perlinScale, y * perlinScale);

        if(perlinNoise > 0.5f) return 3;
        else if(perlinNoise > 0.15) return 2;
        else if(perlinNoise > -0.3) return 1;
        else return 0; //water level
    }
}
