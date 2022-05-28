package com.game.walkingpixels.openGL.vertices;

import com.game.walkingpixels.openGL.VertexBufferLayout;

import java.nio.FloatBuffer;

public interface IVertex {
    void writeToBuffer(FloatBuffer buffer);
}
