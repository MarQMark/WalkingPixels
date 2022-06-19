package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.MainWorld;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.model.World;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BackgroundGLSurfaceView svBackground = findViewById(R.id.myGLSurfaceViewSettingsBackground);
        svBackground.init();

        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        SwitchCompat sw = findViewById(R.id.switch_settings_shadow);
        sw.setChecked(preferences.getBoolean("shadowEnabled",false));
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("shadowEnabled", isChecked);
            editor.apply();
        });

        Button btnReset = findViewById(R.id.btn_settings_reset);
        btnReset.setOnClickListener(e -> {
            Player player = new Player(Settings.this);
            player.reset();
            MainWorld.reset(Settings.this);
        });
    }
}