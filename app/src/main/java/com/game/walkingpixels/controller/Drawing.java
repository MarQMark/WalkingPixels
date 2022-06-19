package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.DrawTimer;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.model.Spell;
import com.game.walkingpixels.view.Iconbar;
import com.game.walkingpixels.view.ResponsiveButton;
import com.game.walkingpixels.view.SpellAdapter;
import com.game.walkingpixels.view.Simplebar;

public class Drawing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Enemy enemy = (Enemy) getIntent().getSerializableExtra("ENEMY");
        if (enemy == null) {
            finish();
        } else {
            setContentView(R.layout.activity_drawing);

            Player player = new Player(Drawing.this);
            DrawTimer drawTimer = new DrawTimer();

            DrawingGLSurfaceView sv = findViewById(R.id.myGLSurfaceViewDrawing);
            sv.init(enemy, drawTimer);


            //Time & Healthbar init
            Simplebar barTimeRemaining = findViewById(R.id.timebar_drawing_time_remaining);
            Iconbar barEnemyHealth = findViewById(R.id.healthbar_drawing_enemy_health);
            barEnemyHealth.setMax(enemy.getHealth());
            barEnemyHealth.setText(Integer.toString(enemy.getLevel()));
            barEnemyHealth.flip();
            Iconbar barPlayerHealth = findViewById(R.id.healthbar_drawing_player_health);
            barPlayerHealth.setMax(player.getMaxHealth());
            barPlayerHealth.setText(Integer.toString(player.getLevel()));


            //open spell menu
            ResponsiveButton btnAttackSelector = findViewById(R.id.btn_drawing_attack_selector);
            btnAttackSelector.setOnClickListener(e -> {
                drawTimer.setDrawTime(1.0f);
                btnAttackSelector.setVisibility(View.INVISIBLE);

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(Drawing.this);
                builderSingle.setCancelable(false);

                final SpellAdapter spellAdapter = new SpellAdapter(Drawing.this, player.getSpells(), 16);

                builderSingle.setAdapter(spellAdapter, (dialog, which) -> {
                    Spell spell = spellAdapter.getItem(which);
                    spell.addUsages();
                    player.setSpellUsages(spell.getId(), spell.getUsages());
                    barTimeRemaining.setMax((int) (spell.getCastTime() * 10));
                    sv.getRenderer().loadSpell(spell);
                    drawTimer.setDrawTime(spell.getCastTime() * player.getTime());
                });


                AlertDialog alertDialog = builderSingle.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            });


            //damage numbers init
            TextView lblEnemyDamageTaken = findViewById(R.id.lbl_drawing_enemy_damage_taken);
            TextView lblPlayerDamageTaken = findViewById(R.id.lbl_drawing_player_damage_taken);
            int[] oldHealth = new int[]{
                    enemy.getHealth(),
                    player.getHealth()
            };
            int[] numberLoopsDamageTakenVisible = new int[]{0};

            //2nd Game loop
            Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    handler.postDelayed(this, 10);

                    //check if enemy dead
                    if(enemy.getHealth() == 0){
                        Intent intent = new Intent();
                        intent.putExtra("health", player.getHealth());
                        intent.putExtra("xp", enemy.getXp());
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    //check if player is dead
                    if(player.getHealth() == 0){
                        setResult(RESULT_OK, new Intent().putExtra("health", player.getHealth()));
                        finish();
                    }

                    //update damage taken labels
                    if (oldHealth[0] != enemy.getHealth()) {
                        lblEnemyDamageTaken.setText(getResources().getString(R.string.lbl_drawing_damage, enemy.getHealth() - oldHealth[0]));
                        numberLoopsDamageTakenVisible[0] = 50;
                    }
                    if (oldHealth[1] != player.getHealth()) {
                        lblPlayerDamageTaken.setText(getResources().getString(R.string.lbl_drawing_damage, player.getHealth() - oldHealth[1]));
                        numberLoopsDamageTakenVisible[0] = 50;
                    }
                    if (numberLoopsDamageTakenVisible[0] > 0)
                        numberLoopsDamageTakenVisible[0]--;
                    else if (numberLoopsDamageTakenVisible[0] == 0) {
                        lblEnemyDamageTaken.setText("");
                        lblPlayerDamageTaken.setText("");
                    }
                    oldHealth[0] = enemy.getHealth();
                    oldHealth[1] = player.getHealth();


                    //update drawing timebar
                    barTimeRemaining.setProgress((int) (drawTimer.getDrawTime() * 10));

                    //update healthbar
                    barEnemyHealth.setProgress(enemy.getHealth());
                    barPlayerHealth.setProgress(player.getHealth());


                    //enemy attack
                    if (enemy.isEnemyTurn() && enemy.getAttackDelay() == 0) {
                        player.damage(enemy.attack());
                        enemy.setEnemyTurn(false);
                        btnAttackSelector.setVisibility(View.VISIBLE);
                    }
                }
            };
            handler.postDelayed(r, 0);
        }
    }

    @Override
    public void onBackPressed() {
    }
}