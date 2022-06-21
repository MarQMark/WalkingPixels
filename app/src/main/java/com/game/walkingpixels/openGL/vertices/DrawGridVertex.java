package com.game.walkingpixels.openGL.vertices;

import com.game.walkingpixels.openGL.buffer.VertexBufferLayout;
import com.game.walkingpixels.util.DataType;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;

public class DrawGridVertex implements IVertex{

    public static final int SIZE = DataType.FLOAT_BYTES * (3 + 4);

    private final float[] position; //vec2 - x, y
    private final float[] color;   //vec4 - r, g, b, a

    public DrawGridVertex(float[] position, float[] color){
        this.position = position;
        this.color = color;
    }

    @Override
    public void writeToBuffer(FloatBuffer buffer) {
        buffer.put(position);
        buffer.put(color);
    }

    public static VertexBufferLayout[] getLayout() {
        return new VertexBufferLayout[]{
                new VertexBufferLayout("a_Position", 3, GL_FLOAT, false),
                new VertexBufferLayout("a_Color", 4, GL_FLOAT, false),
        };
    }
}
