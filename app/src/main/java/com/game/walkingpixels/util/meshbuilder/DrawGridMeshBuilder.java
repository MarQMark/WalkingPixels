package com.game.walkingpixels.util.meshbuilder;

import com.game.walkingpixels.model.DrawGrid;
import com.game.walkingpixels.openGL.vertices.IVertex;
import com.game.walkingpixels.openGL.vertices.drawGridVertex;

import java.util.ArrayList;

public class DrawGridMeshBuilder {

    public static IVertex[] generateMesh(DrawGrid drawGrid){
        ArrayList<drawGridVertex> pixels = new ArrayList<>();

        for (int x = 0; x < drawGrid.getSize(); x++){
            for (int y = 0; y < drawGrid.getSize(); y++) {

                if(drawGrid.getGrid()[x][y]){
                    pixels.add(new drawGridVertex(
                            new float[]{ x, y, 0.0f },
                            new float[]{ 1.0f, 1.0f, 1.0f, 1.0f }));
                    pixels.add(new drawGridVertex(
                            new float[]{ x + 1, y, 0.0f },
                            new float[]{ 1.0f, 1.0f, 1.0f, 1.0f }));
                    pixels.add(new drawGridVertex(
                            new float[]{ x, y + 1, 0.0f },
                            new float[]{ 1.0f, 1.0f, 1.0f, 1.0f }));
                    pixels.add(new drawGridVertex(
                            new float[]{ x + 1, y + 1, 0.0f },
                            new float[]{ 1.0f, 1.0f, 1.0f, 1.0f }));
                }

            }
        }

        drawGridVertex[] finishedMesh = new drawGridVertex[pixels.size()];
        for (int i = 0; i < pixels.size(); i++){
            finishedMesh[i] = pixels.get(i);
        }
        return finishedMesh;
    }
}
