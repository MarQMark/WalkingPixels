package com.game.walkingpixels.controller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MapGLSurfaceView extends GLSurfaceView {

    Context context;
    MapRenderer renderer;

    public MapGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setEGLContextClientVersion(3);
    }

    public void init(){
        renderer = new MapRenderer(context);
        setRenderer(renderer);
    }

    public MapRenderer getRenderer(){
        return renderer;
    }
}
