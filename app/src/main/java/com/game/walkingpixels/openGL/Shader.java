package com.game.walkingpixels.openGL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES32;
import android.renderscript.Matrix4f;


import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import static android.opengl.GLES31.*;

public class Shader {

    private final int id;
    private final HashMap<String, Integer> uniformLocations = new HashMap<>();
    private boolean hasGeometry = false;

    @SuppressLint("InlinedApi")
    public Shader(Context context, String path){
        String gsSource = getShaderSource(context, "geometry", path);
        int gs = 0;
        if(!gsSource.isEmpty()){
            gs = compileShader(GLES32.GL_GEOMETRY_SHADER, gsSource, path);
            if(!hasGeometry)
                path = path.replace("Geometry", "");
        }


        String vsSource = getShaderSource(context, "vertex", path);
        String fsSource = getShaderSource(context, "fragment", path);


        id = glCreateProgram();
        int vs = compileShader(GL_VERTEX_SHADER, vsSource, path);
        int fs = compileShader(GL_FRAGMENT_SHADER, fsSource, path);
        glAttachShader(id, vs);
        glAttachShader(id, fs);

        if(hasGeometry)
            glAttachShader(id, gs);

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

    public boolean hasGeometry(){return hasGeometry;}

    private int getUniformLocation(String uniform){
        if(uniformLocations.containsKey(uniform)){
            //noinspection ConstantConditions
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
    public void setUniform1f(String uniform, float x){
        int location = getUniformLocation(uniform);
        if(location != -1)
            glUniform1f(location, x);
    }
    public void setUniform1fv(String uniform, int count, float[] v, int offset){
        int location = getUniformLocation(uniform);
        if(location != -1)
            glUniform1fv(location, count, v, offset);
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
    public void setUniform3f(String uniform, Vector3 v){
        int location = getUniformLocation(uniform);
        if(location != -1)
            glUniform3f(location, v.x, v.y, v.z);
    }

    public void setUniform4f(String uniform, float x, float y, float z, float w){
        int location = getUniformLocation(uniform);
        if(location != -1)
           glUniform4f(location, x, y, z, w);
    }
    public void setUniform4f(String uniform, Vector4 v){
        int location = getUniformLocation(uniform);
        if(location != -1)
            glUniform4f(location, v.x, v.y, v.z, v.w);
    }

    public void setUniformMatrix4fv(String uniform, Matrix4f matrix4f){
        int location = getUniformLocation(uniform);
        if(location != -1)
            glUniformMatrix4fv(location, 1, false, matrix4f.getArray(), 0);
    }

    private int compileShader(int type, String source, String path){
        int shader = glCreateShader(type);
        if(shader == 0 && type == GLES32.GL_GEOMETRY_SHADER){
            hasGeometry = false;
            return 0;
        }
        glShaderSource(shader, source);
        glCompileShader(shader);

        int[] ib = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, ib, 0);
        int status = ib[0];
        String shaderType = "";

        switch (type){
            case GL_VERTEX_SHADER: shaderType = "vertex"; break;
            case GL_FRAGMENT_SHADER: shaderType = "fragment"; break;
            case GLES32.GL_GEOMETRY_SHADER: shaderType = "geometry"; break;
        }
        System.out.println("[COMPILE] " + shaderType + " " + path +" Status: " + status);
        if(status == 0){
            System.out.println("[COMPILE] " + shaderType  + " " + path + " " + glGetShaderInfoLog(shader));
        }

        return shader;
    }

    private String getShaderSource(Context context, String type, String path){
        StringBuilder source = new StringBuilder();

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
                    source.append(line).append('\n');
                }
            }

            bf.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return source.toString();
    }
}
