package com.game.walkingpixels.controller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class WalkingGLSurfaceView extends GLSurfaceView {

    private final WalkingRenderer renderer;

    public WalkingGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        renderer = new WalkingRenderer(context);
        setRenderer(renderer);
    }

    public WalkingRenderer getRenderer(){
        return renderer;
    }
}
