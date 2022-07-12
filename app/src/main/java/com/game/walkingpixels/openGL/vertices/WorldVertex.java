package com.game.walkingpixels.openGL.vertices;

import com.game.walkingpixels.openGL.buffer.VertexBufferLayout;
import com.game.walkingpixels.util.DataType;

import java.nio.FloatBuffer;

import static android.opengl.GLES31.GL_FLOAT;

public class WorldVertex implements IVertex {
    public static final int SIZE = DataType.FLOAT_BYTES * (3 + 2 + 3 + 4 + 1);

    private final float[] position; //vec2 - x, y
    private final float[] texPosition;
    private final float[] normal;
    private final float[] color;   //vec4 - r, g, b, a
    private final float textureSlot;

    public WorldVertex(float[] position, float[] texPosition, float[] normal, float[] color, float textureSlot){
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

    public float getTextureSlot() {
        return textureSlot;
    }
}
