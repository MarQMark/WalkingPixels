package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Constants;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.view.LevelUpStat;
import com.game.walkingpixels.view.ResponsiveButton;
import com.game.walkingpixels.view.Simplebar;

public class LevelUp extends AppCompatActivity {

    private ResponsiveButton btnStaminaAdd;
    private ResponsiveButton btnHealthAdd;
    private ResponsiveButton btnTimeAdd;
    private ResponsiveButton btnStrengthAdd;

    private TextView lblLevel;
    private TextView lblXp;
    private Simplebar barXp;

    private Player player;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);

        BackgroundGLSurfaceView svBackground = findViewById(R.id.myGLSurfaceViewLevelUpBackground);
        svBackground.init();

        player = new Player(LevelUp.this);
        level = player.getLevel();

        lblLevel = findViewById(R.id.lbl_level_up_level);
        lblXp = findViewById(R.id.lbl_level_up_xp);
        barXp = findViewById(R.id.bar_level_up_level);

        //Stamina
        LevelUpStat statStamina = findViewById(R.id.stat_level_up_stamina);
        statStamina.init(player.getStaminaLevel(), Constants.baseStamina);
        btnStaminaAdd = statStamina.getAddButton();
        btnStaminaAdd.setOnClickListener(e -> {
            statStamina.add();
            level++;
            update();
        });
        ResponsiveButton btnStaminaRm = statStamina.getRemoveButton();
        btnStaminaRm.setOnClickListener(e -> {
            statStamina.rm();
            level--;
            update();
        });

        //Health
        LevelUpStat statHealth = findViewById(R.id.stat_level_up_health);
        statHealth.init(player.getHealthLevel(), Constants.baseHealth);
        btnHealthAdd = statHealth.getAddButton();
        btnHealthAdd.setOnClickListener(e -> {
            statHealth.add();
            level++;
            update();
        });
        ResponsiveButton btnHealthRm = statHealth.getRemoveButton();
        btnHealthRm.setOnClickListener(e -> {
            statHealth.rm();
            level--;
            update();
        });

        //Time
        LevelUpStat statTime = findViewById(R.id.stat_level_up_time);
        statTime.init(player.getTimeLevel(), Constants.baseTime);
        btnTimeAdd = statTime.getAddButton();
        btnTimeAdd.setOnClickListener(e -> {
            statTime.add();
            level++;
            update();
        });
        ResponsiveButton btnTimeRm = statTime.getRemoveButton();
        btnTimeRm.setOnClickListener(e -> {
            statTime.rm();
            level--;
            update();
        });

        //Strength
        LevelUpStat statStrength = findViewById(R.id.stat_level_up_strength);
        statStrength.init(player.getStrengthLevel(), Constants.baseStrength);
        btnStrengthAdd = statStrength.getAddButton();
        btnStrengthAdd.setOnClickListener(e -> {
            statStrength.add();
            level++;
            update();
        });
        ResponsiveButton btnStrengthRm = statStrength.getRemoveButton();
        btnStrengthRm.setOnClickListener(e -> {
            statStrength.rm();
            level--;
            update();
        });


        ResponsiveButton btnLevelUp = findViewById(R.id.btn_level_up_level_up);
        btnLevelUp.setOnClickListener(e -> {
            player.setStaminaLevel(statStamina.getLevel());
            player.setHealthLevel(statHealth.getLevel());
            player.setTimeLevel(statTime.getLevel());
            player.setStrengthLevel(statStrength.getLevel());
            player.setLevel(level);
            player.setHealth(player.getMaxHealth());
            finish();
        });
        ResponsiveButton btnCancel = findViewById(R.id.btn_level_up_cancel);
        btnCancel.setOnClickListener(e -> {
            player.setHealth(player.getMaxHealth());
            finish();
        });

        update();
    }

    private void update(){
        int currentXp = player.getXp();
        for (int i = player.getLevel(); i < level; i++)
            currentXp -= Constants.baseMaxXp * Constants.xpFunction(i);

        lblLevel.setText(getResources().getString(R.string.lbl_stats_level, level));
        lblXp.setText(getResources().getString(R.string.lbl_stats_xp, currentXp, (int)(Constants.baseMaxXp * Constants.xpFunction(level))));
        barXp.setMax((int)(Constants.baseMaxXp * Constants.xpFunction(level)));
        barXp.setProgress(currentXp);

        enableAdd(!(currentXp < Constants.baseMaxXp * Constants.xpFunction(level)));
    }

    private void enableAdd(boolean enable){
        btnStaminaAdd.setEnabled(enable);
        btnHealthAdd.setEnabled(enable);
        btnTimeAdd.setEnabled(enable);
        btnStrengthAdd.setEnabled(enable);
    }

    @Override
    public void onBackPressed() {
        player.setHealth(player.getMaxHealth());
        super.onBackPressed();
    }
}