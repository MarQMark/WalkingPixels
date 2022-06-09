package com.game.walkingpixels.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.game.walkingpixels.util.Scene;

public class GameState {
    //TODO Rework this
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

    public static World world = new World(54216.709022936559375);
}
