package com.game.walkingpixels.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.game.walkingpixels.R;

public class Iconbar extends ConstraintLayout {

    private int max = 100;
    private int progress = 0;

    private ImageView imageProgress;
    private ImageView icon;

    public Iconbar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public Iconbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Iconbar);

        int iconID = attributes.getResourceId(R.styleable.Iconbar_icon, -1);
        if(iconID != -1)
            icon.setImageDrawable(AppCompatResources.getDrawable(context, iconID));

        int progressID = attributes.getResourceId(R.styleable.Iconbar_progress, -1);
        if(progressID != -1)
            imageProgress.setImageDrawable(AppCompatResources.getDrawable(context, progressID));

        attributes.recycle();
    }

    public Iconbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.iconbar, this);

        imageProgress = findViewById(R.id.image_iconbar_health);
        icon = findViewById(R.id.image_iconbar_icon);
        //icon.setImageDrawable(context.getDrawable(R.drawable.ic_select_spell_large));
    }


    public void setMax(int max){
        this.max = max;
    }
    public void setProgress(int progress){
        this.progress = progress;
        ClipDrawable mImageDrawable = (ClipDrawable) imageProgress.getDrawable();
        mImageDrawable.setLevel((int) ((progress / (float)max) * 10000.0f));
    }

    public int getProgress() {
        return progress;
    }

}
