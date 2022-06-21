package com.game.walkingpixels.openGL;

import android.opengl.GLES32;
import android.os.Build;
import android.renderscript.Matrix4f;

import com.game.walkingpixels.model.Camera;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES31.*;

public class PointLight {

    private static final int shadowMapWidth = 2048;
    private static final int shadowMapHeight = 2048;
    private int textureSlot = 4;

    private final Shader shader;
    private final Camera camera;

    private Vector3 position;
    private Vector4 color;
    private float intensity;
    private final int cubeMap;
    private final Matrix4f[] transforms = new Matrix4f[6];

    private boolean positionChanged = false;

    public PointLight(Vector3 position, Vector4 color, float intensity, int framebuffer, Shader shader, int number, Camera camera){

        this.camera = camera;
        this.position = position;
        this.color = color;
        this.intensity = intensity;
        textureSlot += number;

        this.shader = shader;

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

        int[] genTexture = new int[1];
        glGenTextures(1, genTexture, 0);
        cubeMap = genTexture[0];
        glActiveTexture(GL_TEXTURE0 + textureSlot);
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMap);

        for (int i = 0; i < 6; ++i)
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT32F, shadowMapWidth, shadowMapHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        glGenerateMipmap(GL_TEXTURE_CUBE_MAP);

        if(shader.hasGeometry() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                GLES32.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, cubeMap, 0);


        glBindFramebuffer(GL_FRAMEBUFFER  , 0);

        shader.bind();
        createTransforms();
        shader.setUniform1f("u_FarPlane", camera.farPlane);
        shader.unbind();
    }

    public void draw(Batch[] batches){
        if(positionChanged)
            createTransforms();

        setShaderSpecifics();

        if(shader.hasGeometry()){
            for (Batch batch : batches){
                batch.bind();
                batch.draw();
                batch.unbind();
            }
        }else {
            for(int i = 0; i < 6; i++){
                shader.setUniformMatrix4fv("u_LightProjection", transforms[i]);
                glFramebufferTexture2D(GL_FRAMEBUFFER  , GL_DEPTH_ATTACHMENT, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, cubeMap, 0);
                glClear(GL_DEPTH_BUFFER_BIT);
                for (Batch batch : batches){
                    batch.bind();
                    batch.draw();
                    batch.unbind();
                }
            }
        }

        positionChanged = false;
    }

    public void setPosition(Vector3 position){
        this.position = new Vector3(position);
        positionChanged = true;
    }

    public Vector3 getPosition(){ return position;}

    public Vector4 getColor(){return color;}

    public void setColor(Vector4 color){ this.color = color;}

    public float getIntensity() {return intensity;}

    public void setIntensity(float intensity){ this.intensity = intensity;}

    public int getTextureSlot(){  return textureSlot; }

    private void createTransforms(){
        for (int i = 0; i < transforms.length; i++){
            transforms[i] = new Matrix4f();
            transforms[i].loadPerspective(90, 1.0f, camera.nearPlane, camera.farPlane);
        }
        transforms[0].multiply(Camera.lookAt(position, position.add(new Vector3(1.0f, 0.0f, 0.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
        transforms[1].multiply(Camera.lookAt(position, position.add(new Vector3(-1.0f, 0.0f, 0.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
        transforms[2].multiply(Camera.lookAt(position, position.add(new Vector3(0.0f, 1.0f, 0.0f)), new Vector3(0.0f, 0.0f, -1.0f)));
        transforms[3].multiply(Camera.lookAt(position, position.add(new Vector3(0.0f, -1.0f, 0.0f)), new Vector3(0.0f, 0.0f, -1.0f)));
        transforms[4].multiply(Camera.lookAt(position, position.add(new Vector3(0.0f, 0.0f, 1.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
        transforms[5].multiply(Camera.lookAt(position, position.add(new Vector3(0.0f, 0.0f, -1.0f)), new Vector3(0.0f, -1.0f, 0.0f)));
    }

    private void setShaderSpecifics(){
        shader.setUniform3f("u_LightPosition", position.x, position.y, position.z);
        if(shader.hasGeometry()){
            for (int i = 0; i < 6; i++)
                shader.setUniformMatrix4fv("u_shadowMatrices[" + i + "]", transforms[i]);
        }
    }
}
