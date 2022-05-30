package com.game.walkingpixels.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.game.walkingpixels.util.Scene;

public class GameState {
    public static Scene scene  = Scene.WALKING;

    public static boolean drawing = false;
    private static double drawTime = 10.0;
    public static double getDrawTime(){
        return drawTime;
    }
    public static void updateDrawTime(double dt){

        drawTime -= dt;
        if(drawTime <= 0.0){
            drawTime = 0.0;
            drawing = false;
        }
    }
    public static void setDrawTime(double time){
        drawTime = time;
    }
}
