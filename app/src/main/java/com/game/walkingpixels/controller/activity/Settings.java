package com.game.walkingpixels.controller.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.MainWorld;
import com.game.walkingpixels.model.Player;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        SwitchCompat switchShadow = findViewById(R.id.switch_settings_shadow);
        switchShadow.setChecked(preferences.getBoolean("shadow_enabled",false));
        switchShadow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("shadow_enabled", isChecked);
            editor.apply();
        });

        SwitchCompat switchWalking = findViewById(R.id.switch_settings_walking);
        switchWalking.setChecked(preferences.getBoolean("real_time_walking",false));
        switchWalking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("real_time_walking", isChecked);
            editor.apply();
        });

        Button btnReset = findViewById(R.id.btn_settings_reset);
        btnReset.setOnClickListener(e -> {
            final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.ScrollAlertDialog))
                    .setTitle("Reset")
                    .setMessage("Do you really want to reset all your progress?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Player player = new Player(Settings.this);
                        player.reset();
                        MainWorld.reset(Settings.this);
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            TextView lblMessage = alertDialog.findViewById(android.R.id.message);
            Typeface face = ResourcesCompat.getFont(this, R.font.alagard);
            if (lblMessage != null) {
                lblMessage.setTypeface(face);
            }
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.scroll_foreground, getTheme()));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.scroll_foreground, getTheme()));
            ImageView imageView = alertDialog.findViewById(android.R.id.icon);
            if (imageView != null)
                imageView.setColorFilter(getResources().getColor(R.color.scroll_foreground, getTheme()), android.graphics.PorterDuff.Mode.SRC_IN);
        });
    }
}