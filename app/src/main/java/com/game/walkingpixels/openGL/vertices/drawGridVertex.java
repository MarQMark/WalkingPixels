package com.game.walkingpixels.openGL.vertices;

import com.game.walkingpixels.openGL.VertexBufferLayout;
import com.game.walkingpixels.util.DataType;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;

public class drawGridVertex implements IVertex{

    public static final int size = DataType.FloatBYTES * (3 + 4);

    public float[] position; //vec2 - x, y
    public float[] color;   //vec4 - r, g, b, a

    public drawGridVertex(float[] position, float[] color){
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
