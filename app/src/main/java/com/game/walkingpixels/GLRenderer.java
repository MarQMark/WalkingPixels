package com.game.walkingpixels;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.Vertex;
import com.game.walkingpixels.util.MeshBuilder;
import com.game.walkingpixels.util.Vector3;
import com.game.walkingpixels.util.Vector4;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.*;


public class GLRenderer implements GLSurfaceView.Renderer {



    Context context;
    Batch batch;
    Shader shader;

    static World world = new World(54216.709022936559375);

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


        Vector3 lightPosition = new Vector3(0.0f, 20.0f, 0.0f);
        Vector4 lightColor = new Vector4(1f, 1f, 1f, 1.0f);


        shader = new Shader(context);
        shader.bind();

        batch = new Batch(shader.getID(), 150000);
        batch.addVertices(MeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch.bind();

        Texture tx2 = new Texture(context, "textures/anime.png");
        //Texture tx = new Texture(context, "textures/anime.png");
        Texture tx = new Texture(context, "textures/texture_atlas.png");

        tx.bind();
        tx2.bind(1);

        int[] samplers = new int[] {0, 1};
        shader.setUniform1iv("u_Textures", 2, samplers, 0);


        shader.setUniform3f("u_LightPosition", lightPosition.x, lightPosition.y, lightPosition.z);
        shader.setUniform4f("u_LightColor", lightColor.x, lightColor.y, lightColor.z, lightColor.w);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.setUniformMatrix4fv("mvpmatrix", Camera.getMatrix());

        batch.draw();

        world.movePlayerPosition(1, 0);
        batch.updateVertices(MeshBuilder.generateMesh(world.renderedWorld, world.renderedWorldSize, world.worldMaxHeight));
        batch.bind();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Camera.setAspectRatio((float) width / (float)height);
    }
}
