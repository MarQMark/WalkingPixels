package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.MainWorld;
import com.game.walkingpixels.view.ResponsiveButton;

public class MainMenu extends AppCompatActivity {

    /*
        Font by Hewett Tsoi - https://www.dafont.com/alagard.font
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        MainWorld.init(MainMenu.this);


        MainMenuGLSurfaceView sv = findViewById(R.id.myGLSurfaceViewMainMenu);
        sv.init();

        ResponsiveButton btnPlay = findViewById(R.id.btn_main_menu_play);
        btnPlay.setOnClickListener(e -> {
            Intent intent = new Intent(this, Walking.class);
            startActivity(intent);
        });
        ResponsiveButton btnOptions = findViewById(R.id.btn_main_menu_options);
        btnOptions.setOnClickListener(e -> {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        });
        ResponsiveButton btnHelp = findViewById(R.id.btn_main_menu_help);
        btnHelp.setOnClickListener(e -> {
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        });
        ResponsiveButton btnExit = findViewById(R.id.btn_main_menu_exit);
        btnExit.setOnClickListener(e -> {
            finish();
            System.exit(0);
        });

    }
}