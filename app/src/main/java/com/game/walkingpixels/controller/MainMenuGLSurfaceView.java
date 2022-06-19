package com.game.walkingpixels.controller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MainMenuGLSurfaceView extends GLSurfaceView {

    private final Context context;
    private MainMenuRenderer renderer;

    public MainMenuGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setEGLContextClientVersion(3);
    }

    public void init(){
        renderer = new MainMenuRenderer(context);
        setRenderer(renderer);
    }

    public MainMenuRenderer getRenderer(){
        return renderer;
    }
}
