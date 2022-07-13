package com.game.walkingpixels.controller.surface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.game.walkingpixels.controller.renderer.MapRenderer;

public class MapGLSurfaceView extends GLSurfaceView {

    private final MapRenderer renderer;

    public MapGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        renderer = new MapRenderer(context);
        setRenderer(renderer);
    }

    public MapRenderer getRenderer(){
        return renderer;
    }
}
