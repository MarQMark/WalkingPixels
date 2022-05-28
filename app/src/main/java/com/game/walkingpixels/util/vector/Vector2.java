package com.game.walkingpixels.util.vector;

public class Vector2 {
    public float x = 0;
    public float y = 0;

    public Vector2(){
    }

    public Vector2(Vector2 v){
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getLength(){
        return (float) Math.sqrt(x * x + y * y);
    }

    public void normalize(){
        float length = getLength();
        x /= length;
        y /= length;
    }

    public void scale(float m){
        x *= m;
        y *= m;
    }

    public Vector2 add(Vector2 v){
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 sub(Vector2 v){
        return new Vector2(x - v.x, y - v.y);
    }

    public float dot(Vector2 v){
        return x*v.x + y*v.y;
    }
}
