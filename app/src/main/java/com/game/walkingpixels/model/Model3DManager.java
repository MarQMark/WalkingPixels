package com.game.walkingpixels.model;

import android.content.Context;

import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.util.meshbuilder.Model3DBuilder;

import java.util.HashMap;

public class Model3DManager {

    private static Model3DManager manager;

    public final int PLAYER_ANIMATIONS = 5;
    public final int EYE_ANIMATIONS = 10;
    public final int SLIME_ANIMATIONS = 20;
    public final int GOLEM_ANIMATIONS = 5;

    private final HashMap<String, Model3D> models = new HashMap<>();
    private final HashMap<String, Texture> textures = new HashMap<>();

    private Model3DManager(Context context){
        textures.put("sword", new Texture(context, "models/sword.png", 0));
        models.put("sword", new Model3D(context, "models/sword", 0));

        textures.put("player", new Texture(context, "models/player/player.png", 1));
        for (int i = 1; i <= PLAYER_ANIMATIONS; i++)
            models.put("player" + i, new Model3D(context, "models/player/player" + i, 1));

        textures.put("mobs", new Texture(context, "models/mob_texture_atlas.png", 2));
        for (int i = 1; i <= EYE_ANIMATIONS; i++)
            models.put("eye" + i, new Model3D(context, "models/eye/eye" + i, 2));
        for (int i = 1; i <= SLIME_ANIMATIONS; i++)
            models.put("slime" + i, new Model3D(context, "models/slime/slime" + i, 2));
        for (int i = 1; i <= GOLEM_ANIMATIONS; i++)
            models.put("golem" + i, new Model3D(context, "models/golem/golem" + i, 2));

        textures.put("tree", new Texture(context, "models/tree.png", 3));
        models.put("tree", new Model3D(context, "models/tree", 3));
    }

    public Model3D getModel(String name){
        return models.get(name);
    }
    public Texture getTexture(String name){
        return textures.get(name);
    }

    public static Model3DManager getInstance(Context context){
        if(manager == null)
            manager = new Model3DManager(context);

        return manager;
    }
}
