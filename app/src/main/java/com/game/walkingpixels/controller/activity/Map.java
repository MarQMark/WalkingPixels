package com.game.walkingpixels.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.MainWorld;

public class Map extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        TextView lblX = findViewById(R.id.lbl_map_x);
        lblX.setText(getResources().getString(R.string.lbl_map_x, (int) MainWorld.getWorld().getPosition().x * -1));
        TextView lblY = findViewById(R.id.lbl_map_y);
        lblY.setText(getResources().getString(R.string.lbl_map_y, (int)MainWorld.getWorld().getPosition().y));
    }
}