package com.game.walkingpixels.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Constants;

public class LevelUpStat extends RelativeLayout {

    private TextView lblName;
    private TextView lblLevel;
    private TextView lblStat;
    private ResponsiveButton btnAdd;
    private ResponsiveButton btnRm;
    private int levelStart;
    private int levelAdded = 0;
    private float baseValue;
    private boolean isInt;

    public LevelUpStat(Context context) {
        super(context);
        init(context);
    }

    public LevelUpStat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LevelUpStat);

        int name = attributes.getResourceId(R.styleable.LevelUpStat_name, -1);
        if(name != -1)
            lblName.setText(name);

        int stat = attributes.getResourceId(R.styleable.LevelUpStat_stat, -1);
        if(stat != -1)
            lblStat.setText(stat);

        attributes.recycle();
    }

    public LevelUpStat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.level_up_stat, this);

        lblName = findViewById(R.id.lbl_level_up_stat_name);
        lblLevel = findViewById(R.id.lbl_level_up_stat_level);
        lblStat = findViewById(R.id.lbl_level_up_stat_stats);

        btnAdd = findViewById(R.id.btn_level_up_stat_add_level);
        btnRm = findViewById(R.id.btn_level_up_stat_rm_level);
        btnRm.setEnabled(false);
        btnRm.getForeground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    }

    public void init(int level, int value){
        levelStart = level;
        baseValue = value;
        isInt = true;
        update();
    }
    public void init(int level, float value){
        levelStart = level;
        baseValue = value;
        isInt = false;
        update();
    }

    public void add(){
        btnRm.setEnabled(true);
        btnRm.getForeground().setColorFilter(null);
        levelAdded++;

        update();
    }
    public void rm(){
        levelAdded--;
        if(levelAdded == 0){
            btnRm.setEnabled(false);
            btnRm.getForeground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }

        update();
    }

    private void update(){
        lblLevel.setText(getResources().getString(R.string.lbl_level_up_level, levelStart + levelAdded));

        if(isInt)
            lblStat.setText(getResources().getString(R.string.lbl_level_up_stats_int, (int)(baseValue * Constants.levelFunction(levelStart)), (int)(baseValue * Constants.levelFunction(levelStart + levelAdded))));
        else
            lblStat.setText(getResources().getString(R.string.lbl_level_up_stats_float, baseValue * Constants.levelFunction(levelStart), baseValue * Constants.levelFunction(levelStart + levelAdded)));
    }

    public int getLevel(){
        return levelStart + levelAdded;
    }

    public ResponsiveButton getAddButton(){
        return btnAdd;
    }
    public ResponsiveButton getRemoveButton(){
        return btnRm;
    }

}
