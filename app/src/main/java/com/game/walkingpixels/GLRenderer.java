package com.game.walkingpixels;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.util.MobMeshBuilder;
import com.game.walkingpixels.util.WorldMeshBuilder;
import com.game.walkingpixels.util.Vector3;
import com.game.walkingpixels.util.Vector4;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES31.*;


public class GLRenderer implements GLSurfaceView.Renderer {

    Context context;
    Batch batch;
    Shader shader;

    static World world = new World(54216.709022936559375);

    private final float lightMaxHeight = 50.0f;
    private float lightRotation = 0;
    private final Vector3 lightPosition = new Vector3(0.0f, lightMaxHeight, 0.0f);

    private int width;
    private int height;

    Texture tx;
    Texture tx2;

    public GLRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        String version = gl.glGetString(GL10.GL_VERSION);
        System.out.println("[INFO] OpenGL Version: " + version);

        int[] maxTexture = new int[1];
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTexture, 0);
        System.out.println("[INFO] Maximum image units " + maxTexture[0]);

        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);


        SharedPreferences sharedPref = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("shadowOn", false))
            shader = new Shader(context, "Shaders/BasicShadow.shaders");
        else
            shader = new Shader(context, "Shaders/Basic.shaders");

        LightManager.initShader(new Shader(context, "Shaders/ShadowGeometry.shaders"));
        LightManager.initWorldShader(shader);
        LightManager.createPointLight(lightPosition, new Vector4(1f, 1f, 1f, 1.0f), 1000f);
        //LightManager.createPointLight(new Vector3(2.0f, 5.0f, 0.0f), new Vector4(1f, 1f, 1f, 1.0f), 8f);

        shader.bind();

        batch = new Batch(shader.getID(), 20000);
        batch.addVertices("Player", MobMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch.addVertices("World", WorldMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch.bind();

        tx = new Texture(context, "textures/texture_atlas.png", 0);
        tx.bind(0);
        tx2 = new Texture(context, "textures/christina.png", 1);
        tx2.bind(1);

        shader.setUniform1iv("u_Textures", 2, new int[] {0, 1}, 0);
        LightManager.shader.bind();
        LightManager.shader.setUniform1iv("u_Textures", 2, new int[] {0, 1}, 0);

        shader.bind();
    }

    @Override
    public void onDrawFrame(GL10 gl) {


        shader.setUniformMatrix4fv("mvpmatrix", Camera.getMVPMatrix());
        shader.setUniformMatrix4fv("mpmatrix", Camera.getMPMatrix());

        //update sun
        lightRotation++;
        lightPosition.z = (float) (Math.cos(Math.toRadians(lightRotation)) * lightMaxHeight);
        lightPosition.y = (float) (Math.sin(Math.toRadians(lightRotation)) * lightMaxHeight);
        LightManager.setLightPosition(0, lightPosition);


        //calculate sun
        LightManager.calculateShadow(new Batch[]{ batch }, width, height);

        //draw
        shader.bind();
        batch.bind();
        glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        batch.draw();

        //move world
        //world.movePlayerPosition(1, 0);
        batch.updateVertices("Player", MobMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        //batch.updateVertices("World" , WorldMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        //batch.bind();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        Camera.setAspectRatio((float) width / (float)height);
    }

    public static void queryErrors(String location){
        int error;
        while((error = glGetError()) != 0)
            Log.e("OpenGL Error", location + " - 0x " + Integer.toHexString(error));
    }
}
