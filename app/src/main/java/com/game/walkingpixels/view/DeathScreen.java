package com.game.walkingpixels.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.game.walkingpixels.R;


public class DeathScreen extends AlertDialog.Builder {
    public DeathScreen(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View customLayout = inflater.inflate(R.layout.death_screen, null);
        this.setView(customLayout).create();
        this.setCancelable(false);

        AlertDialog alertDialog = this.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ResponsiveButton btn = customLayout.findViewById(R.id.btn_death_screen_close);
        btn.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }
}
