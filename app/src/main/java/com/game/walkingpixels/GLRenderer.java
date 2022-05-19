package com.game.walkingpixels;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.renderscript.Matrix4f;

import androidx.annotation.RequiresApi;

import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
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
    private final Vector4 lightColor = new Vector4(1f, 1f, 1f, 1.0f);

    private Shader shadowShader;
    private int ShadowFrameBuffer;
    private int shadowCubeMap;
    private final int shadowMapWidth = 2048; private final int shadowMapHeight = 2048;
    private Matrix4f[] shadowTransforms;

    int width; int height;


    public GLRenderer(Context context){
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        IntBuffer maxTexture = IntBuffer.allocate(Integer.BYTES);
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTexture);
        System.out.println("[INFO] Maximum image units " + maxTexture.get());

        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);



        //-------------------------------------------------------------------------------------------

        shadowShader = new Shader(context, "Shaders/Shadow.shaders");
        shadowShader.bind();

        IntBuffer genFrameBuffer = IntBuffer.allocate(Integer.BYTES);
        glGenFramebuffers(1, genFrameBuffer);
        ShadowFrameBuffer = genFrameBuffer.get();
        glBindFramebuffer(GL_FRAMEBUFFER, ShadowFrameBuffer);

        IntBuffer genTexture = IntBuffer.allocate(Integer.BYTES);
        glGenTextures(1, genTexture);
        shadowCubeMap = genTexture.get();
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, shadowCubeMap);
        for (int i = 0; i < 6; ++i)
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);


        shadowTransforms = new Matrix4f[6];
        for (int i = 0; i < shadowTransforms.length; i++){
            shadowTransforms[i] = new Matrix4f();
            shadowTransforms[i].loadPerspective(90, 1.0f, 0.1f, 1000f);
        }
        shadowTransforms[0].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(1.0f, 0.0f, 0.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
        shadowTransforms[1].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(-1.0f, 0.0f, 0.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
        shadowTransforms[2].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(0.0f, 1.0f, 0.0f)), new Vector3(0.0f, 0.0f, -1.0f)));
        shadowTransforms[3].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(0.0f, -1.0f, 0.0f)), new Vector3(0.0f, 0.0f, -1.0f)));
        shadowTransforms[4].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(0.0f, 0.0f, 1.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
        shadowTransforms[5].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(0.0f, 0.0f, -1.0f)), new Vector3(0.0f, -1.0f, 0.0f)));

        shadowShader.setUniform3f("u_LightPosition", lightPosition.x, lightPosition.y, lightPosition.z);
        shadowShader.setUniform1f("u_FarPlane", 1000.0f);

        //-------------------------------------------------------------------------------------------

        shader = new Shader(context, "Shaders/Basic.shaders");
        shader.bind();

        batch = new Batch(shader.getID(), 150000);
        batch.addVertices(MeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch.bind();

        Texture tx = new Texture(context, "textures/texture_atlas.png");
        tx.bind();

        int[] samplers = new int[] {0, 1};
        shader.setUniform1iv("u_Textures", 2, samplers, 0);

        shader.setUniform3f("u_LightPosition", lightPosition.x, lightPosition.y, lightPosition.z);
        shader.setUniform4f("u_LightColor", lightColor.x, lightColor.y, lightColor.z, lightColor.w);
        shader.setUniform1i("u_ShadowCubeMap", shadowCubeMap);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        //update light
        lightRotation++;

        lightPosition.z = (float) (Math.cos(Math.toRadians(lightRotation)) * lightMaxHeight);
        lightPosition.y = (float) (Math.sin(Math.toRadians(lightRotation)) * lightMaxHeight);

        for (int i = 0; i < shadowTransforms.length; i++){
            shadowTransforms[i] = new Matrix4f();
            shadowTransforms[i].loadPerspective(90, 1.0f, 0.1f, 1000f);
        }
        shadowTransforms[0].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(1.0f, 0.0f, 0.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
        shadowTransforms[1].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(-1.0f, 0.0f, 0.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
        shadowTransforms[2].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(0.0f, 1.0f, 0.0f)), new Vector3(0.0f, 0.0f, -1.0f)));
        shadowTransforms[3].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(0.0f, -1.0f, 0.0f)), new Vector3(0.0f, 0.0f, -1.0f)));
        shadowTransforms[4].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(0.0f, 0.0f, 1.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
        shadowTransforms[5].multiply(Camera.lookAt(lightPosition, lightPosition.add(new Vector3(0.0f, 0.0f, -1.0f)), new Vector3(0.0f, -1.0f, 0.0f)));


        //do other stuff
        batch.bind();
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, shadowCubeMap);

        shadowShader.bind();
        glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        glBindFramebuffer(GL_FRAMEBUFFER, ShadowFrameBuffer);
        shadowShader.setUniform3f("u_LightPosition", lightPosition.x, lightPosition.y, lightPosition.z);

        glClear(GL_DEPTH_BUFFER_BIT);
        for(int i = 0; i < 6; i++){
            shadowShader.setUniformMatrix4fv("u_LightProjection", shadowTransforms[i]);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, shadowCubeMap, 0);

            glClear(GL_DEPTH_BUFFER_BIT);
            batch.draw();
        }


        shader.bind();
        glViewport(0, 0, width, height);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glColorMask(true, true, true, true);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.setUniform3f("u_LightPosition", lightPosition.x, lightPosition.y, lightPosition.z);
        shader.setUniformMatrix4fv("mvpmatrix", Camera.getMatrix());


        batch.draw();


        //world.movePlayerPosition(1, 0);
        batch.updateVertices(MeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch.bind();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        Camera.setAspectRatio((float) width / (float)height);
    }
}
