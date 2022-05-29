package com.game.walkingpixels.openGL.vertices;

import com.game.walkingpixels.openGL.buffer.VertexBufferLayout;
import com.game.walkingpixels.util.DataType;

import java.nio.FloatBuffer;

import static android.opengl.GLES31.GL_FLOAT;

public class worldVertex implements IVertex {
    public static final int size = DataType.FloatBYTES * (3 + 2 + 3 + 4 + 1);

    public float[] position; //vec2 - x, y
    public float[] texPosition;
    public float[] normal;
    public float[] color;   //vec4 - r, g, b, a
    public float textureSlot;

    public worldVertex(float[] position, float[] texPosition, float[] normal, float[] color, float textureSlot){
        this.position = position;
        this.texPosition = texPosition;
        this.normal = normal;
        this.color = color;
        this.textureSlot = textureSlot;
    }

    @Override
    public void writeToBuffer(FloatBuffer buffer) {
        buffer.put(position);
        buffer.put(texPosition);
        buffer.put(normal);
        buffer.put(color);
        buffer.put(textureSlot);
    }

    public static VertexBufferLayout[] getLayout() {
        return new VertexBufferLayout[]{
                new VertexBufferLayout("a_Position", 3, GL_FLOAT, false),
                new VertexBufferLayout("a_TexCoord", 2, GL_FLOAT, false),
                new VertexBufferLayout("a_Normal", 3, GL_FLOAT, false),
                new VertexBufferLayout("a_Color", 4, GL_FLOAT, false),
                new VertexBufferLayout("a_TextureSlot", 1, GL_FLOAT, false),
        };
    }
}
