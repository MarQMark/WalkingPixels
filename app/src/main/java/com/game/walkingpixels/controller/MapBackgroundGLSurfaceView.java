package com.game.walkingpixels.controller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


public class MapBackgroundGLSurfaceView extends GLSurfaceView {

    Context context;
    MapBackgroundRenderer renderer;

    public MapBackgroundGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setEGLContextClientVersion(3);
    }

    public void init(){
        renderer = new MapBackgroundRenderer(context);
        setRenderer(renderer);
    }

    public MapBackgroundRenderer getRenderer(){
        return renderer;
    }
}
