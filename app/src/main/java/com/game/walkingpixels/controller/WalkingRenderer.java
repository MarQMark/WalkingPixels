package com.game.walkingpixels.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
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

    private Background background;

    public WalkingRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3(0.0f, 0.0f, 12.8f), new Vector3(0.0f, 0.0f, -1.0f));
        camera.rotationX = 50;
        camera.rotationY = 45;

        //init shaders
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("shadowEnabled", true))
            registerShader("walk", new Shader(context, "Shaders/BasicShadow.shaders"));
        else
            registerShader("walk", new Shader(context, "Shaders/Basic.shaders"));
        registerShader("background", new Shader(context, "Shaders/Background.shaders"));


        //init background
        background = new Background(new Texture(context, "textures/clouds.png", 0), 20);
        shader("background").bind();
        shader("background").setUniform1iv("u_Textures", 1, new int[] {0}, 0);
        registerBatch("background", new Batch(shader("background").getID(), 1, PlaneVertex.size, PlaneVertex.getLayout()));
        batch("background").addVertices("Background", background.getVertices());
        batch("background").addTexture(background.getTexture());


        //init light
        registerLightManager("walk", new LightManager());
        lightManager("walk").initShader(new Shader(context, "Shaders/ShadowGeometry.shaders"));
        lightManager("walk").initWorldShader(shader("walk"));
        lightManager("walk").createPointLight(lightPosition, new Vector4(1f, 1f, 1f, 1.0f), 1000f, camera);
        lightManager("walk").createPointLight(new Vector3(2.0f, 5.0f, 0.0f), new Vector4(1f, 1f, 1f, 1.0f), 8f, camera);


        //init world
        shader("walk").bind();
        registerBatch("walk", new Batch(shader("walk").getID(), 2000, WorldVertex.size, WorldVertex.getLayout()));
        batch("walk").addVertices("Player", MobMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight, camera, false));
        batch("walk").addVertices("World", WorldMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch("walk").addTexture(new Texture(context, "textures/texture_atlas.png", 0));
        batch("walk").addTexture(new Texture(context, "textures/christina.png", 1));

        shader("walk").setUniform1iv("u_Textures", 2, new int[] {0, 1}, 0);
    }

    @Override
    public void update(double dt) {
        //update sun
        lightRotation++;
        lightPosition.z = (float) (Math.cos(Math.toRadians(lightRotation)) * lightMaxHeight);
        lightPosition.y = (float) (Math.sin(Math.toRadians(lightRotation)) * lightMaxHeight);
        lightManager("walk").setLightPosition(0, lightPosition);

        //update background
        background.update(dt / 1000, width, height);
        batch("background").updateVertices("Background", background.getVertices());

        //update player rotation
        batch("walk").updateVertices("Player", MobMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight, camera, false));

        //move world
        if(world.hasMoved())
            batch("walk").updateVertices("World" , WorldMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
    }

    @Override
    public void render(double dt) {
        //calculate sun shadow
        lightManager("walk").calculateShadow(new Batch[]{batch("walk")}, width, height);

        glClearColor(0.4f, 0.6f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        //render background
        shader("background").bind();
        batch("background").bind();
        batch("background").draw();

        glClear(GL_DEPTH_BUFFER_BIT);
        //render world
        shader("walk").bind();
        shader("walk").setUniformMatrix4fv("mvpmatrix", camera.getMVPMatrix());
        batch("walk").bind();
        batch("walk").draw();
    }
}
