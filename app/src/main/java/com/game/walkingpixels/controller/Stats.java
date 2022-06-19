package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.view.Simplebar;
import com.game.walkingpixels.view.SpellAdapter;

public class Stats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Player player = new Player(Stats.this);

        BackgroundGLSurfaceView svBackground = findViewById(R.id.myGLSurfaceViewStatsBackground);
        svBackground.init();

        ListView listSpells = findViewById(R.id.list_stats_spells);
        final SpellAdapter spellAdapter = new SpellAdapter(Stats.this, player.getSpells(), 14);
        listSpells.setAdapter(spellAdapter);

        TextView lblLevel = findViewById(R.id.lbl_stats_level);
        lblLevel.setText(getResources().getString(R.string.lbl_stats_level, player.getLevel()));

        TextView lblXp = findViewById(R.id.lbl_stats_xp);
        lblXp.setText(getResources().getString(R.string.lbl_stats_xp, player.getXp(), player.getMaxXp()));
        Simplebar barXp = findViewById(R.id.bar_stats_level);
        barXp.setMax(player.getMaxXp());
        barXp.setProgress(player.getXp());

        TextView lblStamina = findViewById(R.id.lbl_stats_stamina);
        lblStamina.setText(getResources().getString(R.string.lbl_stats_stamina, player.getStaminaLevel(), player.getMaxStamina()));
        TextView lblHealth = findViewById(R.id.lbl_stats_health);
        lblHealth.setText(getResources().getString(R.string.lbl_stats_health, player.getHealthLevel(), player.getMaxHealth()));
        TextView lblTime = findViewById(R.id.lbl_stats_time);
        lblTime.setText(getResources().getString(R.string.lbl_stats_time, player.getTimeLevel(), player.getTime()));
        TextView lblStrength = findViewById(R.id.lbl_stats_strength);
        lblStrength.setText(getResources().getString(R.string.lbl_stats_strength, player.getStrengthLevel(), player.getStrength()));
    }
}