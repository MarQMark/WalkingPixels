package com.game.walkingpixels.controller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class WalkingGLSurfaceView extends GLSurfaceView {

    Context context;
    WalkingRenderer renderer;

    public WalkingGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setEGLContextClientVersion(3);
    }

    public void init(){
        renderer = new WalkingRenderer(context);
        setRenderer(renderer);
    }

    public WalkingRenderer getRenderer(){
        return renderer;
    }
}
