package com.game.walkingpixels.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.meshbuilder.MobMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.BlockMeshBuilder;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class WalkingRenderer extends Renderer{

    private World world = new World(54216.709022936559375);

    private Background background;

    private final float sunMaxHeight = 50.0f;
    private final Vector3 sunPosition = new Vector3(0.0f, sunMaxHeight, 0.0f);
    private final Vector4 clearColor = new Vector4(0.4f, 0.6f, 1.0f, 1.0f);


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
        lightManager("walk").createPointLight(sunPosition, new Vector4(1f, 1f, 1f, 1.0f), 1000f, camera);
        //lightManager("walk").createPointLight(new Vector3(2.0f, 5.0f, 0.0f), new Vector4(1f, 1f, 1f, 1.0f), 8f, camera);


        //init world
        shader("walk").bind();
        registerBatch("walk", new Batch(shader("walk").getID(), 2000, WorldVertex.size, WorldVertex.getLayout()));
        batch("walk").addVertices("Player", MobMeshBuilder.generateMesh(world, camera, false));
        batch("walk").addVertices("World", BlockMeshBuilder.generateMesh(world));
        batch("walk").addTexture(new Texture(context, "textures/texture_atlas.png", 0));
        batch("walk").addTexture(new Texture(context, "textures/christina.png", 1));
        batch("walk").addTexture(new Texture(context, "textures/slime.png", 2));
        batch("walk").addTexture(new Texture(context, "textures/bonfire.png", 3));

        shader("walk").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);
    }

    @Override
    public void update(double dt) {
        //update in-game time (1 day = 24 min)
        float sunRotation = (float) (System.currentTimeMillis() / 4000.0) % 360;

        //update camera rotation
        world.setDirection((int) camera.rotationY);

        //update sun
        sunPosition.z = (float) (Math.cos(Math.toRadians(sunRotation)) * sunMaxHeight);
        sunPosition.y = (float) (Math.sin(Math.toRadians(sunRotation)) * sunMaxHeight);
        lightManager("walk").setLightPosition(0, sunPosition);

        //update background
        background.update(dt / 1000, width, height);
        batch("background").updateVertices("Background", background.getVertices());

        //update player rotation
        batch("walk").updateVertices("Player", MobMeshBuilder.generateMesh(world, camera, false));

        //move world
        if(world.hasMoved())
            batch("walk").updateVertices("World" , BlockMeshBuilder.generateMesh(world));
    }

    @Override
    public void render(double dt) {
        //calculate sun shadow
        lightManager("walk").calculateShadow(new Batch[]{batch("walk")}, width, height);

        //set background color according to the time
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

    public boolean moveForward(){
        return world.forward();
    }
    public void removeEnemy(){
        world.removeEnemy();
    }
    public Enemy checkForEnemy(){
        return world.checkForEnemy();
    }
    public boolean checkForBonfire(){
        return world.checkForBonfire();
    }
    public void respawn(Vector2 bonfire){
        world.setPosition((int)bonfire.x, (int)bonfire.y);
    }
}
