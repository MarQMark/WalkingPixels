package com.game.walkingpixels.controller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MainMenuGLSurfaceView extends GLSurfaceView {

    private final MainMenuRenderer renderer;

    public MainMenuGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        renderer = new MainMenuRenderer(context);
        setRenderer(renderer);
    }

    public MainMenuRenderer getRenderer(){
        return renderer;
    }
}
