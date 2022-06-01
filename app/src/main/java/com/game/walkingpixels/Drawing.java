package com.game.walkingpixels;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.Spell;
import com.game.walkingpixels.view.SpellAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Drawing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing);

        CanvasView cw = findViewById(R.id.myGLSurfaceViewDrawing);
        cw.setDrawingRenderer();

        Button btnAttackSelector = findViewById(R.id.btn_drawing_attack_selector);
        btnAttackSelector.setVisibility(View.INVISIBLE);
        btnAttackSelector.setOnClickListener(e -> {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(Drawing.this);
            builderSingle.setIcon(R.drawable.ic_launcher_foreground);
            builderSingle.setTitle("Select One Name:-");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Drawing.this, android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Hardik");
            arrayAdapter.add("Archit");
            arrayAdapter.add("Jignesh");
            arrayAdapter.add("Umang");
            arrayAdapter.add("Gatti");

            ArrayList<Spell> spells = new ArrayList<>();
            spells.add(new Spell(R.drawable.ic_spell_circle, "Triangle", "this is a triangle"));
            spells.add(new Spell(R.drawable.ic_spell_circle, "Circle", "this is a circle"));
            final SpellAdapter spellAdapter = new SpellAdapter(Drawing.this, spells);


            builderSingle.setAdapter(spellAdapter, (dialog, which) -> {
                //String strName = arrayAdapter.getItem(which);

            });
            builderSingle.show();
        });





        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        TextView lblTime = findViewById(R.id.lbl_drawing_time);

        Handler handler =new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1);
                lblTime.setText(Double.toString(GameState.getDrawTime()));
            }
        };
        handler.postDelayed(r, 0000);
    }
}