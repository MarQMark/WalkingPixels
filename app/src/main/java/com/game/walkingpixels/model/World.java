package com.game.walkingpixels.model;

import com.game.walkingpixels.util.NoiseGenerator;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.util.vector.Vector3;

public class World {

    public final int worldMaxHeight = 5;
    public int renderedWorldSize = 17;
    public Block[][][] renderedWorld = new Block[renderedWorldSize][renderedWorldSize][worldMaxHeight];

    private boolean hasMoved = false;

    private final Vector2 position = new Vector2(0,0);
    private final Vector2 direction = new Vector2(1, 0);

    private final NoiseGenerator noiseGenerator;


    public World(double seed){
        noiseGenerator = new NoiseGenerator(seed);
        generateRenderedWorld();
    }

    public void setDirection(int degree){
        int normalizedDegree = (degree - 45) % 360;

        if(normalizedDegree < -270){
            direction.x = 1;
            direction.y = 0;
        }
        else if(normalizedDegree < -180){
            direction.x = 0;
            direction.y = 1;
        }
        else if(normalizedDegree < -90){
            direction.x = -1;
            direction.y = 0;
        }
        else if(normalizedDegree < 0){
            direction.x = 0;
            direction.y = -1;
        }
        else if(normalizedDegree < 90){
            direction.x = 1;
            direction.y = 0;
        }
        else if(normalizedDegree < 180){
            direction.x = 0;
            direction.y = 1;
        }
        else if(normalizedDegree < 270){
            direction.x = -1;
            direction.y = 0;
        }
        else{
            direction.x = 0;
            direction.y = -1;
        }
    }

    public void forward(){
        hasMoved = true;
        position.x += direction.x;
        position.y += direction.y;
        generateRenderedWorld();
    }

    public void move(int dx, int dy){
        hasMoved = true;
        position.x += dx;
        position.y += dy;
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

                if(insideCircle(x, y)){

                    int height = generateHeight((int) (x + position.x), (int) (y + position.y));

                    for(int z = 0; z < worldMaxHeight; z++){
                        if(z < height){
                            renderedWorld[x][y][z] = Block.DIRT;
                        } else if(z == height){
                            renderedWorld[x][y][z] = heightToBlock(height);
                        } else {
                            renderedWorld[x][y][z] = Block.AIR;
                        }
                    }

                    if(x == renderedWorldSize / 2 && y == renderedWorldSize / 2)
                        renderedWorld[x][y][height + 1]  = Block.PLAYER;

                }
                else {
                    for(int z = 0; z < worldMaxHeight; z++)
                        renderedWorld[x][y][z] = Block.AIR;
                }

            }
        }
    }

    private boolean insideCircle(int x, int y){
        float dx = renderedWorldSize / 2.0f - x,
                dy = renderedWorldSize / 2.0f - y;
        float distance = (float)Math.sqrt(dx*dx + dy*dy);
        return distance <= renderedWorldSize / 2.0f;
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

    public boolean hasMoved() {
        if(hasMoved){
            hasMoved = false;
            return true;
        }
        else{
            return false;
        }
    }
}
