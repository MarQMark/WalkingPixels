package com.game.walkingpixels.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.worldVertex;
import com.game.walkingpixels.util.meshbuilder.MobMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.WorldMeshBuilder;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class WalkingRenderer extends Renderer{

    public static World world = new World(54216.709022936559375);

    private final float lightMaxHeight = 50.0f;
    private float lightRotation = 0;
    private final Vector3 lightPosition = new Vector3(0.0f, lightMaxHeight, 0.0f);

    public WalkingRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        SharedPreferences sharedPref = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("shadowOn", true))
            registerShader("walk", new Shader(context, "Shaders/BasicShadow.shaders"));
        else
            registerShader("walk", new Shader(context, "Shaders/Basic.shaders"));

        registerLightManager("walk", new LightManager());
        lightManager("walk").initShader(new Shader(context, "Shaders/ShadowGeometry.shaders"));
        lightManager("walk").initWorldShader(shader("walk"));
        lightManager("walk").createPointLight(lightPosition, new Vector4(1f, 1f, 1f, 1.0f), 1000f);
        lightManager("walk").createPointLight(new Vector3(2.0f, 5.0f, 0.0f), new Vector4(1f, 1f, 1f, 1.0f), 8f);

        shader("walk").bind();

        registerBatch("walk", new Batch(shader("walk").getID(), 20000, worldVertex.size, worldVertex.getLayout()));
        batch("walk").addVertices("Player", MobMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch("walk").addVertices("World", WorldMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch("walk").bind();

        Texture tx = new Texture(context, "textures/texture_atlas.png", 0);
        tx.bind(0);
        Texture tx2 = new Texture(context, "textures/christina.png", 1);
        tx2.bind(1);

        shader("walk").setUniform1iv("u_Textures", 2, new int[] {0, 1}, 0);
    }

    @Override
    public void update(double dt) {
        shader("walk").bind();
        shader("walk").setUniformMatrix4fv("mvpmatrix", Camera.getMVPMatrix());

        //update sun
        lightRotation++;
        lightPosition.z = (float) (Math.cos(Math.toRadians(lightRotation)) * lightMaxHeight);
        lightPosition.y = (float) (Math.sin(Math.toRadians(lightRotation)) * lightMaxHeight);
        lightManager("walk").setLightPosition(0, lightPosition);
    }

    @Override
    public void render(double dt) {
        //calculate sun
        lightManager("walk").calculateShadow(new Batch[]{batch("walk")}, width, height);

        //draw
        shader("walk").bind();
        batch("walk").bind();
        glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        batch("walk").draw();

        //move world
        world.movePlayerPosition(1, 0);
        batch("walk").updateVertices("Player", MobMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch("walk").updateVertices("World" , WorldMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
    }
}
