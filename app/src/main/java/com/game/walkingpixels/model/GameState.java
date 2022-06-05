package com.game.walkingpixels.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.game.walkingpixels.util.Scene;

public class GameState {

    private static double drawTime = 10.0;
    public static double getDrawTime(){
        return drawTime;
    }
    public static void updateDrawTime(double dt){

        drawTime -= dt;
        if(drawTime <= 0.0){
            drawTime = 0.0;
        }
    }
    public static void setDrawTime(double time){
        drawTime = time;
    }
}
