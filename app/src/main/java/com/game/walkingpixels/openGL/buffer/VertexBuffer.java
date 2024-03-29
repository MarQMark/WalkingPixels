package com.game.walkingpixels.openGL.buffer;

import com.game.walkingpixels.openGL.vertices.IVertex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static android.opengl.GLES31.*;

public class VertexBuffer {

    private final int id;
    private final int shaderID;
    private final ArrayList<VertexBufferLayout> layouts = new ArrayList<>();
    private final int vertexSize;
    private final int maxVertexCount;

    public VertexBuffer(int shaderID, int maxVertexCount, int vertexSize, VertexBufferLayout[] layouts){
        this.shaderID = shaderID;
        this.vertexSize = vertexSize;
        this.maxVertexCount = maxVertexCount;

        int[] buffer = new int[1];
        glGenBuffers(1 , buffer, 0);
        id = buffer[0];

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, maxVertexCount * vertexSize, null, GL_DYNAMIC_DRAW);

        addLayout(layouts);
    }

    public void fillBuffer(IVertex[] vertices){
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexSize * vertices.length);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = vbb.asFloatBuffer();
        for (IVertex vertex: vertices)
            vertex.writeToBuffer(fb);
        fb.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER,vertices.length * vertexSize, fb, GL_DYNAMIC_DRAW);
    }

    public void clearBuffer(){
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER,maxVertexCount * vertexSize, null, GL_DYNAMIC_DRAW);
    }

    public void fillPartBuffer(IVertex[] vertices, int offset){
        if(vertices == null) return;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexSize * vertices.length);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = vbb.asFloatBuffer();

        for (IVertex vertex: vertices)
            vertex.writeToBuffer(fb);

        fb.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferSubData(GL_ARRAY_BUFFER, offset * vertexSize, vertices.length * vertexSize, fb);
    }

    private void addLayout(VertexBufferLayout[] layouts){
        //calculate stride
        int stride = 0;
        for (VertexBufferLayout layout : layouts) {
            stride += layout.getSize() * layout.getDataSize();
        }

        int offset = 0;
        for (VertexBufferLayout layout : layouts) {
            layout.setStride(stride);
            layout.setOffset(offset);
            this.layouts.add(new VertexBufferLayout(layout));
            offset += layout.getSize() * layout.getDataSize();
        }
    }


    public void bind(){
        glBindBuffer(GL_ARRAY_BUFFER, id);
        for (VertexBufferLayout layout : layouts) {
            layout.loadLayout(shaderID);
        }
    }

    public void bind(int shaderID){
        glBindBuffer(GL_ARRAY_BUFFER, id);
        for (VertexBufferLayout layout : layouts) {
            layout.loadLayout(shaderID);
        }
    }

    public void unbind(){
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int getVertexSize(){ return vertexSize;}
}
