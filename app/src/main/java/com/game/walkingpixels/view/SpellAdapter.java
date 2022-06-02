package com.game.walkingpixels.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import java.io.InputStream;
import java.util.ArrayList;

public class SpellAdapter extends ArrayAdapter<Spell> {

    private final Context context;
    private final ArrayList<Spell> spells;

    public SpellAdapter(Context context, ArrayList<Spell> spells){
        super(context, android.R.layout.select_dialog_item, spells);
        this.context = context;
        this.spells = spells;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) convertView = inflater.inflate(R.layout.spell_list_view, parent, false);

        TextView title = convertView.findViewById(R.id.spell_list_view_name);
        TextView description = convertView.findViewById(R.id.spell_list_view_description);
        ImageView image = convertView.findViewById(R.id.spell_list_view_image);

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
