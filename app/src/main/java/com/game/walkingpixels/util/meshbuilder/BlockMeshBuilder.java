package com.game.walkingpixels.util.meshbuilder;

import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.atlas.GridTextureAtlas;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.util.vector.Vector3;

import java.util.ArrayList;

public class BlockMeshBuilder extends MeshBuilder{

    public BlockMeshBuilder(){
        registerGridTextureAtlas("blocks", new GridTextureAtlas(128, 64, 32));
    }

    private enum Side{
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        FRONT,
        BACK
    }

    public WorldVertex[] generateMesh(World world){
        ArrayList<WorldVertex> mesh = new ArrayList<>();

        for(int x = 0; x < world.getBlockGridSize(); x ++){
            for(int y = 0; y < world.getBlockGridSize(); y ++) {
                for(int z = 0; z < world.getWorldMaxHeight(); z ++) {

                    if(world.getBlockGrid()[x][y][z].ordinal() < Block.AIR.ordinal()){

                        if(z == 0 || world.getBlockGrid()[x][y][z - 1].ordinal() >= Block.AIR.ordinal()){
                            addSideToMesh(mesh, x, y, z, Side.BOTTOM, world.getBlockGrid()[x][y][z], world.getBlockGridSize());
                        }

                        if(z == world.getWorldMaxHeight() - 1 || world.getBlockGrid()[x][y][z + 1].ordinal() >= Block.AIR.ordinal()){
                            addSideToMesh(mesh, x, y, z, Side.TOP, world.getBlockGrid()[x][y][z], world.getBlockGridSize());
                        }

                        if(x == 0 || world.getBlockGrid()[x - 1][y][z].ordinal() >= Block.AIR.ordinal()){
                            addSideToMesh(mesh, x, y, z, Side.LEFT, world.getBlockGrid()[x][y][z], world.getBlockGridSize());
                        }

                        if(x == world.getBlockGridSize() - 1 || world.getBlockGrid()[x + 1][y][z].ordinal() >= Block.AIR.ordinal()){
                            addSideToMesh(mesh, x, y, z, Side.RIGHT, world.getBlockGrid()[x][y][z], world.getBlockGridSize());
                        }

                        if(y == 0 || world.getBlockGrid()[x][y - 1][z].ordinal() >= Block.AIR.ordinal()){
                            addSideToMesh(mesh, x, y, z, Side.BACK, world.getBlockGrid()[x][y][z], world.getBlockGridSize());
                        }

                        if(y == world.getBlockGridSize() - 1 || world.getBlockGrid()[x][y + 1][z].ordinal() >= Block.AIR.ordinal()){
                            addSideToMesh(mesh, x, y, z, Side.FRONT, world.getBlockGrid()[x][y][z], world.getBlockGridSize());
                        }
                    }
                }
            }
        }


        WorldVertex[] finishedMesh = new WorldVertex[mesh.size()];
        for (int i = 0; i < mesh.size(); i++){
            finishedMesh[i] = mesh.get(i);
        }
        return finishedMesh;
    }

    private void addSideToMesh(ArrayList<WorldVertex> mesh, int x, int z, int y, Side side, Block block, int blockGridSize){

        Vector3[] corners = getCorners(x, y, z, side, blockGridSize);
        Vector2[] texture = getTextureCoords(side, block);
        Vector3 normal = getNormal(side);

        for (int i = 0; i < 4; i++){
            mesh.add(new WorldVertex(
                    new float[]{ corners[i].x, corners[i].y, corners[i].z },
                    new float[]{ texture[i].x, texture[i].y },
                    new float[]{ normal.x, normal.y, normal.z},
                    new float[]{ 0.0f, 0.0f, 0.0f, 1.0f },
                    0.0f));
        }
    }

    private Vector3 getNormal(Side side){
        switch (side){
            case BOTTOM:
                return new Vector3(0.0f, -1.0f, 0.0f);
            case TOP:
                return new Vector3(0.0f, 1.0f, 0.0f);
            case LEFT:
                return new Vector3(-1.0f, 0.0f, 0.0f);
            case RIGHT:
                return new Vector3(1.0f, 0.0f, 0.0f);
            case FRONT:
                return new Vector3(0.0f, 0.0f, 1.0f);
            case BACK:
                return new Vector3(0.0f, 0.0f, -1.0f);
        }

        return new Vector3(0.0f, 0.0f, 0.0f);
    }

    private Vector2[] getTextureCoords(Side side, Block block){

        int id = 0;

        switch (block){
            case WATER: id = 4; break;
            case DIRT: id = 0; break;
            case GRASS:
                if( side == Side.TOP) id = 2;
                else id = 1;
                break;
            case SNOW:
                if( side == Side.TOP) id = 6;
                else id = 5;
                break;
        }

        return gridTextureAtlas("blocks").getTextureCoordinates(id);
    }

    private Vector3[] getCorners(int x, int y, int z, Side side, int blockGridSize){
        Vector3 toOrigin = new Vector3(blockGridSize / 2.0f, 0, blockGridSize / 2.0f);
        Vector3[] allCorners = new Vector3[8];
        allCorners[0] = new Vector3(x, y, z).sub(toOrigin);
        allCorners[1] = new Vector3(x + 1, y, z).sub(toOrigin);
        allCorners[2] = new Vector3(x, y, z +1).sub(toOrigin);
        allCorners[3] = new Vector3(x + 1, y, z +1).sub(toOrigin);
        allCorners[4] = new Vector3(x, y + 1, z).sub(toOrigin);
        allCorners[5] = new Vector3(x + 1, y + 1, z).sub(toOrigin);
        allCorners[6] = new Vector3(x, y + 1, z +1).sub(toOrigin);
        allCorners[7] = new Vector3(x + 1, y + 1, z +1).sub(toOrigin);


        Vector3[] corners = new Vector3[4];

        switch (side){
            case BOTTOM:
                corners[0] = allCorners[0];
                corners[1] = allCorners[1];
                corners[2] = allCorners[2];
                corners[3] = allCorners[3];
                break;
            case TOP:
                corners[0] = allCorners[4];
                corners[1] = allCorners[5];
                corners[2] = allCorners[6];
                corners[3] = allCorners[7];
                break;
            case LEFT:
                corners[0] = allCorners[0];
                corners[1] = allCorners[2];
                corners[2] = allCorners[4];
                corners[3] = allCorners[6];
                break;
            case RIGHT:
                corners[0] = allCorners[1];
                corners[1] = allCorners[3];
                corners[2] = allCorners[5];
                corners[3] = allCorners[7];
                break;
            case FRONT:
                corners[0] = allCorners[2];
                corners[1] = allCorners[3];
                corners[2] = allCorners[6];
                corners[3] = allCorners[7];
                break;
            case BACK:
                corners[0] = allCorners[0];
                corners[1] = allCorners[1];
                corners[2] = allCorners[4];
                corners[3] = allCorners[5];
                break;
        }

        return corners;
    }
}
