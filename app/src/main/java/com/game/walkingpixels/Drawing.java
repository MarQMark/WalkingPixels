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
import android.widget.TextView;

import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.model.Spell;
import com.game.walkingpixels.view.SpellAdapter;

import java.util.ArrayList;

public class Drawing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing);


        Player player = new Player();
        player.spells.add(new Spell("Circle", "This is a circle", "shapes/circle.png", "shapes/circle.png"));
        player.spells.add(new Spell("Triangle", "This is a triangle", "shapes/triangle.png", "shapes/triangle.png"));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", "shapes/fire.png"));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", "shapes/fire.png"));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", "shapes/fire.png"));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", "shapes/fire.png"));
        player.spells.add(new Spell("Flame", "This is a fire spell", "shapes/fire.png", "shapes/fire.png"));

        Enemy enemy = new Enemy("textures/slime.png");


        CanvasView cw = findViewById(R.id.myGLSurfaceViewDrawing);
        cw.setDrawingRenderer(enemy);


        Button btnAttackSelector = findViewById(R.id.btn_drawing_attack_selector);
        btnAttackSelector.setOnClickListener(e -> {
            btnAttackSelector.setVisibility(View.INVISIBLE);

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(Drawing.this);
            builderSingle.setCancelable(false);

            final SpellAdapter spellAdapter = new SpellAdapter(Drawing.this, player.spells);

            builderSingle.setAdapter(spellAdapter, (dialog, which) -> {
                Spell spell = spellAdapter.getItem(which);
                cw.drawingRenderer.drawGrid.loadShape(Drawing.this, spell.getShapePath());
            });


            AlertDialog alertDialog = builderSingle.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        });


        TextView lblTime = findViewById(R.id.lbl_drawing_time);

        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1);
                lblTime.setText(Double.toString(GameState.getDrawTime()));

                if(GameState.getDrawTime() == 0.0)
                    btnAttackSelector.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(r, 0000);
    }

}