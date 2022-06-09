package com.game.walkingpixels.controller;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

import com.game.walkingpixels.R;
import com.game.walkingpixels.controller.WalkingGLSurfaceView;
import com.game.walkingpixels.controller.WalkingRenderer;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.view.Iconbar;

public class Walking extends AppCompatActivity {

    private Player player;
    private WalkingGLSurfaceView sv;

    private Iconbar barHealth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);

        sv = findViewById(R.id.myGLSurfaceViewWalking);
        sv.init();

        player = new Player(Walking.this);
        int[] stamina = new int[]{5000};

        //stamina and health bar
        Iconbar barStamina = findViewById(R.id.bar_walking_stamina);
        barStamina.setMax(player.getMaxStamina());
        barStamina.setProgress(stamina[0]);
        barHealth = findViewById(R.id.bar_walking_health);
        barHealth.setMax(player.getMaxHealth());
        barHealth.setProgress(player.getHealth());

        //buttons
        Button btnStats = findViewById(R.id.btn_walking_stats);
        Button btnMap = findViewById(R.id.btn_walking_map);
        Button btnBonfire = findViewById(R.id.btn_walking_bonfire);

        //move forward
        Button btnMoveForward = findViewById(R.id.btn_walking_forward);
        btnMoveForward.setOnClickListener(e -> {
            if(sv.getRenderer().moveForward())
            {
                stamina[0] -= 100;
                barStamina.setProgress(stamina[0]);

                Enemy enemy = sv.getRenderer().checkForEnemy();
                if(enemy != null){
                    Intent intent = new Intent(this, Drawing.class);
                    intent.putExtra("ENEMY", enemy);
                    drawingActivityLauncher.launch(intent);
                }
            }

            if(sv.getRenderer().checkForBonfire())
                btnBonfire.setVisibility(View.VISIBLE);
            else
                btnBonfire.setVisibility(View.INVISIBLE);
        });

        //rotate map
        int[] rotationLeft = new int[] {0};
        Button btnTurnLeft = findViewById(R.id.btn_walking_turn_left);
        btnTurnLeft.setOnClickListener(e -> rotationLeft[0] += 90);
        Button btnTurnRight = findViewById(R.id.btn_walking_turn_right);
        btnTurnRight.setOnClickListener(e -> rotationLeft[0] += -90);

        //2nd Game loop
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 10);
                if(rotationLeft[0] != 0){

                    btnMoveForward.setEnabled(false);
                    //btnTurnLeft.setEnabled(false);
                    //btnTurnRight.setEnabled(false);
                    btnStats.setEnabled(false);
                    btnMap.setEnabled(false);
                    btnBonfire.setEnabled(false);

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
                        //btnTurnLeft.setEnabled(true);
                        //btnTurnRight.setEnabled(true);
                        btnStats.setEnabled(true);
                        btnMap.setEnabled(true);
                        btnBonfire.setEnabled(true);
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


    ActivityResultLauncher<Intent> drawingActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if(data != null){
                int health = data.getIntExtra("health", 0);
                if(health == 0){
                    player.kill();
                    barHealth.setProgress(player.getHealth());
                    sv.getRenderer().respawn(player.getLastSavePosition());
                }
                else {
                    sv.getRenderer().removeEnemy();
                    player.setHealth(health);
                    player.addXp(data.getIntExtra("xp", 0));
                    barHealth.setProgress(player.getHealth());
                }
                player.saveStats();
            }
        }
    });
}