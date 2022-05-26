package com.game.walkingpixels.openGL;

import java.util.ArrayList;
import java.util.Arrays;

import static android.opengl.GLES31.*;


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
        update();
    }

    public void updateVertices(Vertex[] vertices){
        this.vertices.clear();
        this.vertices.addAll(Arrays.asList(vertices));
        lastVertexPosition = (vertices.length / 4) * 6;
        update();
    }

    public void bind(){
        vb.bind();
        ib.bind();
    }

    public void unbind(){
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void update(){
        vb.fillBuffer(vertices);
    }

    public void draw(){
        glDrawElements(GL_TRIANGLES, lastVertexPosition, GL_UNSIGNED_SHORT, 0);
    }
}
