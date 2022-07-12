package com.game.walkingpixels.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.model.Camera;
import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.model.MainWorld;
import com.game.walkingpixels.model.Sun;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.meshbuilder.SpriteMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.BlockMeshBuilder;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

public class WalkingRenderer extends Renderer{

    private Background background;

    private final Sun sun = new Sun();

    private final SpriteMeshBuilder spriteMeshBuilder = new SpriteMeshBuilder();
    private final BlockMeshBuilder blockMeshBuilder = new BlockMeshBuilder();

    private boolean shadow;

    public WalkingRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        //init camera
        camera = new Camera(new Vector3(0.0f, 0.0f, 12.8f), new Vector3(0.0f, 0.0f, -1.0f));
        camera.rotationX = 50;
        SharedPreferences sharedPreferences = context.getSharedPreferences("World", Context.MODE_PRIVATE);
        camera.rotationY = sharedPreferences.getFloat("rotation", 0);

        //init shaders
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        shadow = sharedPref.getBoolean("shadow_enabled", false);
        if(shadow)
            registerShader("walk", new Shader(context, "Shaders/BasicShadow.shaders"));
        else
            registerShader("walk", new Shader(context, "Shaders/Basic.shaders"));
        registerShader("background", new Shader(context, "Shaders/Background.shaders"));

        //init background
        background = new Background(new Texture(context, "textures/clouds.png", 0), 20);
        shader("background").bind();
        shader("background").setUniform1iv("u_Textures", 1, new int[] {0}, 0);
        registerBatch("background", new Batch(shader("background").getID(), 1, PlaneVertex.SIZE, PlaneVertex.getLayout()));
        batch("background").addVertices("Background", background.getVertices());
        batch("background").addTexture(background.getTexture());

        //init light
        registerLightManager("walk", new LightManager());
        queryErrors("After Light - init");
        lightManager("walk").initShader(new Shader(context, "Shaders/ShadowGeometry.shaders"));
        queryErrors("After Light - init shader");
        lightManager("walk").initWorldShader(shader("walk"));
        queryErrors("After Light - init world shader");
        lightManager("walk").createPointLight(sun.getPosition(), new Vector4(1f, 1f, 1f, 1.0f), 1000f, camera);

        //init world
        shader("walk").bind();
        registerBatch("walk", new Batch(shader("walk").getID(), 2000, WorldVertex.SIZE, WorldVertex.getLayout()));
        batch("walk").addVertices("Sprites", spriteMeshBuilder.generateMesh(MainWorld.getWorld(), camera, false, !shadow));
        batch("walk").addVertices("World", blockMeshBuilder.generateMesh(MainWorld.getWorld()));
        batch("walk").addTexture(new Texture(context, "textures/block_atlas.png", 0));
        batch("walk").addTexture(new Texture(context, "textures/mob_texture_atlas.png", 1));
        batch("walk").addTexture(new Texture(context, "textures/tree.png", 2));

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

        //update rotations
        batch("walk").updateVertices("Sprites", spriteMeshBuilder.generateMesh(MainWorld.getWorld(), camera, false, !shadow));

        //move world
        if(MainWorld.getWorld().hasMoved())
            batch("walk").updateVertices("World" , blockMeshBuilder.generateMesh(MainWorld.getWorld()));
    }

    @Override
    public void render(double dt) {
        //calculate sun shadow
        queryErrors("Before Shadow");
        lightManager("walk").calculateShadow(new Batch[]{batch("walk")}, width, height);
        queryErrors("After Shadow");

        //set background color according to the time
        Vector4 clearColor = sun.getColor();
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        glClear(GL_COLOR_BUFFER_BIT);

        //render background
        glDisable(GL_DEPTH_TEST);
        shader("background").bind();
        batch("background").bind();
        batch("background").draw();
        glEnable(GL_DEPTH_TEST);

        glClear(GL_DEPTH_BUFFER_BIT);
        //render world
        shader("walk").bind();
        shader("walk").setUniformMatrix4fv("mvpmatrix", camera.getMVPMatrix());
        batch("walk").bind();
        batch("walk").draw();

        queryErrors("After Render");
    }
}
