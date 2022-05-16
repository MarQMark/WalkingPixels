package com.game.walkingpixels.openGL;

import android.content.Context;
import android.renderscript.Matrix4f;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.HashMap;

import static android.opengl.GLES20.*;

public class Shader {

    private final int id;
    private final HashMap<String, Integer> uniformLocations = new HashMap<String, Integer>();

    public Shader(Context context){
        int vs = compileShader(GL_VERTEX_SHADER, getShaderSource(context, "vertex", "Shaders/Basic.shaders"));
        int fs = compileShader(GL_FRAGMENT_SHADER, getShaderSource(context, "fragment", "Shaders/Basic.shaders"));

        id = glCreateProgram();
        glAttachShader(id, vs);
        glAttachShader(id, fs);
        glLinkProgram(id);
        glUseProgram(id);
    }

    public int getID() { return id;}

    public void bind(){
        glUseProgram(id);
    }

    public void unbind(){
        glUseProgram(0);
    }

    private int getUniformLocation(String uniform){
        if(uniformLocations.containsKey(uniform)){
            return uniformLocations.get(uniform);
        }

        int location = glGetUniformLocation(id, uniform);
        uniformLocations.put(uniform, location);
        return location;
    }

    public void setUniform1i(String uniform, int x){
        int location = getUniformLocation(uniform);
        if(location != -1)
            glUniform1i(location, x);
    }

    public void setUniform1iv(String uniform, int count, int[] v, int offset){
        int location = getUniformLocation(uniform);
        if(location != -1)
            glUniform1iv(location, count, v, offset);
    }

    public void setUniform3f(String uniform, float x, float y, float z){
        int location = getUniformLocation(uniform);
        if(location != -1)
            glUniform3f(location, x, y, z);
    }

    public void setUniform4f(String uniform, float x, float y, float z, float w){
        int location = getUniformLocation(uniform);
        if(location != -1)
           glUniform4f(location, x, y, z, w);
    }

    public void setUniformMatrix4fv(String uniform, Matrix4f matrix4f){
        int location = getUniformLocation(uniform);
        if(location != -1)
            glUniformMatrix4fv(location, 1, false, matrix4f.getArray(), 0);
    }

    private int compileShader(int type, String source){
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);

        IntBuffer ib = IntBuffer.allocate(Integer.BYTES);
        glGetShaderiv(shader, GL_COMPILE_STATUS, ib);
        int status = ib.get();
        System.out.println("[COMPILE] " + (type == GL_VERTEX_SHADER ? "vertex": "fragment") + " Status: " + status);
        if(status == 0){
            System.out.println("[COMPILE] " + (type == GL_VERTEX_SHADER ? "vertex": "fragment") + " " + glGetShaderInfoLog(shader));
        }

        return shader;
    }

    private String getShaderSource(Context context, String type, String path){
        String source = "";

        BufferedReader bf;
        try{
            bf = new BufferedReader(new InputStreamReader(context.getAssets().open(path)));

            boolean active = false;
            String line;
            while ((line = bf.readLine()) != null){
                if(line.contains("#shader")){
                    active = line.contains(type);
                }
                else if(active){
                    source += line + '\n';
                }
            }

            bf.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return source;
    }
}
