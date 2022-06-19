package com.game.walkingpixels.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.game.walkingpixels.R;

public class Iconbar extends ConstraintLayout {

    private int max = 100;
    private int progress = 0;

    private TextView lblProgress;
    private ImageView imageProgress;
    private ImageView icon;
    private TextView text;

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

        int textID = attributes.getResourceId(R.styleable.Iconbar_text, -1);
        if(textID != -1)
            text.setText(textID);

        int progressID = attributes.getResourceId(R.styleable.Iconbar_icon_bar_progress, -1);
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

        lblProgress = findViewById(R.id.lbl_iconbar_progress);
        imageProgress = findViewById(R.id.image_iconbar_health);
        icon = findViewById(R.id.image_iconbar_icon);
        text = findViewById(R.id.lbl_iconbar_icon);
    }


    public void setMax(int max){
        this.max = max;
        lblProgress.setText(getResources().getString(R.string.lbl_iconbar_progress, progress, max));
    }
    public void setProgress(int progress){
        this.progress = progress;
        lblProgress.setText(getResources().getString(R.string.lbl_iconbar_progress, progress, max));
        ClipDrawable mImageDrawable = (ClipDrawable) imageProgress.getDrawable();
        mImageDrawable.setLevel((int) ((progress / (float)max) * 10000.0f));
    }

    public int getProgress() {
        return progress;
    }

    public void setText(String text){
        this.text.setText(text);
    }

    public void flip(){
        lblProgress.setScaleX(-1);
        icon.setScaleX(-1);
        text.setScaleX(-1);
    }
}
