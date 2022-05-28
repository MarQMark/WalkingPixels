package com.game.walkingpixels;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.renderscript.Matrix4f;
import android.util.Log;

import com.game.walkingpixels.model.DrawGrid;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.drawGridVertex;
import com.game.walkingpixels.openGL.vertices.worldVertex;
import com.game.walkingpixels.util.meshbuilder.DrawGridMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.MobMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.WorldMeshBuilder;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES31.*;


public class GLRenderer implements GLSurfaceView.Renderer {

    public enum Scene{
        DRAWING,
        WALKING,
        MAP
    }

    private final Scene scene;


    private final Context context;

    private Batch walkingBatch;
    private Shader walkingShader;
    private Batch drawingBatch;
    private Shader drawingShader;

    public DrawGrid drawGrid;

    static World world = new World(54216.709022936559375);

    private final float lightMaxHeight = 50.0f;
    private float lightRotation = 0;
    private final Vector3 lightPosition = new Vector3(0.0f, lightMaxHeight, 0.0f);

    private int width;
    private int height;


    public GLRenderer(Context context, Scene scene){
        this.context = context;
        this.scene = scene;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        printOpenGLInfo(gl);

        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        createWalkingScene();
        createDrawingScene();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
       drawWalkingScene();
       drawDrawingScene();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        Camera.setAspectRatio((float) width / (float)height);
    }

    private void createWalkingScene(){
        SharedPreferences sharedPref = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("shadowOn", false))
            walkingShader = new Shader(context, "Shaders/BasicShadow.shaders");
        else
            walkingShader = new Shader(context, "Shaders/Basic.shaders");

        LightManager.initShader(new Shader(context, "Shaders/ShadowGeometry.shaders"));
        LightManager.initWorldShader(walkingShader);
        LightManager.createPointLight(lightPosition, new Vector4(1f, 1f, 1f, 1.0f), 1000f);
        //LightManager.createPointLight(new Vector3(2.0f, 5.0f, 0.0f), new Vector4(1f, 1f, 1f, 1.0f), 8f);

        walkingShader.bind();

        walkingBatch = new Batch(walkingShader.getID(), 20000, worldVertex.size, worldVertex.getLayout());
        walkingBatch.addVertices("Player", MobMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        walkingBatch.addVertices("World", WorldMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        walkingBatch.bind();

        Texture tx = new Texture(context, "textures/texture_atlas.png", 0);
        tx.bind(0);
        Texture tx2 = new Texture(context, "textures/christina.png", 1);
        tx2.bind(1);

        walkingShader.setUniform1iv("u_Textures", 2, new int[] {0, 1}, 0);
    }

    private void drawWalkingScene(){
        walkingShader.bind();
        walkingShader.setUniformMatrix4fv("mvpmatrix", Camera.getMVPMatrix());

        //update sun
        lightRotation++;
        lightPosition.z = (float) (Math.cos(Math.toRadians(lightRotation)) * lightMaxHeight);
        lightPosition.y = (float) (Math.sin(Math.toRadians(lightRotation)) * lightMaxHeight);
        LightManager.setLightPosition(0, lightPosition);


        //calculate sun
        LightManager.calculateShadow(new Batch[]{walkingBatch}, width, height);

        //draw
        walkingShader.bind();
        walkingBatch.bind();
        glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        walkingBatch.draw();

        //move world
        //world.movePlayerPosition(1, 0);
        walkingBatch.updateVertices("Player", MobMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        //walkingBatch.updateVertices("World" , WorldMeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        //walkingBatch.bind();
    }

    private void createDrawingScene(){
        drawGrid = new DrawGrid(8);

        drawingShader = new Shader(context, "Shaders/DrawGrid.shaders");
        drawingShader.bind();

        drawingBatch = new Batch(drawingShader.getID(), drawGrid.getSize() * drawGrid.getSize(), drawGridVertex.size, drawGridVertex.getLayout());
        drawingBatch.addVertices("Grid", DrawGridMeshBuilder.generateMesh(drawGrid));
    }

    private void drawDrawingScene(){

        Matrix4f view = new Matrix4f();
        view.loadIdentity();
        view.translate(-0.9f, -0.9f, 0.0f);
        float scale = 1.8f * (1.0f / drawGrid.getSize());
        view.scale(scale,  scale * (width / (float)height), 1.0f);

        drawingShader.bind();

        drawingShader.setUniformMatrix4fv("viewmatrix", view);

        drawingBatch.bind();
        //glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
        //glClear(GL_COLOR_BUFFER_BIT);
        drawingBatch.draw();
    }

    private void printOpenGLInfo(GL10 gl){
        String version = gl.glGetString(GL10.GL_VERSION);
        System.out.println("[INFO] OpenGL Version: " + version);

        int[] maxTexture = new int[1];
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTexture, 0);
        System.out.println("[INFO] Maximum image units " + maxTexture[0]);
    }

    public static void queryErrors(String location){
        int error;
        while((error = glGetError()) != 0)
            Log.e("OpenGL Error", location + " - 0x " + Integer.toHexString(error));
    }
}
