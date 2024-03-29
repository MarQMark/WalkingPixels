package com.game.walkingpixels.util.meshbuilder;

import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.atlas.GridTextureAtlas;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.vertices.MapVertex;
import com.game.walkingpixels.util.vector.Vector2;

import java.util.ArrayList;

public class MapMeshBuilder extends MeshBuilder{

    public MapMeshBuilder(){
        registerGridTextureAtlas("map", new GridTextureAtlas(64, 32, 16));
        registerGridTextureAtlas("compass", new GridTextureAtlas(196, 98, 49));
    }

    public MapVertex[] generateMesh(World world, int countX, int countY){
        ArrayList<MapVertex> mesh = new ArrayList<>();

        for(int x = 0; x < countX; x++){
            for(int y = 0; y < countY; y++) {
                int worldX = ((countX - 1) - x) + (int)world.getPosition().x - countX / 2 + world.getBlockGridSize() / 2;
                int worldY = y + (int)world.getPosition().y - countY / 2 + world.getBlockGridSize() / 2;
                int height = world.generateHeight(worldX, worldY);

                addVertices(mesh, height, x, y, countX, countY);

                Block block = world.generateBlock(worldX, worldY, height + 1);
                if(block == Block.TREE)
                    addVertices(mesh, 4, x, y, countX, countY);
                else if(block == Block.BONFIRE)
                    addVertices(mesh, 5, x, y, countX, countY);
                else if(x == countX / 2 && y == countY / 2)
                    addVertices(mesh, 6, x, y, countX, countY);
            }
        }

        MapVertex[] finishedMesh = new MapVertex[mesh.size()];
        for (int i = 0; i < mesh.size(); i++){
            finishedMesh[i] = mesh.get(i);
        }
        return finishedMesh;
    }

    private void addVertices(ArrayList<MapVertex> mesh, int id, int x, int y, int countX, int countY){
        Vector2[] textureCoordinates = gridTextureAtlas("map").getTextureCoordinates(id);
        mesh.add(new MapVertex(
                new float[]{ (2.0f * (x) / countX) - 1.0f, (2.0f * (y) / countY) - 1.0f },
                new float[]{ textureCoordinates[0].x , textureCoordinates[0].y },
                0.0f));
        mesh.add(new MapVertex(
                new float[]{ (2.0f * (x + 1) / countX) - 1.0f, (2.0f * (y) / countY) - 1.0f },
                new float[]{ textureCoordinates[1].x , textureCoordinates[1].y },
                0.0f));
        mesh.add(new MapVertex(
                new float[]{ (2.0f * (x) / countX) - 1.0f, (2.0f * (y + 1) / countY) - 1.0f },
                new float[]{ textureCoordinates[2].x , textureCoordinates[2].y },
                0.0f));
        mesh.add(new MapVertex(
                new float[]{ (2.0f * (x + 1) / countX) - 1.0f, (2.0f * (y + 1) / countY) - 1.0f },
                new float[]{ textureCoordinates[3].x , textureCoordinates[3].y },
                0.0f));

    }

    public MapVertex[] generateCompass(Vector2 direction, float aspectRatio){
        Vector2[] textureCoordinates = gridTextureAtlas("compass").getTextureCoordinates(directionToSprite(direction));
        return new MapVertex[]{
                new MapVertex(
                        new float[]{ 0.26f, -1 },
                        new float[]{ textureCoordinates[0].x , textureCoordinates[0].y },
                        1.0f),
                new MapVertex(
                        new float[]{ 1f, -1 },
                        new float[]{ textureCoordinates[1].x , textureCoordinates[1].y },
                        1.0f),
                new MapVertex(
                        new float[]{ 0.26f, 1f - (0.76f * aspectRatio)},
                        new float[]{ textureCoordinates[2].x , textureCoordinates[2].y },
                        1.0f),
                new MapVertex(
                        new float[]{ 1f, 1f - (0.76f * aspectRatio)},
                        new float[]{ textureCoordinates[3].x , textureCoordinates[3].y },
                        1.0f)
        };
    }

    private int directionToSprite(Vector2 direction){
        if(direction.x == 1){
            if(direction.y == 1) return 7;
            else if(direction.y == 0) return 6;
            else return 5;
        }
        else if(direction.x == 0){
            if(direction.y == -1) return 4;
            else return 0;
        }
        else {
            if(direction.y == 1) return 1;
            else if(direction.y == 0) return 2;
            else return 3;
        }
    }
}
