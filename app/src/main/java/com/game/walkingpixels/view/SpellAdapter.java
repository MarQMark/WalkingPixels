package com.game.walkingpixels.view;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.game.walkingpixels.model.Spell;

import java.util.ArrayList;
import java.util.List;

public class SpellAdapter extends ArrayAdapter<Spell> {

    private Context context;
    private ArrayList<Spell> spells = new ArrayList<>();

    public SpellAdapter(Context context, ArrayList<Spell> spells){
        super(context, 0, spells);
        this.context = context;
        this.spells = spells;
    }
}
