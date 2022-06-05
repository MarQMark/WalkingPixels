package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

import com.game.walkingpixels.R;
import com.game.walkingpixels.controller.WalkingGLSurfaceView;
import com.game.walkingpixels.controller.WalkingRenderer;
import com.game.walkingpixels.model.GameState;

public class Walking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);

        WalkingGLSurfaceView sv = findViewById(R.id.myGLSurfaceViewWalking);
        sv.init();



        Button btnStats = findViewById(R.id.btn_walking_stats);
        Button btnMap = findViewById(R.id.btn_walking_map);


        Button btnMoveForward = findViewById(R.id.btn_walking_forward);
        btnMoveForward.setOnClickListener(e -> WalkingRenderer.world.move(1, 0));

        int[] rotationLeft = new int[] {0};
        Button btnTurnLeft = findViewById(R.id.btn_walking_turn_left);
        btnTurnLeft.setOnClickListener(e -> rotationLeft[0] = 90);
        Button btnTurnRight = findViewById(R.id.btn_walking_turn_right);
        btnTurnRight.setOnClickListener(e -> rotationLeft[0] = -90);

        //2nd Game loop
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 10);
                if(rotationLeft[0] != 0){

                    btnMoveForward.setEnabled(false);
                    btnTurnLeft.setEnabled(false);
                    btnTurnRight.setEnabled(false);
                    btnStats.setEnabled(false);
                    btnMap.setEnabled(false);

                    if(rotationLeft[0] > 0){
                        sv.getRenderer().camera.rotationY++;
                        rotationLeft[0]--;
                    }
                    else{
                        sv.getRenderer().camera.rotationY--;
                        rotationLeft[0]++;
                    }

                    if(rotationLeft[0] == 0){
                        btnMoveForward.setEnabled(true);
                        btnTurnLeft.setEnabled(true);
                        btnTurnRight.setEnabled(true);
                        btnStats.setEnabled(true);
                        btnMap.setEnabled(true);
                    }
                }

            }
        };
        handler.postDelayed(r, 0);













        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw = findViewById(R.id.switch1);
        sw.setChecked(preferences.getBoolean("shadowEnabled",false));
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("shadowEnabled", isChecked);
            editor.apply();
        });
    }
}