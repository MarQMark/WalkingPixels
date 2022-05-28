package com.game.walkingpixels.openGL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.opengl.GLES31.*;


public class Batch {

    private final ArrayList<Vertex> vertices = new ArrayList<>();

    private class BatchPart{
        public String name;
        public int offset;
        public int size;
        public BatchPart(String name, int offset, int size){
            this.name = name;
            this.offset = offset;
            this.size = size;
        }
    };
    private final ArrayList<BatchPart> parts = new ArrayList<>();


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

    public void addVertices(String name, Vertex[] vertices){
        if(parts.size() == 0)
            parts.add(new BatchPart(name, 0, vertices.length));
        else
            parts.add(new BatchPart(name, parts.get(parts.size() - 1).offset + parts.get(parts.size() - 1).size, vertices.length));

        this.vertices.addAll(Arrays.asList(vertices));
        lastVertexPosition += (vertices.length / 4) * 6;

        vb.fillPartBuffer(vertices, parts.get(parts.size() - 1).offset);
    }

    public void updateVertices(String name, Vertex[] vertices){
        int partNumber = -1;
        for (int i = 0; i < parts.size(); i++) {
            if(parts.get(i).name.equals(name)){
                partNumber = i;
                break;
            }
        }

        if(partNumber == -1){
            addVertices(name, vertices);
            return;
        }

        vb.fillPartBuffer(vertices, parts.get(partNumber).offset);
    }


    public void bind(){
        vb.bind();
        ib.bind();
    }

    public void unbind(){
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }


    public void draw(){
        glDrawElements(GL_TRIANGLES, lastVertexPosition, GL_UNSIGNED_SHORT, 0);
    }
}
