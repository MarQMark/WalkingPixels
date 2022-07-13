package com.game.walkingpixels.controller.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Spell;

import java.io.IOException;
import java.util.ArrayList;

public class SpellAdapter extends ArrayAdapter<Spell> {

    private final Context context;
    private final ArrayList<Spell> spells;
    private final int fontSize;

    public SpellAdapter(Context context, ArrayList<Spell> spells, int fontSize){
        super(context, android.R.layout.select_dialog_item, spells);
        this.context = context;
        this.spells = spells;
        this.fontSize = fontSize;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) convertView = inflater.inflate(R.layout.spell_list_view, parent, false);

        TextView title = convertView.findViewById(R.id.lbl_spell_list_name);
        TextView description = convertView.findViewById(R.id.lbl_spell_list_description);
        ImageView image = convertView.findViewById(R.id.image_spell_list_image);

        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        description.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize - 2);

        Spell spell = spells.get(position);
        title.setText(spell.getName());
        description.setText(spell.getDescription());

        try {
            Drawable d = Drawable.createFromStream(context.getAssets().open(spell.getPath()), null);
            image.setImageDrawable(d);
        }
        catch(IOException ignored) {
        }

        return convertView;

    }
}
