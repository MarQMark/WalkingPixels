package com.game.walkingpixels.controller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


public class BackgroundGLSurfaceView extends GLSurfaceView {

    Context context;
    BackgroundRenderer renderer;

    public BackgroundGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setEGLContextClientVersion(3);
    }

    public void init(){
        renderer = new BackgroundRenderer(context);
        setRenderer(renderer);
    }

    public BackgroundRenderer getRenderer(){
        return renderer;
    }
}
