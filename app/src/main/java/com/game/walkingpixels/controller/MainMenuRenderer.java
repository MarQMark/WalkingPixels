package com.game.walkingpixels.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.MainWorld;
import com.game.walkingpixels.model.Sun;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.meshbuilder.BlockMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.MobMeshBuilder;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class MainMenuRenderer extends Renderer{

    private World world;

    private final Sun sun = new Sun();
    private Background background;

    private final MobMeshBuilder mobMeshBuilder = new MobMeshBuilder();
    private final BlockMeshBuilder blockMeshBuilder = new BlockMeshBuilder();

    public MainMenuRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3(0.0f, 0.0f, 12.8f), new Vector3(0.0f, 0.0f, -1.0f));
        camera.rotationX = 50;
        camera.rotationY =  0;

        world = new World(MainWorld.getWorld().getSeed());
        world.getBlockGrid()
                [world.getBlockGridSize() / 2]
                [world.getBlockGridSize() / 2]
                [world.generateHeight(world.getBlockGridSize() / 2, world.getBlockGridSize() / 2) + 1]
                = Block.AIR;

        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("shadowEnabled", true))
            registerShader("world", new Shader(context, "Shaders/BasicShadow.shaders"));
        else
            registerShader("world", new Shader(context, "Shaders/Basic.shaders"));
        registerShader("background", new Shader(context, "Shaders/Background.shaders"));

        //init background
        background = new Background(new Texture(context, "textures/clouds.png", 0), 20);
        shader("background").bind();
        shader("background").setUniform1iv("u_Textures", 1, new int[] {0}, 0);
        registerBatch("background", new Batch(shader("background").getID(), 1, PlaneVertex.size, PlaneVertex.getLayout()));
        batch("background").addVertices("Background", background.getVertices());
        batch("background").addTexture(background.getTexture());

        //init light
        registerLightManager("world", new LightManager());
        lightManager("world").initShader(new Shader(context, "Shaders/ShadowGeometry.shaders"));
        lightManager("world").initWorldShader(shader("world"));
        lightManager("world").createPointLight(sun.getPosition(), new Vector4(1f, 1f, 1f, 1.0f), 1000f, camera);

        //init world
        shader("world").bind();
        registerBatch("world", new Batch(shader("world").getID(), 2000, WorldVertex.size, WorldVertex.getLayout()));
        batch("world").addVertices("Mobs", mobMeshBuilder.generateMesh(world, camera, false));
        batch("world").addVertices("World", blockMeshBuilder.generateMesh(world));
        batch("world").addTexture(new Texture(context, "textures/texture_atlas.png", 0));
        batch("world").addTexture(new Texture(context, "textures/mob_texture_atlas.png", 1));
        batch("world").addTexture(new Texture(context, "textures/tree.png", 2));
        batch("world").addTexture(new Texture(context, "textures/bonfire.png", 3));

        shader("world").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);
    }

    @Override
    public void update(double dt) {
        camera.rotationY += dt / 100;

        //update sun
        lightManager("world").setLightPosition(0, sun.getPosition());

        //update background
        background.update(dt / 1000, width, height);
        batch("background").updateVertices("Background", background.getVertices());

        //update player rotation
        batch("world").updateVertices("Mobs", mobMeshBuilder.generateMesh(world, camera, false));
    }

    @Override
    public void render(double dt) {
        //calculate sun shadow
        lightManager("world").calculateShadow(new Batch[]{batch("world")}, width, height);

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
        shader("world").bind();
        shader("world").setUniformMatrix4fv("mvpmatrix", camera.getMVPMatrix());
        batch("world").bind();
        batch("world").draw();
    }
}
