package com.game.walkingpixels.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.model.Spell;

import java.io.IOException;

public class NewSpell extends AlertDialog.Builder {

    public NewSpell(Context context, Spell spell) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View customLayout = inflater.inflate(R.layout.new_spell,null);
        this.setView(customLayout).create();
        this.setCancelable(false);

        TextView title = customLayout.findViewById(R.id.lbl_new_spell_name);
        TextView description = customLayout.findViewById(R.id.lbl_new_spell_description);
        ImageView image = customLayout.findViewById(R.id.image_new_spell_image);
        try {
            Drawable d = Drawable.createFromStream(context.getAssets().open(spell.getPath()), null);
            image.setImageDrawable(d);
        }
        catch(IOException ignored) {
        }

        title.setText(spell.getName());
        description.setText(spell.getDescription());

        AlertDialog alertDialog = this.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ResponsiveButton btn = customLayout.findViewById(R.id.btn_new_spell_close);
        btn.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

}
