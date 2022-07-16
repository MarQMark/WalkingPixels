package com.game.walkingpixels.controller.surface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.game.walkingpixels.controller.renderer.DrawingRenderer;
import com.game.walkingpixels.model.DrawTimer;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.util.TouchPosition;
import com.game.walkingpixels.util.vector.Vector2;

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
            TouchPosition.addPosition(new Vector2(event.getX(), event.getY()));
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            TouchPosition.addPosition(new Vector2(-1));
        }
        return true;
    }
}
