package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Attack;
import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.model.Spell;
import com.game.walkingpixels.view.Iconbar;
import com.game.walkingpixels.view.SpellAdapter;
import com.game.walkingpixels.view.Timebar;

public class Drawing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing);


        Player player = new Player(Drawing.this);
        player.spells.add(new Spell("Circle", "This is a circle", "shapes/circle.png", 4.0, 20));
        player.spells.add(new Spell("Triangle", "This is a triangle", "shapes/triangle.png", 4.0, 20));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", 5.0, 40));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", 5.0, 40));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", 5.0, 40));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", 5.0, 40));
        player.spells.add(new Spell("Flame", "This is a long fire spell", "shapes/fire.png", 7.0, 40));

        Enemy enemy = new Enemy(Block.SLIME, 100);
        enemy.addAttack(new Attack(20, 10, 3));
        enemy.addAttack(new Attack(30, 20, 1));
        enemy.addAttack(new Attack(30, 0, 2));


        DrawingGLSurfaceView sv = findViewById(R.id.myGLSurfaceViewDrawing);
        sv.init(enemy);


        //Time & Healthbar init
        Timebar barTimeRemaining = findViewById(R.id.timebar_drawing_time_remaining);
        Iconbar barEnemyHealth = findViewById(R.id.healthbar_drawing_enemy_health);
        barEnemyHealth.setMax(enemy.getHealth());
        Iconbar barPlayerHealth = findViewById(R.id.healthbar_drawing_player_health);
        barPlayerHealth.setMax(player.getHealth());


        //open spell menu
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
                sv.getRenderer().loadSpell(spell);
                GameState.setDrawTime(spell.getCastTime());
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

                //update damage taken labels
                if(oldHealth[0] != enemy.getHealth()) {
                    lblEnemyDamageTaken.setText(Integer.toString(enemy.getHealth() - oldHealth[0]));
                    numberLoopsDamageTakenVisible[0] = 50;
                }
                if(oldHealth[1] != player.getHealth()){
                    lblPlayerDamageTaken.setText(Integer.toString(player.getHealth() - oldHealth[1]));
                    numberLoopsDamageTakenVisible[0] = 50;
                }
                if(numberLoopsDamageTakenVisible[0] > 0)
                    numberLoopsDamageTakenVisible[0]--;
                else if(numberLoopsDamageTakenVisible[0] == 0){
                    lblEnemyDamageTaken.setText("");
                    lblPlayerDamageTaken.setText("");
                }
                oldHealth[0] = enemy.getHealth();
                oldHealth[1] = player.getHealth();


                //update drawing timebar
                barTimeRemaining.setTime((int)(GameState.getDrawTime() * 10));

                //update healthbar
                barEnemyHealth.setProgress(enemy.getHealth());
                barPlayerHealth.setProgress(player.getHealth());


                //enemy attack
                if(enemy.isEnemyTurn() && enemy.getAttackDelay() == 0){
                    player.damage(enemy.attack());
                    enemy.setEnemyTurn(false);
                    btnAttackSelector.setVisibility(View.VISIBLE);
                }
            }
        };
        handler.postDelayed(r, 0);
    }

}