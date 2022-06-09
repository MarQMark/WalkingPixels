package com.game.walkingpixels.controller;

import android.content.Context;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class MapBackgroundRenderer extends Renderer{

    private Background background;

    private final float sunMaxHeight = 50.0f;
    private final Vector3 sunPosition = new Vector3(0.0f, sunMaxHeight, 0.0f);
    private final Vector4 clearColor = new Vector4(0.4f, 0.6f, 1.0f, 1.0f);

    public MapBackgroundRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 0.0f, -1.0f));

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
        //update in-game time (1 day = 24 min)
        float sunRotation = (float) (System.currentTimeMillis() / 4000.0) % 360;

        //update sun
        sunPosition.z = (float) (Math.cos(Math.toRadians(sunRotation)) * sunMaxHeight);
        sunPosition.y = (float) (Math.sin(Math.toRadians(sunRotation)) * sunMaxHeight);

        //update background
        background.update(dt / 1000, width, height);
        batch("background").updateVertices("Background", background.getVertices());
    }

    @Override
    public void render(double dt) {
        float diffuse = 0.0f;
        Vector4 timeClearColor = new Vector4(clearColor);
        if(sunPosition.y > 0){
            Vector3 nLightPosition = new Vector3(sunPosition);
            nLightPosition.normalize();
            diffuse = nLightPosition.dot(new Vector3(0.0f, 1.0f, 0.0f));
            diffuse = Math.max(diffuse, 0.0f);
        }
        timeClearColor.scale(diffuse + 0.2f);

        glClearColor(timeClearColor.x, timeClearColor.y, timeClearColor.z, timeClearColor.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //render background
        shader("background").bind();
        batch("background").bind();
        batch("background").draw();
    }
}
