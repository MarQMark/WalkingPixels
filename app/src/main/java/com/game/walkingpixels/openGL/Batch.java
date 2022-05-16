package com.game.walkingpixels.openGL;

import java.util.ArrayList;
import java.util.Arrays;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;

public class Batch {

    private final ArrayList<Vertex> vertices = new ArrayList<>();

    private VertexBuffer vb;
    private IndexBuffer ib;
    //private Texture tx;

    private final int shaderID;
    private int lastVertexPosition;

    public Batch(int shaderID, int maxQuadCount){
        this.shaderID = shaderID;
        vb = new VertexBuffer(shaderID, maxQuadCount * 4);

        short[] indices = new short[maxQuadCount * 6];

        for(int i = 0; i < maxQuadCount*2; i++){
            if(i % 2 == 0){
                indices[i * 3] = (short)(i * 2);
                indices[i * 3 + 1] = (short)(i * 2 + 1);
                indices[i * 3 + 2] = (short)(i * 2 + 2);
            }
            else {
                indices[i * 3] = (short)(i * 2);
                indices[i * 3 + 1] = (short)(i * 2 + 1);
                indices[i * 3 + 2] = (short)(i * 2 - 1);
            }
        }

        ib = new IndexBuffer(indices);
    }

    public void addVertices(Vertex[] vertices){
        this.vertices.addAll(Arrays.asList(vertices));
        lastVertexPosition += (vertices.length / 4) * 6;
    }

    public void updateVertices(Vertex[] vertices){
        this.vertices.clear();
        this.vertices.addAll(Arrays.asList(vertices));
        lastVertexPosition = (vertices.length / 4) * 6;
    }

    public void bind(){
        vb.fillBuffer(vertices);
        vb.bind();
        //tx.bind();
        ib.bind();
    }

    public void draw(){

        glDrawElements(GL_TRIANGLES, lastVertexPosition, GL_UNSIGNED_SHORT, 0);
    }
}
