package com.game.walkingpixels.openGL.buffer;

import com.game.walkingpixels.util.DataType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES31.*;

public class IndexBuffer {

    private final int id;

    public IndexBuffer(short[] indices){

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * DataType.ShortBYTES);
        ibb.order(ByteOrder.nativeOrder());
        ShortBuffer indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        IntBuffer buffers = IntBuffer.allocate(DataType.IntegerBYTES);
        glGenBuffers(1 , buffers);
        id = buffers.get();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * DataType.ShortBYTES, indexBuffer, GL_STATIC_DRAW);
    }

    public void bind(){
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    public void unbind(){
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void destroy(){
        glDeleteBuffers(1, new int[]{id}, 0);
    }
}
