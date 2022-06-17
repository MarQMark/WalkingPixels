package com.game.walkingpixels.controller;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Constants;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.model.Spell;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.view.DeathScreen;
import com.game.walkingpixels.view.Iconbar;
import com.game.walkingpixels.view.NewSpell;
import com.game.walkingpixels.view.ResponsiveButton;

import java.util.ArrayList;

public class Walking extends AppCompatActivity implements SensorEventListener {

    private Player player;
    private WalkingGLSurfaceView sv;

    private Iconbar barStamina;
    private Iconbar barHealth;

    private int[] stamina;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);

        //init step sensor
        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);

        sv = findViewById(R.id.myGLSurfaceViewWalking);
        sv.init();

        //init player
        player = new Player(Walking.this);
        stamina = new int[]{3000};

        //stamina and health bar
        barStamina = findViewById(R.id.bar_walking_stamina);
        barStamina.setMax(player.getMaxStamina());
        barStamina.setProgress(stamina[0]);
        barHealth = findViewById(R.id.bar_walking_health);
        barHealth.setMax(player.getMaxHealth());
        barHealth.setProgress(player.getHealth());

        //buttons
        ResponsiveButton btnStats = findViewById(R.id.btn_walking_stats);
        btnStats.setOnClickListener(e -> startActivity(new Intent(this, Stats.class)));
        ResponsiveButton btnMap = findViewById(R.id.btn_walking_map);
        btnMap.setOnClickListener(e -> startActivity(new Intent(this, Map.class)));
        ResponsiveButton btnBonfire = findViewById(R.id.btn_walking_bonfire);
        btnBonfire.setOnClickListener(e -> {
            player.setLastSavePosition(new Vector2(GameState.world.getPosition()));
            player.saveStats();
            levelUpActivityLauncher.launch(new Intent(this, LevelUp.class));
        });


        //move forward
        ResponsiveButton btnMoveForward = findViewById(R.id.btn_walking_forward);
        btnMoveForward.setOnClickListener(e -> {
            if(GameState.world.forward())
            {
                stamina[0] -= 100;
                barStamina.setProgress(stamina[0]);

                Enemy enemy = GameState.world.checkForEnemy();
                if(enemy != null){
                    Intent intent = new Intent(this, Drawing.class);
                    intent.putExtra("ENEMY", enemy);
                    drawingActivityLauncher.launch(intent);
                }
            }

            if(GameState.world.checkForBonfire())
                btnBonfire.setVisibility(View.VISIBLE);
            else
                btnBonfire.setVisibility(View.INVISIBLE);
        });

        //rotate map
        int[] rotationLeft = new int[] {0};
        ResponsiveButton btnTurnLeft = findViewById(R.id.btn_walking_turn_left);
        btnTurnLeft.setOnClickListener(e -> rotationLeft[0] += 45);
        ResponsiveButton btnTurnRight = findViewById(R.id.btn_walking_turn_right);
        btnTurnRight.setOnClickListener(e -> rotationLeft[0] += -45);

        //2nd Game loop
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 10);
                if(rotationLeft[0] != 0){

                    btnMoveForward.setEnabled(false);
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
                        btnStats.setEnabled(true);
                        btnMap.setEnabled(true);
                        btnBonfire.setEnabled(true);
                    }
                }

            }
        };
        handler.postDelayed(r, 0);
    }


    private final ActivityResultLauncher<Intent> drawingActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if(data != null){
                player.loadStats();

                int health = data.getIntExtra("health", 0);
                if(health == 0){
                    player.kill();
                    barHealth.setProgress(player.getHealth());
                    new DeathScreen(Walking.this);
                    Vector2 lastPosition = player.getLastSavePosition();
                    GameState.world.setPosition((int) lastPosition.x, (int) lastPosition.y);
                }
                else {
                    GameState.world.removeEnemy();
                    player.setHealth(health);
                    player.addXp(data.getIntExtra("xp", 0));
                    barHealth.setProgress(player.getHealth());
                }

                //add spells
                ArrayList<Integer> newSpells = new ArrayList<>();
                for (Spell spell : player.getSpells()){
                    if(spell.getId() < 16 && spell.getId() % 4 < 3 && spell.getUsages() >= Constants.tierSpellUsages[spell.getId() % 4]){
                        new NewSpell(Walking.this, new Spell(spell.getId() + 1, 0));
                        spell.setMastered();
                        newSpells.add(spell.getId() + 1);
                    }
                }
                for (int id : newSpells){
                    player.addSpell(id);

                    //Meggido
                    if(player.hasSpell(3) && player.hasSpell(7) && player.hasSpell(11) && player.hasSpell(15)){
                        new NewSpell(Walking.this, new Spell(16, 0));
                        player.addSpell(16);
                    }
                }

                player.saveStats();
            }
        }
    });

    private final ActivityResultLauncher<Intent> levelUpActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        player = new Player(Walking.this);
        barHealth.setMax(player.getMaxHealth());
        barHealth.setProgress(player.getHealth());
        barStamina.setMax(player.getMaxStamina());
        barStamina.setProgress(stamina[0]);
    });

    @Override
    public void onSensorChanged(SensorEvent event) {
        stamina[0]++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}