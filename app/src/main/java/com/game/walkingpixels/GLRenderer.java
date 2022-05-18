package com.game.walkingpixels;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.renderscript.Matrix4f;

import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.Vertex;
import com.game.walkingpixels.util.MeshBuilder;
import com.game.walkingpixels.util.Vector3;
import com.game.walkingpixels.util.Vector4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.*;


public class GLRenderer implements GLSurfaceView.Renderer {



    Context context;
    Batch batch;
    Shader shader;

    static World world = new World(54216.709022936559375);


    Shader shadowDebugShader;

    Shader shadowShader;
    int ShadowFrameBuffer;
    int shadowVertexBuffer;
    int shadowTexture;
    int shadowMapWidth = 2048; int shadowMapHeight = 2048;


    int width; int height;


    public GLRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        IntBuffer maxTexture = IntBuffer.allocate(Integer.BYTES);
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTexture);
        System.out.println("[INFO] Maximum image units " + maxTexture.get());

        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);



        //-------------------------------------------------------------------------------------------

        Vector3 lightPosition = new Vector3(0f, 20.0f, 1f);
        Vector4 lightColor = new Vector4(1f, 1f, 1f, 1.0f);

        shadowShader = new Shader(context, "Shaders/Shadow.shaders");
        shadowShader.bind();

        IntBuffer genFrameBuffer = IntBuffer.allocate(Integer.BYTES);
        glGenFramebuffers(1, genFrameBuffer);
        ShadowFrameBuffer = genFrameBuffer.get();
        glBindFramebuffer(GL_FRAMEBUFFER, ShadowFrameBuffer);

        IntBuffer genTexture = IntBuffer.allocate(Integer.BYTES);
        glGenTextures(1, genTexture);
        shadowTexture = genTexture.get();
        glBindTexture(GL_TEXTURE_2D, shadowTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowTexture, 0);


        glBindFramebuffer(GL_FRAMEBUFFER, 0);


        Matrix4f projection = new Matrix4f();
        projection.loadOrtho(-35.0f, 35.0f, -35.0f, 35.0f, 0.1f, 1000f);
        //projection.loadPerspective(90, 1080f/2200f, 0.1f, 1000f);
        Vector3 up = new Vector3(lightPosition.x, -lightPosition.z, lightPosition.y);
        up.normalize();
        Matrix4f lightView = Camera.lookAt(lightPosition, new Vector3(0.0f, 0.0f, 0.0f), up);
        projection.multiply(lightView);

        shadowShader.setUniformMatrix4fv("lightProjection", projection);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, shadowTexture);

        //-------------------------------------------------------------------------------------------

        float[] rectangleVertices = new float[]{
                //  Coords   // texCoords
                1.0f, -1.0f,  1.0f, 0.0f,
                -1.0f, -1.0f,  0.0f, 0.0f,
                -1.0f,  1.0f,  0.0f, 1.0f,

                1.0f,  1.0f,  1.0f, 1.0f,
                1.0f, -1.0f,  1.0f, 0.0f,
                -1.0f,  1.0f,  0.0f, 1.0f
        };

        shadowDebugShader = new Shader(context, "Shaders/DebugDepth.shaders");
        shadowDebugShader.bind();
        shadowDebugShader.setUniform1i("depthMap", 1);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        IntBuffer genVertexBuffer = IntBuffer.allocate(Integer.BYTES);
        glGenBuffers(1, genVertexBuffer);
        shadowVertexBuffer = genVertexBuffer.get();
        ByteBuffer vbb = ByteBuffer.allocateDirect(6 * 4 * Float.BYTES);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = vbb.asFloatBuffer();
        fb.put(rectangleVertices);
        fb.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, shadowVertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, fb.capacity() * Float.BYTES, fb, GL_STATIC_DRAW);

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

        shader.setUniformMatrix4fv("lightProjection", projection);

        shader.setUniformMatrix4fv("mvpmatrix", projection);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, shadowTexture);

        batch.bind();

        // Preparations for the Shadow Map
        shadowShader.bind();
        glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        glBindFramebuffer(GL_FRAMEBUFFER, ShadowFrameBuffer);
        glColorMask(false, false, false, false);
        glClear(GL_DEPTH_BUFFER_BIT);

        batch.draw();



        shader.bind();
        glViewport(0, 0, width, height);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glColorMask(true, true, true, true);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.setUniformMatrix4fv("mvpmatrix", Camera.getMatrix());

        batch.draw();


        /*shadowDebugShader.bind();

        glViewport(0, 0, width, height);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glBindBuffer(GL_ARRAY_BUFFER, shadowVertexBuffer);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glDrawArrays(GL_TRIANGLES, 0, 6);*/

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
