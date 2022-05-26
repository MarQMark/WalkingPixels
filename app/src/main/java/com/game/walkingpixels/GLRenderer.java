package com.game.walkingpixels;

import android.content.Context;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.renderscript.Matrix4f;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.PointLight;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.util.MeshBuilder;
import com.game.walkingpixels.util.Vector3;
import com.game.walkingpixels.util.Vector4;

import java.nio.IntBuffer;

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


    public GLRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        String version = gl.glGetString(GL10.GL_VERSION);
        System.out.println("[INFO] OpenGL Version: " + version);

        IntBuffer maxTexture = IntBuffer.allocate(Integer.BYTES);
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTexture);
        System.out.println("[INFO] Maximum image units " + maxTexture.get());

        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);


        shader = new Shader(context, "Shaders/Basic.shaders");

        LightManager.initShader(new Shader(context, "Shaders/ShadowGeometry.shaders"));
        LightManager.initWorldShader(shader);
        LightManager.createPointLight(lightPosition, new Vector4(1f, 1f, 1f, 1.0f), 1000f);
        LightManager.createPointLight(new Vector3(2.0f, 5.0f, 0.0f), new Vector4(1f, 1f, 1f, 1.0f), 300f);

        shader.bind();

        batch = new Batch(shader.getID(), 20000);
        batch.addVertices(MeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch.bind();

        Texture tx = new Texture(context, "textures/texture_atlas.png");
        tx.bind();

        shader.setUniform1iv("u_Textures", 2, new int[] {0, 1}, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        shader.setUniformMatrix4fv("mvpmatrix", Camera.getMatrix());

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
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        batch.draw();


        //move world
        //world.movePlayerPosition(1, 0);
        //batch.updateVertices(MeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
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
