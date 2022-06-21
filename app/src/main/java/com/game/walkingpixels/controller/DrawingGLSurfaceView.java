package com.game.walkingpixels.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.game.walkingpixels.model.DrawTimer;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.util.TouchPosition;

public class DrawingGLSurfaceView extends GLSurfaceView {

    private final Context context;
    private DrawingRenderer renderer;

    public DrawingGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setEGLContextClientVersion(3);
    }

    public void init(Enemy enemy, DrawTimer drawTimer){
        renderer = new DrawingRenderer(context, enemy, drawTimer);
        setRenderer(renderer);
    }

    public DrawingRenderer getRenderer(){
        return renderer;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){

            TouchPosition.lastPosition.x = TouchPosition.position.x;
            TouchPosition.lastPosition.y = TouchPosition.position.y;

            TouchPosition.position.x = event.getX();
            TouchPosition.position.y = event.getY();

            if(TouchPosition.lastPosition.x == -1){
                TouchPosition.lastPosition.x = event.getX();
                TouchPosition.lastPosition.y = event.getY();
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            TouchPosition.lastPosition.x = -1;
            TouchPosition.lastPosition.y = -1;
            TouchPosition.position.x = -1;
            TouchPosition.position.y = -1;
        }

        return true;
    }
}
