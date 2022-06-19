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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Constants;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.MainWorld;
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
    private ResponsiveButton btnMoveForward;
    private ResponsiveButton btnBonfire;

    private int[] stamina;
    private boolean moving = false;


    @SuppressLint("ClickableViewAccessibility")
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
        btnBonfire = findViewById(R.id.btn_walking_bonfire);
        btnBonfire.setOnClickListener(e -> {
            player.setLastSavePosition(new Vector2(MainWorld.getWorld().getPosition()));
            levelUpActivityLauncher.launch(new Intent(this, LevelUp.class));
        });


        //move forward
        btnMoveForward = findViewById(R.id.btn_walking_forward);
        btnMoveForward.setOnClickListener(e -> forward());
        /*Handler handler1 = new Handler();
        final Runnable r1 = new Runnable() {
            public void run() {
                handler1.postDelayed(this, 200);
                if(moving) forward();
            }
        };
        handler1.postDelayed(r1, 0);*/


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
                        SharedPreferences sharedPreferences = getSharedPreferences("World", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat("rotation", sv.getRenderer().camera.rotationY);
                        editor.apply();
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
        btnMoveForward.setEnabled(true);
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if(data != null){
                int health = data.getIntExtra("health", 0);
                if(health == 0){
                    //player lost
                    player.kill();
                    barHealth.setProgress(player.getHealth());
                    new DeathScreen(Walking.this);
                    Vector2 lastPosition = player.getLastSavePosition();
                    MainWorld.getWorld().setPosition((int) lastPosition.x, (int) lastPosition.y);
                }
                else {
                    //player won
                    MainWorld.getWorld().removeEnemy();
                    player.setHealth(health);
                    player.addXp(data.getIntExtra("xp", 0));
                    barHealth.setProgress(player.getHealth());
                }

                //add spells
                ArrayList<Integer> newSpells = new ArrayList<>();
                for (Spell spell : player.getSpells()){
                    if(spell.getId() < 16 && spell.getId() % 4 < 3 && spell.getUsages() >= Constants.tierSpellUsages[spell.getId() % 4]){
                        new NewSpell(Walking.this, new Spell(spell.getId() + 1, 0));
                        player.setSpellUsages(spell.getId(), -1);
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

    private void forward(){
        if(MainWorld.getWorld().forward())
        {
            SharedPreferences.Editor editor = getSharedPreferences("World", Context.MODE_PRIVATE).edit();
            editor.putInt("positionX", (int) MainWorld.getWorld().getPosition().x);
            editor.putInt("positionY", (int) MainWorld.getWorld().getPosition().y);
            editor.apply();

            stamina[0] -= 100;
            barStamina.setProgress(stamina[0]);

            Enemy enemy = MainWorld.getWorld().checkForEnemy();
            if(enemy != null){
                moving = false;
                btnMoveForward.setEnabled(false);
                Intent intent = new Intent(this, Drawing.class);
                intent.putExtra("ENEMY", enemy);
                drawingActivityLauncher.launch(intent);
            }
        }

        if(MainWorld.getWorld().checkForBonfire())
            btnBonfire.setVisibility(View.VISIBLE);
        else
            btnBonfire.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stamina[0]++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}