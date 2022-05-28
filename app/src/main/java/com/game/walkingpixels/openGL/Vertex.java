package com.game.walkingpixels.openGL;

import com.game.walkingpixels.openGL.VertexBufferLayout;

import java.nio.FloatBuffer;

import static android.opengl.GLES31.GL_FLOAT;

public class Vertex {
    public static final int size = Float.BYTES * (3 + 2 + 3 + 4 + 1);

    public float[] position; //vec2 - x, y
    public float[] texPosition;
    public float[] normal;
    public float[] color;   //vec4 - r, g, b, a
    public float textureSlot;

    public Vertex(float[] position, float[] texPosition, float[] normal,float[] color, float textureSlot){
        this.position = position;
        this.texPosition = texPosition;
        this.normal = normal;
        this.color = color;
        this.textureSlot = textureSlot;
    }

    public void writeToBuffer(FloatBuffer buffer){
        buffer.put(position);
        buffer.put(texPosition);
        buffer.put(normal);
        buffer.put(color);
        buffer.put(textureSlot);
    }

    public static VertexBufferLayout[] getLayout(){
        VertexBufferLayout[] layout = {
                new VertexBufferLayout("a_Position", 3, GL_FLOAT, false),
                new VertexBufferLayout("a_TexCoord", 2, GL_FLOAT, false),
                new VertexBufferLayout("a_Normal", 3, GL_FLOAT, false),
                new VertexBufferLayout("a_Color", 4, GL_FLOAT, false),
                new VertexBufferLayout("a_TextureSlot", 1, GL_FLOAT, false),
        };
        return layout;
    }
}
