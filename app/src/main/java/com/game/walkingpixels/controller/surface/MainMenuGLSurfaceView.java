package com.game.walkingpixels.controller.surface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.game.walkingpixels.controller.renderer.MainMenuRenderer;

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
