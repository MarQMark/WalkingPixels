package com.game.walkingpixels.model;

import android.content.Context;
import android.util.Pair;

import com.game.walkingpixels.openGL.Texture;

import java.util.HashMap;
import java.util.Objects;

public class Model3DManager {

    private static Model3DManager manager;

    public final int PLAYER_ANIMATIONS = 26;
    public final int EYE_ANIMATIONS = 22;
    public final int SLIME_ANIMATIONS = 30;
    public final int GOLEM_ANIMATIONS = 16;
    public final int SNAKE_ANIMATIONS = 10;
    public final int FLOWER_ANIMATIONS = 13;
    public final int BAT_ANIMATIONS = 6;

    private final HashMap<String, Model3D> models = new HashMap<>();
    private final HashMap<String, Pair<String, Integer>> textures = new HashMap<>();

    private Model3DManager(Context context){
        textures.put("player", new Pair<>("models/player/player.png", 1));
        for (int i = 0; i <= PLAYER_ANIMATIONS; i++)
            models.put("player" + i, new Model3D(context, "models/player/player" + i, 1));

        textures.put("mobs", new Pair<>("models/mob_texture_atlas.png", 2));
        models.put("obelisk", new Model3D(context, "models/obelisk/obelisk", 2));
        for (int i = 0; i < EYE_ANIMATIONS; i++)
            models.put("eye" + i, new Model3D(context, "models/eye/eye" + i, 2));
        for (int i = 0; i < SLIME_ANIMATIONS; i++)
            models.put("slime" + i, new Model3D(context, "models/slime/slime" + i, 2));
        for (int i = 0; i < SLIME_ANIMATIONS; i++)
            models.put("slimeGreen" + i, new Model3D(context, "models/slime/slimeGreen" + i, 2));
        for (int i = 0; i < SLIME_ANIMATIONS; i++)
            models.put("slimePink" + i, new Model3D(context, "models/slime/slimePink" + i, 2));
        for (int i = 0; i < GOLEM_ANIMATIONS; i++)
            models.put("golem" + i, new Model3D(context, "models/golem/golem" + i, 2));
        for (int i = 0; i < SNAKE_ANIMATIONS; i++)
            models.put("snake" + i, new Model3D(context, "models/snake/snake" + i, 2));
        for (int i = 0; i < FLOWER_ANIMATIONS; i++)
            models.put("flower" + i, new Model3D(context, "models/flower/flower" + i, 2));
        for (int i = 0; i < BAT_ANIMATIONS; i++)
            models.put("bat" + i, new Model3D(context, "models/bat/bat" + i, 2));

        textures.put("tree", new Pair<>("models/tree/tree.png", 3));
        models.put("tree", new Model3D(context, "models/tree/tree", 3));
    }

    public Model3D getModel(String name){
        return models.get(name);
    }
    public Texture getTexture(Context context, String name){
        return new Texture(context, Objects.requireNonNull(textures.get(name)).first, Objects.requireNonNull(textures.get(name)).second);
    }

    public static Model3DManager getInstance(Context context){
        if(manager == null)
            manager = new Model3DManager(context);

        return manager;
    }
}
