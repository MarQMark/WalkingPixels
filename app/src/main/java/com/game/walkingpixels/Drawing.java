package com.game.walkingpixels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.game.walkingpixels.controller.DrawingGLSurfaceView;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.model.Spell;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.view.Healthbar;
import com.game.walkingpixels.view.SpellAdapter;
import com.game.walkingpixels.view.Timebar;

import java.util.ArrayList;

public class Drawing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing);


        Player player = new Player();
        player.spells.add(new Spell("Circle", "This is a circle", "shapes/circle.png", 5.0, 20));
        player.spells.add(new Spell("Triangle", "This is a triangle", "shapes/triangle.png", 5.0, 20));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", 7.0, 40));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", 7.0, 40));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", 7.0, 40));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", 7.0, 40));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", 7.0, 40));

        Enemy enemy = new Enemy(World.Block.SLIME, 100);


        DrawingGLSurfaceView cw = findViewById(R.id.myGLSurfaceViewDrawing);
        cw.init(enemy);



        Timebar barTimeRemaining = findViewById(R.id.timebar_drawing_time_remaining);

        Healthbar barEnemyHealth = findViewById(R.id.healthbar_drawing_enemy_health);
        barEnemyHealth.setMax(enemy.getHealth());
        Healthbar barPlayerHealth = findViewById(R.id.healthbar_drawing_player_health);
        barPlayerHealth.setMax(player.health);


        Button btnAttackSelector = findViewById(R.id.btn_drawing_attack_selector);
        btnAttackSelector.setOnClickListener(e -> {
            GameState.setDrawTime(1.0f);
            btnAttackSelector.setVisibility(View.INVISIBLE);

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(Drawing.this);
            builderSingle.setCancelable(false);

            final SpellAdapter spellAdapter = new SpellAdapter(Drawing.this, player.spells);

            builderSingle.setAdapter(spellAdapter, (dialog, which) -> {
                Spell spell = spellAdapter.getItem(which);
                barTimeRemaining.setMax((int) (spell.getCastTime() * 10));
                cw.getRenderer().loadSpell(spell);
                GameState.setDrawTime(spell.getCastTime());
            });


            AlertDialog alertDialog = builderSingle.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        });



        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 10);
                barTimeRemaining.setTime((int)(GameState.getDrawTime() * 10));

                barEnemyHealth.setHealth(enemy.getHealth());

                if(GameState.getDrawTime() == 0.0)
                    btnAttackSelector.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(r, 0000);
    }

}