package com.game.walkingpixels.openGL;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_SHORT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;

public class VertexBufferLayout {
    private final String attributeName;
    private final int size;
    private final int type;
    private final boolean normalized;
    private int stride;
    private int offset;

    private int dataSize;

    public VertexBufferLayout(String attributeName, int size, int type, boolean normalized){
        this.attributeName = attributeName;
        this.size = size;
        this.type = type;
        this.normalized = normalized;

        switch (type){
            case GL_FLOAT: dataSize = 4; break;
            case GL_SHORT: dataSize = 2; break;
        }
    }

    public VertexBufferLayout(VertexBufferLayout layout){
        this.attributeName = layout.attributeName;
        this.size = layout.size;
        this.type = layout.type;
        this.normalized = layout.normalized;
        this.stride = layout.stride;
        this.offset = layout.offset;
        this.dataSize = layout.dataSize;
    }

    public void loadLayout(int program){
        int handle = glGetAttribLocation(program, attributeName);
        if(handle == -1)System.out.println("[ERROR] " + attributeName + " not found");
        glEnableVertexAttribArray(handle);
        glVertexAttribPointer(handle, size, type, normalized, stride, offset);
    }

    public int getSize() {return size;}
    public int getDataSize() {return dataSize;}
    public void setStride(int stride) { this.stride = stride;}
    public void setOffset(int offset) { this.offset = offset;}
}
