package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.World;

public class Map extends AppCompatActivity {

    private MapGLSurfaceView sv;
    private MapBackgroundGLSurfaceView svBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        sv = findViewById(R.id.myGLSurfaceViewMap);
        sv.init();

        svBackground = findViewById(R.id.myGLSurfaceViewMapBackground);
        svBackground.init();
    }
}