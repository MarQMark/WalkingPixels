package com.game.walkingpixels;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CanvasView extends GLSurfaceView {
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(3);

        GLRenderer renderer = new GLRenderer(context, GLRenderer.Scene.WALKING);
        setRenderer(renderer);
    }
}
