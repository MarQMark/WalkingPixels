package com.game.walkingpixels.controller;

import android.content.Context;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.MapVertex;
import com.game.walkingpixels.util.meshbuilder.MapMeshBuilder;
import com.game.walkingpixels.util.vector.Vector3;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class MapRenderer extends Renderer{

    private final MapMeshBuilder mapMeshBuilder = new MapMeshBuilder();

    public MapRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 0.0f, -1.0f));

        registerShader("map", new Shader(context, "Shaders/Map.shaders"));
        registerBatch("map", new Batch(shader("map").getID(), 20000, MapVertex.size, MapVertex.getLayout()));
        batch("map").addVertices("Grid", mapMeshBuilder.generateMesh(GameState.world,32, 64));
        batch("map").addTexture(new Texture(context, "textures/map_texture_atlas.png", 0));

        shader("map").bind();
        shader("map").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void render(double dt) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader("map").bind();
        batch("map").bind();
        batch("map").draw();
    }
}
