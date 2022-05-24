package com.game.walkingpixels.util;

public class Vector3 {

    public float x = 0;
    public float y = 0;
    public float z = 0;

    public Vector3(){
    }

    public Vector3(Vector3 v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getLength(){
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize(){
        float length = getLength();
        x /= length;
        y /= length;
        z /= length;
    }

    public void scale(float m){
        x *= m;
        y *= m;
        z *= m;
    }

    public Vector3 add(Vector3 v){
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 sub(Vector3 v){
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public Vector3 cross(Vector3 v){
        return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public float dot(Vector3 v){
        return x*v.x + y*v.y + z*v.z;
    }

    public static Vector3 zero(){
        return new Vector3(0.0f, 0.0f, 0.0f);
    }
}
