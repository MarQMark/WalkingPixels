package com.game.walkingpixels.util.meshbuilder;

import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.TextureAtlas;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.vertices.MapVertex;
import com.game.walkingpixels.util.vector.Vector2;

import java.util.ArrayList;

public class MapMeshBuilder {

    private static final TextureAtlas textureAtlas = new TextureAtlas(64, 64, 16);

    public static MapVertex[] generateMesh(World world, int countX, int countY){
        ArrayList<MapVertex> mesh = new ArrayList<>();

        for(int x = 0; x < countX; x++){
            for(int y = 0; y < countY; y++) {
                int worldX = x + (int)world.getPosition().x - countX / 2 + world.getBlockGridSize() / 2;
                int worldY = y + (int)world.getPosition().y - countY / 2 + world.getBlockGridSize() / 2;
                int height = world.generateHeight(worldX, worldY);



                Block block = world.generateBlock(worldX, worldY, height + 1);
                if(block == Block.TREE)
                    addVertices(mesh, 4, x, y, countX, countY);
                else if(block == Block.BONFIRE)
                    addVertices(mesh, 5, x, y, countX, countY);
                else if(x == countX / 2 && y == countY / 2)
                    addVertices(mesh, 6, x, y, countX, countY);

                addVertices(mesh, height, x, y, countX, countY);
            }
        }


        MapVertex[] finishedMesh = new MapVertex[mesh.size()];
        for (int i = 0; i < mesh.size(); i++){
            finishedMesh[i] = mesh.get(i);
        }
        return finishedMesh;
    }

    private static void addVertices(ArrayList<MapVertex> mesh, int id, int x, int y, int countX, int countY){

        //determine texture coords
        float textureSize = 1.0f / (float) textureAtlas.getTexturesPerRow();
        Vector2 location = textureAtlas.getTextureLocation(id);
        location.scale(textureSize);

        mesh.add(new MapVertex(
                new float[]{ (2.0f * (x) / countX) - 1.0f, (2.0f * (y) / countY) - 1.0f },
                new float[]{ location.x, 1 - location.y - textureSize },
                0.0f));
        mesh.add(new MapVertex(
                new float[]{ (2.0f * (x + 1) / countX) - 1.0f, (2.0f * (y) / countY) - 1.0f },
                new float[]{ location.x + textureSize, 1 - location.y - textureSize },
                0.0f));
        mesh.add(new MapVertex(
                new float[]{ (2.0f * (x) / countX) - 1.0f, (2.0f * (y + 1) / countY) - 1.0f },
                new float[]{ location.x, 1 - location.y },
                0.0f));
        mesh.add(new MapVertex(
                new float[]{ (2.0f * (x + 1) / countX) - 1.0f, (2.0f * (y + 1) / countY) - 1.0f },
                new float[]{ location.x + textureSize, 1 - location.y },
                0.0f));

    }
}
