package com.game.walkingpixels.model;

public class DrawTimer {
    private double drawTime = 10.0;

    public DrawTimer(){
    }

    public double getDrawTime(){
        return drawTime;
    }
    public void updateDrawTime(double dt){

        drawTime -= dt;
        if(drawTime <= 0.0){
            drawTime = 0.0;
        }
    }
    public void setDrawTime(double time){
        drawTime = time;
    }
}
