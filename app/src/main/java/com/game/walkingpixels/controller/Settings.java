package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import com.game.walkingpixels.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BackgroundGLSurfaceView svBackground = findViewById(R.id.myGLSurfaceViewSettingsBackground);
        svBackground.init();

        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw = findViewById(R.id.switch_settings_shadow);
        sw.setChecked(preferences.getBoolean("shadowEnabled",false));
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("shadowEnabled", isChecked);
            editor.apply();
        });
    }
}