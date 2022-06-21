package com.game.walkingpixels.openGL.vertices;

import com.game.walkingpixels.openGL.buffer.VertexBufferLayout;
import com.game.walkingpixels.util.DataType;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;

public class MapVertex implements IVertex{

    public static final int SIZE = DataType.FLOAT_BYTES * (2 + 2 + 1);

    private final float[] position; //vec2 - x, y
    private final float[] texPosition;  //2
    private final float textureSlot;

    public MapVertex(float[] position, float[] texPosition, float textureSlot){
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
                new VertexBufferLayout("a_Position", 2, GL_FLOAT, false),
                new VertexBufferLayout("a_TexCoord", 2, GL_FLOAT, false),
                new VertexBufferLayout("a_TextureSlot", 1, GL_FLOAT, false),
        };
    }
}
