package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.Player;

public class Map extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        MapGLSurfaceView sv = findViewById(R.id.myGLSurfaceViewMap);
        sv.init();

        BackgroundGLSurfaceView svBackground = findViewById(R.id.myGLSurfaceViewMapBackground);
        svBackground.init();

        TextView lblX = findViewById(R.id.lbl_map_x);
        lblX.setText(getResources().getString(R.string.lbl_map_x, (int)GameState.world.getPosition().x));
        TextView lblY = findViewById(R.id.lbl_map_y);
        lblY.setText(getResources().getString(R.string.lbl_map_y, (int)GameState.world.getPosition().y));
    }
}