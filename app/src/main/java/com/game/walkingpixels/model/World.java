package com.game.walkingpixels.model;

import com.game.walkingpixels.util.NoiseGenerator;
import com.game.walkingpixels.util.vector.Vector2;

import java.util.Random;

public class World {

    public final int worldMaxHeight = 5;
    public int blockGridSize = 15;

    private int despawnRadius = 5;
    private int enemyGridSize = blockGridSize + despawnRadius;
    public Block[][][] blockGrid = new Block[blockGridSize][blockGridSize][worldMaxHeight];
    public Enemy[][] enemyGrid = new Enemy[enemyGridSize][enemyGridSize];

    private boolean hasMoved = false;

    private final Vector2 position = new Vector2(0,0);
    private final Vector2 direction = new Vector2(1, 0);

    private final NoiseGenerator noiseGenerator;
    private final Random random = new Random();


    public World(double seed){
        noiseGenerator = new NoiseGenerator(seed);
        generateBlockGrid();
        generateEnemyGrid();
    }

    public Enemy checkForEnemy(){
        final int enemyAttackRadius = 1;
        final int middle = enemyGridSize / 2 + 2; //why do I have to add two there???

        for (int x = middle - enemyAttackRadius; x <= middle + enemyAttackRadius; x++){
            for (int y = middle - enemyAttackRadius; y <= middle + enemyAttackRadius; y++) {
                if(enemyGrid[x][y] != null)
                    return enemyGrid[x][y];
            }
        }

        return null;
    }

    public void setDirection(int degree){
        int normalizedDegree = (degree - 45) % 360;

        if(normalizedDegree < -270){
            direction.x = 1;
            direction.y = -1;
        }
        else if(normalizedDegree < -180){
            direction.x = 1;
            direction.y = 1;
        }
        else if(normalizedDegree < -90){
            direction.x = -1;
            direction.y = 1;
        }
        else if(normalizedDegree < 0){
            direction.x = -1;
            direction.y = -1;
        }
        else if(normalizedDegree < 90){
            direction.x = 1;
            direction.y = -1;
        }
        else if(normalizedDegree < 180){
            direction.x = 1;
            direction.y = 1;
        }
        else if(normalizedDegree < 270){
            direction.x = -1;
            direction.y = 1;
        }
        else{
            direction.x = -1;
            direction.y = -1;
        }
    }

    public boolean forward(){
        hasMoved = true;
        position.x += direction.x;
        position.y += direction.y;
        generateBlockGrid();
        moveEnemyGrid();

        return true;
    }

    public void setPosition(int x, int y){
        hasMoved = true;
        position.x = x;
        position.y = y;
        generateBlockGrid();
        generateEnemyGrid();
    }

    public void setWorldSize(int WorldSize){
        this.blockGridSize = WorldSize;
        enemyGridSize = WorldSize + despawnRadius;
        blockGrid = new Block[WorldSize][WorldSize][worldMaxHeight];
        enemyGrid = new Enemy[enemyGridSize][enemyGridSize];
        generateBlockGrid();
        generateEnemyGrid();
    }

    public void clear(){
        blockGrid = new Block[blockGridSize][blockGridSize][worldMaxHeight];
        enemyGrid = new Enemy[enemyGridSize][enemyGridSize];
    }

    private void moveEnemyGrid(){
        if(direction.x == 1){
            for(int x = 0; x < enemyGridSize - 1; x++)
                System.arraycopy(enemyGrid[x + 1], 0, enemyGrid[x], 0, enemyGridSize);

            for(int y = 0; y < enemyGridSize; y++){
                if(random.nextInt() % (100 * blockGridSize) == 0)
                    enemyGrid[blockGridSize + despawnRadius - 1][y] = new Enemy(Block.SLIME, 100);
                else
                    enemyGrid[enemyGridSize - 1][y] = null;
            }
        }
        if(direction.x == -1){
            for(int x = enemyGridSize - 1; x > 0; x--){
                    System.arraycopy(enemyGrid[x - 1], 0, enemyGrid[x], 0, enemyGridSize);
            }

            for(int y = 0; y < enemyGridSize; y++){
                if(random.nextInt() % (100 * blockGridSize) == 0)
                    enemyGrid[0][y] = new Enemy(Block.SLIME, 100);
                else
                    enemyGrid[0][y] = null;
            }
        }
        if(direction.y == 1){
            for(int y = 0; y < enemyGridSize - 1; y++){
                for(int x = 0; x < enemyGridSize; x++)
                    enemyGrid[x][y] = enemyGrid[x][y + 1];
            }

            for(int x = 0; x < enemyGridSize; x++){
                if(random.nextInt() % (100 * blockGridSize) == 0)
                    enemyGrid[x][enemyGridSize - 1] = new Enemy(Block.SLIME, 100);
                else
                    enemyGrid[x][enemyGridSize - 1] = null;
            }
        }
        if(direction.y == -1){
            for(int y = enemyGridSize - 1; y > 0 ; y--){
                for(int x = 0; x < enemyGridSize; x++)
                    enemyGrid[x][y] = enemyGrid[x][y - 1];
            }

            for(int x = 0; x < enemyGridSize; x++){
                if(random.nextInt() % (100 * blockGridSize) == 0)
                    enemyGrid[x][0] = new Enemy(Block.SLIME, 100);
                else
                    enemyGrid[x][0] = null;
            }
        }
    }

    private void generateEnemyGrid(){
        for (int x = 0; x < enemyGridSize; x++) {
            for (int y = 0; y < enemyGridSize; y++) {
                //if(random.nextInt() % (100 * renderedWorldSize) == 0 && !insideCircle(x, y, renderedWorldSize * 0.6f))
                if(random.nextInt() % (4) == 0 && !insideCircle(x-despawnRadius, y-despawnRadius, (blockGridSize / 2.0f) * 0.8f)){
                    enemyGrid[x][y] = new Enemy(Block.SLIME, 100);
                }
                else {
                    enemyGrid[x][y] = null;
                }
            }
        }
    }

    private void generateBlockGrid(){
        for (int x = 0; x < blockGridSize; x++){
            for (int y = 0; y < blockGridSize; y++) {

                if(insideCircle(x, y, blockGridSize / 2.0f)){

                    int height = generateHeight((int) (x + position.x), (int) (y + position.y));

                    for(int z = 0; z < worldMaxHeight; z++){
                        if(z < height){
                            blockGrid[x][y][z] = Block.DIRT;
                        } else if(z == height){
                            blockGrid[x][y][z] = heightToBlock(height);
                        } else {
                            blockGrid[x][y][z] = Block.AIR;
                        }
                    }

                    if(x == blockGridSize / 2 && y == blockGridSize / 2)
                        blockGrid[x][y][height + 1]  = Block.PLAYER;

                }
                else {
                    for(int z = 0; z < worldMaxHeight; z++)
                        blockGrid[x][y][z] = Block.AIR;
                }

            }
        }
    }

    public boolean insideCircle(int x, int y, float radius){
        float dx = (int)(blockGridSize / 2.0f) - x,
                dy = (int)(blockGridSize / 2.0f) - y;
        float distance = (float)Math.sqrt(dx*dx + dy*dy);
        return distance <= radius;
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

    public Block[][][] getBlockGrid(){
        return blockGrid;
    }
    public Enemy[][] getEnemyGrid(){
        return enemyGrid;
    }
    public int getBlockGridSize(){
        return blockGridSize;
    }
    public int getWorldMaxHeight(){
        return worldMaxHeight;
    }
    public int getEnemyGridSize(){
        return enemyGridSize;
    }
    public int getDespawnRadius(){
        return despawnRadius;
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
