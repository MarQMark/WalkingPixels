package com.game.walkingpixels.util.meshbuilder;

import com.game.walkingpixels.model.DrawGrid;
import com.game.walkingpixels.openGL.vertices.IVertex;
import com.game.walkingpixels.openGL.vertices.drawGridVertex;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.util.vector.Vector4;

import java.util.ArrayList;
import java.util.Vector;

public class DrawGridMeshBuilder {

    public static IVertex[] generateMesh(DrawGrid drawGrid){
        ArrayList<drawGridVertex> pixels = new ArrayList<>();

        for (int x = 0; x < drawGrid.getSize(); x++){
            for (int y = 0; y < drawGrid.getSize(); y++) {

                if(drawGrid.getGrid()[x][y] == 1){
                    getVertices(pixels, new Vector2(x, y), new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
                }

            }
        }

        getBackground(pixels, drawGrid);

        drawGridVertex[] finishedMesh = new drawGridVertex[pixels.size()];
        for (int i = 0; i < pixels.size(); i++){
            finishedMesh[i] = pixels.get(i);
        }
        return finishedMesh;
    }

    private static void getVertices(ArrayList<drawGridVertex> pixels, Vector2 position, Vector4 color){
        pixels.add(new drawGridVertex(
                new float[]{ position.x, position.y, 0.0f },
                new float[]{ color.x, color.y, color.z, color.w }));
        pixels.add(new drawGridVertex(
                new float[]{ position.x + 1, position.y, 0.0f },
                new float[]{ color.x, color.y, color.z, color.w }));
        pixels.add(new drawGridVertex(
                new float[]{ position.x, position.y + 1, 0.0f },
                new float[]{ color.x, color.y, color.z, color.w }));
        pixels.add(new drawGridVertex(
                new float[]{ position.x + 1, position.y + 1, 0.0f },
                new float[]{ color.x, color.y, color.z, color.w }));
    }

    private static void getBackground(ArrayList<drawGridVertex> pixels, DrawGrid drawGrid){
        Vector4 color = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
        pixels.add(new drawGridVertex(
                new float[]{ 0.0f, 0.0f, 0.0f },
                new float[]{ color.x, color.y, color.z, color.w }));
        pixels.add(new drawGridVertex(
                new float[]{ drawGrid.getSize(), 0.0f, 0.0f },
                new float[]{ color.x, color.y, color.z, color.w }));
        pixels.add(new drawGridVertex(
                new float[]{ 0.0f, drawGrid.getSize(), 0.0f },
                new float[]{ color.x, color.y, color.z, color.w }));
        pixels.add(new drawGridVertex(
                new float[]{ drawGrid.getSize(), drawGrid.getSize(), 0.0f },
                new float[]{ color.x, color.y, color.z, color.w }));
    }
}
