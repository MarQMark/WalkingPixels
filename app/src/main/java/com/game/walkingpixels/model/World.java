package com.game.walkingpixels.model;

import com.game.walkingpixels.util.NoiseGenerator;
import com.game.walkingpixels.util.vector.Vector2;

import java.util.Random;

public class World {

    public final int worldMaxHeight = 5;
    public int blockGridSize = 15;

    private final int despawnRadius = 5;
    private int enemyGridSize = blockGridSize + despawnRadius;
    public Block[][][] blockGrid = new Block[blockGridSize][blockGridSize][worldMaxHeight];
    public Enemy[][] enemyGrid = new Enemy[enemyGridSize][enemyGridSize];

    private boolean hasMoved = false;

    private final Vector2 position = new Vector2(0,0);
    private final Vector2 direction = new Vector2(1, 0);

    private NoiseGenerator noiseGenerator;
    private final Random random = new Random();


    public World(double seed){
        noiseGenerator = new NoiseGenerator(seed);
        generateBlockGrid();
        generateEnemyGrid();
    }

    public World(){
        noiseGenerator = new NoiseGenerator(random.nextFloat());
        validSeed();
        generateBlockGrid();
        generateEnemyGrid();
    }

    private void validSeed(){
        if(heightToBlock(generateHeight(blockGridSize / 2,blockGridSize / 2)) == Block.WATER || heightToBlock(generateHeight(blockGridSize / 2 + 1,blockGridSize / 2 + 1)) == Block.WATER){
            noiseGenerator = new NoiseGenerator(random.nextFloat());
            validSeed();
        }
    }

    public void removeEnemy(){
        final int enemyAttackRadius = 1;
        final int middle = enemyGridSize / 2 + 2; //why do I have to add two there???

        for (int x = middle - enemyAttackRadius; x <= middle + enemyAttackRadius; x++){
            for (int y = middle - enemyAttackRadius; y <= middle + enemyAttackRadius; y++) {
                if(enemyGrid[x][y] != null)
                    enemyGrid[x][y] = null;
            }
        }
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

    public boolean checkForBonfire(){
        final int enemyAttackRadius = 1;
        final int middle = blockGridSize / 2; //why do I have to add two there???

        for (int x = middle - enemyAttackRadius; x <= middle + enemyAttackRadius; x++){
            for (int y = middle - enemyAttackRadius; y <= middle + enemyAttackRadius; y++) {
                for (int z = 0; z < worldMaxHeight; z++) {
                    if (blockGrid[x][y][z] == Block.BONFIRE)
                        return true;
                }
            }
        }

        return false;
    }

    public void setDirection(int degree){
        int normalizedDegree = ((degree - 45) % 360) / 45;
        double rad = ((normalizedDegree + 0.6f) * 2.0f * Math.PI) / 8.0f;

        double xLimit = Math.sin(rad);
        if(xLimit > 0.4)
            direction.x = 1;
        else if(xLimit < -0.4)
            direction.x = -1;
        else
            direction.x = 0;

        double yLimit = -Math.cos(rad);
        if(yLimit > 0.4)
            direction.y = 1;
        else if(yLimit < -0.4)
            direction.y = -1;
        else
            direction.y = 0;
    }

    public boolean forward(){
        //check if can move forward
        int x = (int) (blockGridSize / 2 + direction.x);
        int y = (int) (blockGridSize / 2 + direction.y);
        for (int z = 0; z < worldMaxHeight; z++){
            if(blockGrid[x][y][z] == Block.WATER || blockGrid[x][y][z].ordinal() > Block.AIR.ordinal())
                return false;
        }

        position.x += direction.x;
        position.y += direction.y;
        generateBlockGrid();
        moveEnemyGrid();
        hasMoved = true;

        return true;
    }

    public void setPosition(int x, int y){
        position.x = x;
        position.y = y;
        clear();
        generateBlockGrid();
        generateEnemyGrid();
        hasMoved = true;
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

            for(int y = 0; y < enemyGridSize; y++)
                spawnEnemy(enemyGridSize - 1,  y);
        }
        if(direction.x == -1){
            for(int x = enemyGridSize - 1; x > 0; x--){
                    System.arraycopy(enemyGrid[x - 1], 0, enemyGrid[x], 0, enemyGridSize);
            }

            for(int y = 0; y < enemyGridSize; y++)
                spawnEnemy(0, y);
        }
        if(direction.y == 1){
            for(int y = 0; y < enemyGridSize - 1; y++){
                for(int x = 0; x < enemyGridSize; x++)
                    enemyGrid[x][y] = enemyGrid[x][y + 1];
            }

            for(int x = 0; x < enemyGridSize; x++)
                spawnEnemy(x, enemyGridSize - 1);
        }
        if(direction.y == -1){
            for(int y = enemyGridSize - 1; y > 0 ; y--){
                for(int x = 0; x < enemyGridSize; x++)
                    enemyGrid[x][y] = enemyGrid[x][y - 1];
            }

            for(int x = 0; x < enemyGridSize; x++)
                spawnEnemy(x, 0);
        }
    }

    private void generateEnemyGrid(){
        for (int x = 0; x < enemyGridSize; x++) {
            for (int y = 0; y < enemyGridSize; y++) {
                if(!insideCircle(x - despawnRadius, y - despawnRadius, (blockGridSize / 2.0f) * 0.5f))
                    spawnEnemy(x, y);
            }
        }
    }

    private void spawnEnemy(int x, int y){

        if(random.nextInt() % (7 * blockGridSize) == 0){
            //make sure enemy doesn't spawn in something
            int worldX = (int) (x + position.x - despawnRadius);
            int worldY = (int) (y + position.y - despawnRadius);
            int height = generateHeight(worldX, worldY);
            if(generateBlock(worldX, worldY, height) != Block.WATER && generateBlock(worldX, worldY, height + 1) == Block.AIR){
                int level = (int) (position.getLength() / 100.0f + 1.0f);
                enemyGrid[x][y] = new Enemy(level);
            }
        }
        else{
            enemyGrid[x][y] = null;
        }
    }

    private void generateBlockGrid(){
        for (int x = 0; x < blockGridSize; x++){
            for (int y = 0; y < blockGridSize; y++) {

                if(insideCircle(x, y, blockGridSize / 2.0f)){

                    int worldX = x + (int)position.x;
                    int worldY = y + (int)position.y;

                    //generate block pillar
                    for(int z = 0; z < worldMaxHeight; z++){
                        blockGrid[x][y][z] = generateBlock(worldX, worldY, z);
                    }

                    //set player
                    if(x == blockGridSize / 2 && y == blockGridSize / 2)
                        blockGrid[x][y][generateHeight(worldX, worldY) + 1]  = Block.PLAYER;

                }
                else {
                    for(int z = 0; z < worldMaxHeight; z++)
                        blockGrid[x][y][z] = Block.AIR;
                }

            }
        }
    }

    public Block generateBlock(int x, int y, int z){
        int height = generateHeight(x, y);
        Block block = Block.AIR;

        //fill everything below height with dirt
        if(z < height){
            block = Block.DIRT;
        }
        else if(z == height){
            block = heightToBlock(height);
        }
        else if(z == height + 1){
            //spawn bonfire at spawn (only hardcoded bonfire)
            if(x == blockGridSize / 2 + 1 && y == blockGridSize / 2 + 1)
                block = Block.BONFIRE;
            //set bonfires
            else if(generateBonfire(x, y) && heightToBlock(height) != Block.WATER)
                block = Block.BONFIRE;
            //set trees
            else if(generateTree(x, y) && heightToBlock(height) != Block.WATER)
                block = Block.TREE;
            else
                block = Block.AIR;
        }

        return block;
    }

    public boolean insideCircle(int x, int y, float radius){
        float dx = (int)(blockGridSize / 2.0f) - x,
                dy = (int)(blockGridSize / 2.0f) - y;
        float distance = (float)Math.sqrt(dx*dx + dy*dy);
        return distance <= radius;
    }

    public Block heightToBlock(int height){
        switch (height){
            case 0: return Block.WATER;
            case 1:
            case 2:
                return Block.GRASS;
            case 3: return Block.SNOW;
        }

        return Block.AIR;
    }

    public int generateHeight(int x, int y){
        double perlinScale = 1f;
        double perlinNoise = noiseGenerator.noise(x * perlinScale, y * perlinScale);

        if(perlinNoise > 0.5f) return 3;
        else if(perlinNoise > 0.15) return 2;
        else if(perlinNoise > -0.3) return 1;
        else return 0; //water level
    }

    private boolean generateTree(int x, int y){
        double perlinScale = 100f;
        double perlinNoise = noiseGenerator.noise(x * perlinScale, y * perlinScale);

        return perlinNoise > 0.7f;
    }
    private boolean generateBonfire(int x, int y){
        double perlinScale = 1000f;
        double perlinNoise = noiseGenerator.noise(x * perlinScale, y * perlinScale);

        return perlinNoise > 0.9f;
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
    public Vector2 getPosition() {
        return position;
    }
    public Vector2 getDirection() {
        return direction;
    }

    public float getSeed(){
        return (float) noiseGenerator.getSeed();
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
