package com.game.walkingpixels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.game.walkingpixels.controller.DrawingRenderer;
import com.game.walkingpixels.controller.WalkingRenderer;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.util.EventHandler;
import com.game.walkingpixels.util.Scene;

public class CanvasView extends GLSurfaceView {

    Context context;
    WalkingRenderer walkingRenderer;
    DrawingRenderer drawingRenderer;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setEGLContextClientVersion(3);
    }

    public void setWalkingRenderer(){
        walkingRenderer = new WalkingRenderer(context);
        setRenderer(walkingRenderer);
    }
    public void setDrawingRenderer(Enemy enemy){
        drawingRenderer = new DrawingRenderer(context, enemy);
        setRenderer(drawingRenderer);
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
