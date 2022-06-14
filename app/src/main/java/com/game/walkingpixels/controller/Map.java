package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.game.walkingpixels.R;

public class Map extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        MapGLSurfaceView sv = findViewById(R.id.myGLSurfaceViewMap);
        sv.init();

        BackgroundGLSurfaceView svBackground = findViewById(R.id.myGLSurfaceViewMapBackground);
        svBackground.init();
    }
}