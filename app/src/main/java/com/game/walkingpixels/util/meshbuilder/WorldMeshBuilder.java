package com.game.walkingpixels.util.meshbuilder;

import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.vertices.worldVertex;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.util.vector.Vector3;

import java.util.ArrayList;

public class WorldMeshBuilder {

    private static final int textureAtlasWidth = 64;
    private static final int textureAtlasHeight = 64;
    private static final int textureAtlasBlock = 16;
    private static final int textureAtlasBlocksPerRow = textureAtlasWidth / textureAtlasBlock;

    private enum Side{
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        FRONT,
        BACK
    }

    public static worldVertex[] generateMesh(World.Block[][][] renderedWorld, int renderedWorldSize, int worldMaxHeight){
        ArrayList<worldVertex> mesh = new ArrayList<>();

        for(int x = 0; x < renderedWorldSize; x ++){
            for(int y = 0; y < renderedWorldSize; y ++) {
                for(int z = 0; z < worldMaxHeight; z ++) {

                    if(renderedWorld[x][y][z] != World.Block.AIR && renderedWorld[x][y][z] != World.Block.PLAYER){
                        if(z == 0|| renderedWorld[x][y][z - 1] == World.Block.AIR){
                            addSideToMesh(mesh, x, y, z, Side.BOTTOM, renderedWorld[x][y][z], renderedWorldSize);
                        }

                        if(z == worldMaxHeight - 1 || renderedWorld[x][y][z + 1] == World.Block.AIR){
                            addSideToMesh(mesh, x, y, z, Side.TOP, renderedWorld[x][y][z], renderedWorldSize);
                        }

                        if(x == 0 || renderedWorld[x - 1][y][z] == World.Block.AIR){
                            addSideToMesh(mesh, x, y, z, Side.LEFT, renderedWorld[x][y][z], renderedWorldSize);
                        }

                        if(x == renderedWorldSize - 1 || renderedWorld[x + 1][y][z] == World.Block.AIR){
                            addSideToMesh(mesh, x, y, z, Side.RIGHT, renderedWorld[x][y][z], renderedWorldSize);
                        }

                        if(y == 0 || renderedWorld[x][y - 1][z] == World.Block.AIR){
                            addSideToMesh(mesh, x, y, z, Side.BACK, renderedWorld[x][y][z], renderedWorldSize);
                        }

                        if(y == renderedWorldSize - 1 || renderedWorld[x][y + 1][z] == World.Block.AIR){
                            addSideToMesh(mesh, x, y, z, Side.FRONT, renderedWorld[x][y][z], renderedWorldSize);
                        }
                    }

                    /*if(renderedWorld[x][y][z] == World.Block.PLAYER){
                        MobMeshBuilder.getMobVertices(mesh, new Vector3(x, z, y).sub(new Vector3(renderedWorldSize / 2, 0, renderedWorldSize / 2)), renderedWorld[x][y][z]);
                    }*/
                }
            }
        }


        worldVertex[] finishedMesh = new worldVertex[mesh.size()];
        for (int i = 0; i < mesh.size(); i++){
            finishedMesh[i] = mesh.get(i);
        }
        return finishedMesh;
    }

    private static void addSideToMesh(ArrayList<worldVertex> mesh, int x, int z, int y, Side side, World.Block block, int renderedWorldSize){

        Vector3[] corners = getCorners(x, y, z, side, renderedWorldSize);
        Vector2[] texture = getTextureCoords(side, block);
        Vector3 normal = getNormal(side);

        for (int i = 0; i < 4; i++){
            mesh.add(new worldVertex(
                    new float[]{ corners[i].x, corners[i].y, corners[i].z },
                    new float[]{ texture[i].x, texture[i].y },
                    new float[]{ normal.x, normal.y, normal.z},
                    new float[]{ 0.0f, 0.0f, 0.0f, 1.0f },
                    0.0f));
        }
    }

    private static Vector3 getNormal(Side side){
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

    private static Vector2[] getTextureCoords(Side side, World.Block block){

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

        float textureSize = 1.0f / (float) textureAtlasBlocksPerRow;
        Vector2 location = getTextureLocation(id);
        location.scale(textureSize);

        Vector2[] textures = new Vector2[4];
        textures[0] = new Vector2(location.x, 1 - location.y - textureSize);
        textures[1] = new Vector2(location.x + textureSize, 1 - location.y - textureSize);
        textures[3] = new Vector2(location.x + textureSize, 1 - location.y);
        textures[2] = new Vector2(location.x, 1 - location.y);

        return textures;
    }

    private static Vector2 getTextureLocation(int id){
        Vector2 location = new Vector2();
        location.x = id % textureAtlasBlocksPerRow;
        location.y = id / textureAtlasBlocksPerRow;

        return location;
    }

    private static Vector3[] getCorners(int x, int y, int z, Side side, int renderedWorldSize){
        Vector3 toOrigin = new Vector3(renderedWorldSize / 2, 0, renderedWorldSize / 2);
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
