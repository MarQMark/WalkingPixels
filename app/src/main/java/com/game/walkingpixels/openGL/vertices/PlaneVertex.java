package com.game.walkingpixels.openGL.vertices;

import com.game.walkingpixels.openGL.buffer.VertexBufferLayout;
import com.game.walkingpixels.util.DataType;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;

public class PlaneVertex implements IVertex{

    public static final int size = DataType.FloatBYTES * (3 + 2 + 1);

    public float[] position; //vec2 - x, y
    public float[] texPosition;
    public float textureSlot;

    public PlaneVertex(float[] position, float[] texPosition, float textureSlot){
        this.position = position;
        this.texPosition = texPosition;
        this.textureSlot = textureSlot;
    }

    @Override
    public void writeToBuffer(FloatBuffer buffer) {
        buffer.put(position);
        buffer.put(texPosition);
        buffer.put(textureSlot);
    }

    public static VertexBufferLayout[] getLayout() {
        return new VertexBufferLayout[]{
                new VertexBufferLayout("a_Position", 3, GL_FLOAT, false),
                new VertexBufferLayout("a_TexCoord", 2, GL_FLOAT, false),
                new VertexBufferLayout("a_TextureSlot", 1, GL_FLOAT, false),
        };
    }
}
