package com.game.walkingpixels.controller;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Constants;
import com.game.walkingpixels.model.Enemy;
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
    private ResponsiveButton btnStats;
    private ResponsiveButton btnMap;
    private ResponsiveButton btnTurnLeft;
    private ResponsiveButton btnTurnRight;
    private ResponsiveButton btnBonfire;
    private SwitchCompat switchAutoMoving;
    private TextView lblOutOfStamina;

    private boolean staminaInitialized = false;
    private boolean autoMoving = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);

        //init step sensor
        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Sensor accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        sv = findViewById(R.id.myGLSurfaceViewWalking);
        sv.init();

        //init player
        player = new Player(Walking.this);

        //stamina and health bar
        barStamina = findViewById(R.id.bar_walking_stamina);
        barStamina.setMax(player.getMaxStamina());
        barStamina.setProgress(player.getStamina());
        barHealth = findViewById(R.id.bar_walking_health);
        barHealth.setMax(player.getMaxHealth());
        barHealth.setProgress(player.getHealth());

        //buttons
        btnStats = findViewById(R.id.btn_walking_stats);
        btnStats.setOnClickListener(e -> startActivity(new Intent(this, Stats.class)));
        btnMap = findViewById(R.id.btn_walking_map);
        btnMap.setOnClickListener(e -> startActivity(new Intent(this, Map.class)));
        btnBonfire = findViewById(R.id.btn_walking_bonfire);
        btnBonfire.setVisibility(MainWorld.getWorld().checkForBonfire() ? View.VISIBLE : View.INVISIBLE);
        btnBonfire.setOnClickListener(e -> {
            btnBonfire.setEnabled(false);
            player.setLastSavePosition(new Vector2(MainWorld.getWorld().getPosition()));
            levelUpActivityLauncher.launch(new Intent(this, LevelUp.class));
        });

        //out of stamina
        lblOutOfStamina = findViewById(R.id.lbl_walking_out_of_stamina);
        lblOutOfStamina.setVisibility(player.getStamina() == 0 ? View.VISIBLE : View.INVISIBLE);

        //move forward
        btnMoveForward = findViewById(R.id.btn_walking_forward);
        btnMoveForward.setOnClickListener(e -> forward(1));

        //auto moving
        switchAutoMoving = findViewById(R.id.switch_walking_auto_walking);
        switchAutoMoving.setChecked(false);
        switchAutoMoving.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnStats.setEnabled(!isChecked);
            btnTurnLeft.setEnabled(!isChecked);
            btnTurnRight.setEnabled(!isChecked);
            btnMoveForward.setEnabled(!isChecked);
            btnMap.setEnabled(!isChecked);
            btnBonfire.setEnabled(!isChecked);
            autoMoving = isChecked;
        });
        //auto moving loop
        Handler autoMovingHandler = new Handler();
        final Runnable autoMovingRunnable = new Runnable() {
            public void run() {
                autoMovingHandler.postDelayed(this, 500);
                if(autoMoving)
                    forward(2);
            }
        };
        autoMovingHandler.postDelayed(autoMovingRunnable, 0);

        //rotate map
        int[] rotationLeft = new int[] {0};
        btnTurnLeft = findViewById(R.id.btn_walking_turn_left);
        btnTurnLeft.setOnClickListener(e -> rotationLeft[0] += 45);
        btnTurnRight = findViewById(R.id.btn_walking_turn_right);
        btnTurnRight.setOnClickListener(e -> rotationLeft[0] += -45);

        //Rotation loop Game loop
        Handler rotationHandler = new Handler();
        final Runnable rotationRunnable = new Runnable() {
            public void run() {
                rotationHandler.postDelayed(this, 10);
                if(rotationLeft[0] != 0){

                    btnMoveForward.setEnabled(false);
                    btnStats.setEnabled(false);
                    btnMap.setEnabled(false);
                    btnBonfire.setEnabled(false);
                    switchAutoMoving.setEnabled(false);

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
                        switchAutoMoving.setEnabled(true);
                    }
                }

            }
        };
        rotationHandler.postDelayed(rotationRunnable, 0);
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
        btnBonfire.setEnabled(true);
        player = new Player(Walking.this);
        barHealth.setMax(player.getMaxHealth());
        barHealth.setProgress(player.getHealth());
        barStamina.setMax(player.getMaxStamina());
        barStamina.setProgress(player.getStamina());
    });

    private void forward(int i){
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        System.out.println("AAAAAAAAAAAAAA   " + i + "    " + sharedPreferences.getBoolean("real_time_walking", false));
        if(player.getStamina() > 0 && MainWorld.getWorld().forward())
        {
            SharedPreferences.Editor editor = getSharedPreferences("World", Context.MODE_PRIVATE).edit();
            editor.putInt("positionX", (int) MainWorld.getWorld().getPosition().x);
            editor.putInt("positionY", (int) MainWorld.getWorld().getPosition().y);
            editor.apply();

            player.setStamina(player.getStamina() - 1);
            barStamina.setProgress(player.getStamina());
            if(player.getStamina() <= 0){
                player.setStamina(0);
                lblOutOfStamina.setVisibility(View.VISIBLE);
            }

            Enemy enemy = MainWorld.getWorld().checkForEnemy();
            if(enemy != null){
                btnStats.setEnabled(true);
                btnTurnLeft.setEnabled(true);
                btnTurnRight.setEnabled(true);
                btnMap.setEnabled(true);
                btnBonfire.setEnabled(true);
                switchAutoMoving.setChecked(false);
                autoMoving = false;

                btnMoveForward.setEnabled(false);
                Intent intent = new Intent(this, Drawing.class);
                intent.putExtra("Enemy", enemy);
                drawingActivityLauncher.launch(intent);
            }
        }

        if(MainWorld.getWorld().checkForBonfire())
            btnBonfire.setVisibility(View.VISIBLE);
        else
            btnBonfire.setVisibility(View.INVISIBLE);
    }


    private final float[] gravity = new float[3];
    private double prevY;
    private boolean ignore = true;
    private int countdown = 5;

    private float[] lowPassFilter( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + 1.0f * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] smoothed = lowPassFilter(event.values, gravity);
            gravity[0] = smoothed[0];
            gravity[1] = smoothed[1];
            gravity[2] = smoothed[2];
            if(ignore) {
                countdown--;
                ignore = (countdown >= 0) && ignore;
            }
            else
                countdown = 22;

            if((Math.abs(prevY - gravity[1]) > 1.0) && !ignore){

                SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
                if(sharedPreferences.getBoolean("real_time_walking", false)){
                    if(btnMoveForward.isEnabled()){
                        forward(3);
                    }
                }
                else {
                    player.setStamina(player.getStamina() + 1);
                    lblOutOfStamina.setVisibility(View.INVISIBLE);
                    barStamina.setProgress(player.getStamina());
                }

                ignore = true;
            }
            prevY = gravity[1];
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            SharedPreferences sharedPreferences = getSharedPreferences("Stamina", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(!staminaInitialized){
                int lastStepsMeasured = sharedPreferences.getInt("last_steps_measured", (int)event.values[0]);
                //counter reset due to phone restart
                if(lastStepsMeasured > event.values[0])
                    player.setStamina(player.getStamina() + (int)event.values[0]);
                else
                    player.setStamina(player.getStamina() + (int)event.values[0] - lastStepsMeasured);

                barStamina.setProgress(player.getStamina());
                staminaInitialized = true;
            }
            editor.putInt("last_steps_measured", (int)event.values[0]);
            editor.apply();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}