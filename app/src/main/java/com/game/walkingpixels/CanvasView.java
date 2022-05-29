package com.game.walkingpixels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.game.walkingpixels.util.EventHandler;

public class CanvasView extends GLSurfaceView {
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(3);

        GLRenderer renderer = new GLRenderer(context, GLRenderer.Scene.WALKING);
        setRenderer(renderer);
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
