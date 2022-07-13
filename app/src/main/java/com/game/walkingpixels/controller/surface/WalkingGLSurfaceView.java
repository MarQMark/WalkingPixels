package com.game.walkingpixels.controller.surface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.game.walkingpixels.controller.renderer.WalkingRenderer;

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
