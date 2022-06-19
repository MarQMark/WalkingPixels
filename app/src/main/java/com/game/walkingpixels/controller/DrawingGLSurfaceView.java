package com.game.walkingpixels.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.game.walkingpixels.model.DrawTimer;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.util.EventHandler;

public class DrawingGLSurfaceView extends GLSurfaceView {

    Context context;
    DrawingRenderer renderer;

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

            EventHandler.lastTouchPosition.x = EventHandler.touchPosition.x;
            EventHandler.lastTouchPosition.y = EventHandler.touchPosition.y;

            EventHandler.touchPosition.x = event.getX();
            EventHandler.touchPosition.y = event.getY();

            if(EventHandler.lastTouchPosition.x == -1){
                EventHandler.lastTouchPosition.x = event.getX();
                EventHandler.lastTouchPosition.y = event.getY();
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            EventHandler.lastTouchPosition.x = -1;
            EventHandler.lastTouchPosition.y = -1;
            EventHandler.touchPosition.x = -1;
            EventHandler.touchPosition.y = -1;
        }

        return true;
    }
}
