package com.game.walkingpixels.openGL;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.*;

public class VertexBuffer {

    private final int id;
    private final int shaderID;
    private final ArrayList<VertexBufferLayout> layouts = new ArrayList<>();

    public VertexBuffer(int shaderID, Vertex[] vertices){
        this.shaderID = shaderID;

        ByteBuffer vbb = ByteBuffer.allocateDirect(Vertex.size * vertices.length);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = vbb.asFloatBuffer();
        for (Vertex vertex: vertices)
            vertex.writeToBuffer(fb);
        fb.position(0);

        IntBuffer buffer = IntBuffer.allocate(Integer.BYTES);
        glGenBuffers(1 , buffer);
        id = buffer.get();

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, fb.capacity() * Float.BYTES, fb, GL_STATIC_DRAW);

        addLayout(Vertex.getLayout());
    }

    public VertexBuffer(int shaderID, int maxVertexCount){
        this.shaderID = shaderID;

        IntBuffer buffer = IntBuffer.allocate(Integer.BYTES);
        glGenBuffers(1 , buffer);
        id = buffer.get();

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, maxVertexCount * Vertex.size, null, GL_DYNAMIC_DRAW);

        addLayout(Vertex.getLayout());
    }

    public void fillBuffer(ArrayList<Vertex> vertices){
        ByteBuffer vbb = ByteBuffer.allocateDirect(Vertex.size * vertices.size());
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = vbb.asFloatBuffer();
        for (Vertex vertex: vertices)
            vertex.writeToBuffer(fb);
        fb.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices.size() * Vertex.size, fb);
    }

    private void addLayout(VertexBufferLayout[] layouts)
    {
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

    public void unbind(){
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
