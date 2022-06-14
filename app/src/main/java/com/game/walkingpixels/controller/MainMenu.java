package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.game.walkingpixels.R;

public class MainMenu extends AppCompatActivity {

    /*
        Font by Hewett Tsoi - https://www.dafont.com/alagard.font
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        /*View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);*/

        Button btnPlay = findViewById(R.id.btn_main_menu_play);
        btnPlay.setOnClickListener(e -> {
            Intent intent = new Intent(this, Walking.class);
            startActivity(intent);
        });
        Button btnOptions = findViewById(R.id.btn_main_menu_options);
        btnOptions.setOnClickListener(e -> {
            Intent intent = new Intent(this, Drawing.class);
            startActivity(intent);
        });
        Button btnExit = findViewById(R.id.btn_main_menu_exit);
        btnExit.setOnClickListener(e -> {
            finish();
            System.exit(0);
        });

    }
}