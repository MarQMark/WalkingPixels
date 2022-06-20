package com.game.walkingpixels.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.model.MainWorld;
import com.game.walkingpixels.model.Sun;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.meshbuilder.MobMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.BlockMeshBuilder;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class WalkingRenderer extends Renderer{

    private Background background;

    private final Sun sun = new Sun();

    private final MobMeshBuilder mobMeshBuilder = new MobMeshBuilder();
    private final BlockMeshBuilder blockMeshBuilder = new BlockMeshBuilder();


    public WalkingRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3(0.0f, 0.0f, 12.8f), new Vector3(0.0f, 0.0f, -1.0f));
        camera.rotationX = 50;
        SharedPreferences sharedPreferences = context.getSharedPreferences("World", Context.MODE_PRIVATE);
        camera.rotationY = sharedPreferences.getFloat("rotation", 0);

        //init shaders
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("shadow_enabled", false))
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
        lightManager("walk").createPointLight(sun.getPosition(), new Vector4(1f, 1f, 1f, 1.0f), 1000f, camera);


        //init world
        shader("walk").bind();
        registerBatch("walk", new Batch(shader("walk").getID(), 2000, WorldVertex.size, WorldVertex.getLayout()));
        batch("walk").addVertices("Player", mobMeshBuilder.generateMesh(MainWorld.getWorld(), camera, false));
        batch("walk").addVertices("World", blockMeshBuilder.generateMesh(MainWorld.getWorld()));
        batch("walk").addTexture(new Texture(context, "textures/block_atlas.png", 0));
        batch("walk").addTexture(new Texture(context, "textures/mob_texture_atlas.png", 1));
        batch("walk").addTexture(new Texture(context, "textures/tree.png", 2));
        batch("walk").addTexture(new Texture(context, "textures/bonfire.png", 3));

        shader("walk").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);
    }

    @Override
    public void update(double dt) {
        //update camera rotation
        MainWorld.getWorld().setDirection((int) camera.rotationY);

        //update sun
        lightManager("walk").setLightPosition(0, sun.getPosition());

        //update background
        background.update(dt / 1000, width, height);
        batch("background").updateVertices("Background", background.getVertices());

        //update player rotation
        batch("walk").updateVertices("Player", mobMeshBuilder.generateMesh(MainWorld.getWorld(), camera, false));

        //move world
        if(MainWorld.getWorld().hasMoved())
            batch("walk").updateVertices("World" , blockMeshBuilder.generateMesh(MainWorld.getWorld()));
    }

    @Override
    public void render(double dt) {
        //calculate sun shadow
        lightManager("walk").calculateShadow(new Batch[]{batch("walk")}, width, height);

        //set background color according to the time
        Vector4 clearColor = sun.getColor();
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
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
