package com.game.walkingpixels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.game.walkingpixels.model.DrawGrid;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.util.Scene;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        Button btnPlay = findViewById(R.id.btn_main_menu_play);
        btnPlay.setOnClickListener(e -> {
            GameState.scene = Scene.WALKING;
            Intent intent = new Intent(this, Walking.class);
            startActivity(intent);
        });
        Button btnOptions = findViewById(R.id.btn_main_menu_options);
        btnOptions.setOnClickListener(e -> {
            GameState.scene = Scene.DRAWING;
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