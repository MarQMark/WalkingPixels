package com.game.walkingpixels.util;

public class Vector4 {
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public float w = 0;

    public Vector4(){
    }

    public Vector4(Vector4 v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
    }

    public Vector4(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float getLength(){
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public void normalize(){
        float length = getLength();
        x /= length;
        y /= length;
        z /= length;
        w /= length;
    }

    public void scale(float m){
        x *= m;
        y *= m;
        z *= m;
        w *= m;
    }

    public Vector4 add(Vector4 v){
        return new Vector4(x + v.x, y + v.y, z + v.z, w + v.w);
    }

    public Vector4 sub(Vector4 v){
        return new Vector4(x - v.x, y - v.y, z - v.z, w - v.w);
    }

    public float dot(Vector4 v){
        return x*v.x + y*v.y + z*v.z + w*v.w;
    }

    public static Vector4 zero(){
        return new Vector4(0.0f, 0.0f, 0.0f, 0.0f);
    }
}
