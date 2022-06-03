package com.game.walkingpixels.view;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.game.walkingpixels.R;

public class Timebar extends RelativeLayout {

    private int max = 100;
    private int time = 0;

    private ImageView imageTime;

    public Timebar(Context context) {
        super(context);
        init(context);
    }

    public Timebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Timebar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.timebar, this);

        imageTime = findViewById(R.id.image_timebar_time);
    }


    public void setMax(int max){
        this.max = max;
    }
    public void setTime(int time){
        this.time = time;
        ClipDrawable mImageDrawable = (ClipDrawable) imageTime.getDrawable();
        mImageDrawable.setLevel((int) ((time / (float)max) * 10000.0f));
    }
}
