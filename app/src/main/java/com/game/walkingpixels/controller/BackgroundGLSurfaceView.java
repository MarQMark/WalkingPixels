package com.game.walkingpixels.controller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


public class BackgroundGLSurfaceView extends GLSurfaceView {

    private final BackgroundRenderer renderer;

    public BackgroundGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        renderer = new BackgroundRenderer(context);
        setRenderer(renderer);
    }

    public BackgroundRenderer getRenderer(){
        return renderer;
    }
}
