package com.game.walkingpixels.openGL;

import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glViewport;

public class LightManager {

    private final int maxNumberOfPointLights = 4;
    private int numberOfPointLights = 0;
    private final PointLight[] lights = new PointLight[maxNumberOfPointLights];

    private Shader worldShader= null;

    private int framebuffer = 0;
    private Shader shader = null;

    private final int shadowMapWidth = 2048;
    private final int shadowMapHeight = 2048;

    public LightManager(){
    }

    public void calculateShadow(Batch[] batches, int width, int height){
        if(numberOfPointLights == 0)
            return;

        shader.bind();
        glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        glBindFramebuffer(GL_FRAMEBUFFER  , framebuffer);

        for (int i = 0; i < numberOfPointLights; i++)
            lights[i].draw(batches);

        shader.unbind();
        glViewport(0, 0, width, height);
        glBindFramebuffer(GL_FRAMEBUFFER  , 0);
    }

    public void createPointLight(Vector3 position, Vector4 color, float intensity){
        if(numberOfPointLights >= maxNumberOfPointLights)
            throw new NumberOfPointLightsOutOfBoundsException();

        if(framebuffer == 0){
            int[] genFrameBuffer = new int[1];
            glGenFramebuffers(1, genFrameBuffer, 0);
            framebuffer = genFrameBuffer[0];
        }

        if(shader == null)
            throw new LightShadowShaderIsNotInitializedException();
        if(worldShader == null)
            throw new WorldLightShadowShaderIsNotInitializedException();


        lights[numberOfPointLights] = new PointLight(position, color, intensity, framebuffer, shader, numberOfPointLights);

        worldShader.bind();
        worldShader.setUniform1f("u_LightCount", (numberOfPointLights + 1));
        worldShader.setUniform3f("u_LightPosition[" + numberOfPointLights + "]", position);
        worldShader.setUniform4f("u_LightColor[" + numberOfPointLights + "]", color);
        worldShader.setUniform1f("u_LightIntensity[" + numberOfPointLights + "]", intensity);
        worldShader.setUniform1i("u_ShadowCubeMap[" + numberOfPointLights + "]", lights[numberOfPointLights].getTextureSlot());
        worldShader.unbind();

        numberOfPointLights++;
    }

    public void initShader(Shader shader){
        this.shader = shader;
    }
    public void initWorldShader(Shader shader){
        worldShader = shader;
        worldShader.bind();
        worldShader.setUniform1f("u_LightCount", 0);
        worldShader.unbind();
    }

    public Vector3 getLightPosition(int light){
        return lights[light].getPosition();
    }
    public Vector4 getLightColor(int light){
        return lights[light].getColor();
    }
    public float getLightIntensity(int light){
        return lights[light].getIntensity();
    }
    public void setLightPosition(int light, Vector3 position){
        lights[light].setPosition(position);
        worldShader.bind();
        worldShader.setUniform3f("u_LightPosition[" + light + "]", position);
        worldShader.unbind();
    }
    public void setLightColor(int light, Vector4 color){
        lights[light].setColor(color);
        worldShader.bind();
        worldShader.setUniform4f("u_LightColor[" + light + "]", color);
        worldShader.unbind();
    }
    public void setLightIntensity(int light, float intensity){
        lights[light].setIntensity(intensity);
        worldShader.bind();
        worldShader.setUniform1f("u_LightIntensity[" + light + "]", intensity);
        worldShader.unbind();
    }

    public void removeLight(int light){
        if(lights[light] == null) return;
        lights[light] = null;

        for (int i = light + 1; i < numberOfPointLights; i++){
            if(lights[i] != null)
                lights[i - 1] = lights[i];
            else
                break;
        }

        numberOfPointLights--;

        worldShader.bind();
        for (int i = 0; i < numberOfPointLights; i++){
            worldShader.setUniform1f("u_LightCount", numberOfPointLights);
            worldShader.setUniform3f("u_LightPosition[" + i + "]", lights[i].getPosition());
            worldShader.setUniform4f("u_LightColor[" + i + "]", lights[i].getColor());
            worldShader.setUniform1f("u_LightIntensity[" + i + "]", lights[i].getIntensity());
            worldShader.setUniform1i("u_ShadowCubeMap[" + i + "]", lights[i].getTextureSlot());
        }
        worldShader.bind();
    }

    private class NumberOfPointLightsOutOfBoundsException extends RuntimeException {
        public NumberOfPointLightsOutOfBoundsException(){
            super("Number of PointLights: " + numberOfPointLights + "  Maximum: " + maxNumberOfPointLights);
        }
    }
    private static class LightShadowShaderIsNotInitializedException extends RuntimeException {
        public LightShadowShaderIsNotInitializedException(){
            super("Light shadow shader has not been initialized");
        }
    }
    private static class WorldLightShadowShaderIsNotInitializedException extends RuntimeException {
        public WorldLightShadowShaderIsNotInitializedException(){
            super("world shader for light shadow has not been initialized");
        }
    }
}
