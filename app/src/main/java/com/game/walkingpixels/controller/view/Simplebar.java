package com.game.walkingpixels.controller.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.content.res.AppCompatResources;

import com.game.walkingpixels.R;

public class Simplebar extends RelativeLayout {

    private int max = 100;
    private int progress = 0;

    private ImageView imageProgress;

    public Simplebar(Context context) {
        super(context);
        init(context);
    }

    public Simplebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Simplebar);

        int progressID = attributes.getResourceId(R.styleable.Simplebar_simple_bar_progress, -1);
        if(progressID != -1)
            imageProgress.setImageDrawable(AppCompatResources.getDrawable(context, progressID));

        attributes.recycle();
    }

    public Simplebar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.simplebar, this);

        imageProgress = findViewById(R.id.image_timebar_time);
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
