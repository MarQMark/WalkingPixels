package com.game.walkingpixels.controller;

import android.content.Context;

import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.model.Sun;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class BackgroundRenderer extends Renderer{

    private Background background;

    private final Sun sun = new Sun();

    public BackgroundRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        registerShader("background", new Shader(context, "Shaders/Background.shaders"));
        background = new Background(new Texture(context, "textures/clouds.png", 0), 20);
        shader("background").bind();
        shader("background").setUniform1iv("u_Textures", 1, new int[] {0}, 0);
        registerBatch("background", new Batch(shader("background").getID(), 1, PlaneVertex.size, PlaneVertex.getLayout()));
        batch("background").addVertices("Background", background.getVertices());
        batch("background").addTexture(background.getTexture());
    }

    @Override
    public void update(double dt) {
        background.update(dt / 1000, width, height);
        batch("background").updateVertices("Background", background.getVertices());
    }

    @Override
    public void render(double dt) {
        Vector4 clearColor = sun.getColor();
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader("background").bind();
        batch("background").bind();
        batch("background").draw();
    }
}
