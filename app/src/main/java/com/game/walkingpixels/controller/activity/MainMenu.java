package com.game.walkingpixels.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.VersionInfo;
import android.os.Build;
import android.os.Bundle;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.MainWorld;
import com.game.walkingpixels.controller.view.ResponsiveButton;

public class MainMenu extends AppCompatActivity {

    /*
        Font by Hewett Tsoi - https://www.dafont.com/alagard.font
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        MainWorld.init(MainMenu.this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                //ask for permission
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            }
        }

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