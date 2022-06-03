package com.game.walkingpixels.view;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.game.walkingpixels.R;

public class Healthbar extends ConstraintLayout {

    private int max = 100;
    private int health = 0;

    private ImageView imageHealth;

    public Healthbar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public Healthbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Healthbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.healthbar, this);

        imageHealth = findViewById(R.id.image_healthbar_health);
    }


    public void setMax(int max){
        this.max = max;
    }
    public void setHealth(int health){
        this.health = health;
        ClipDrawable mImageDrawable = (ClipDrawable) imageHealth.getDrawable();
        mImageDrawable.setLevel((int) ((health / (float)max) * 10000.0f));
    }
}
