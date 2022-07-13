package com.game.walkingpixels.controller.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.game.walkingpixels.model.Camera;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;

import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetIntegerv;

public abstract class Renderer implements GLSurfaceView.Renderer {
    public int width = 0;
    public int height = 0;

    private final HashMap<String, Shader> shaders = new HashMap<>();
    private final HashMap<String, Batch> batches = new HashMap<>();
    private final HashMap<String, LightManager> lightManagers = new HashMap<>();

    public final Context context;
    public Camera camera;

    private double lastTime;

    public Renderer(Context context){
        this.context = context;
    }

    public abstract void init();
    public abstract void update(double dt);
    public abstract void render(double dt);

    public void registerShader(String name, Shader shader){
        shaders.put(name, shader);
    }
    public Shader shader(String name){
        return shaders.get(name);
    }
    public void registerBatch(String name, Batch batch){
        batches.put(name, batch);
    }
    public Batch batch(String name){
        return batches.get(name);
    }
    public void registerLightManager(String name, LightManager lightManager){
        lightManagers.put(name, lightManager);
    }
    public LightManager lightManager(String name){
        return lightManagers.get(name);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        printOpenGLInfo(gl);

        lastTime = System.currentTimeMillis();

        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        init();
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        double deltaTime = System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();

        update(deltaTime);
        render(deltaTime);

        queryErrors("onDrawFrame");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;

        if(camera != null)
            camera.setAspectRatio((float) width / (float)height);
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
