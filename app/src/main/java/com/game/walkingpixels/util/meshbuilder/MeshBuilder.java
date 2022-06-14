package com.game.walkingpixels.util.meshbuilder;

import com.game.walkingpixels.model.atlas.AnimationTextureAtlas;
import com.game.walkingpixels.model.atlas.GridTextureAtlas;

import java.util.HashMap;

public abstract class MeshBuilder {

    private final HashMap<String, GridTextureAtlas> gridTextureAtlases = new HashMap<>();
    private final HashMap<String, AnimationTextureAtlas> animationTextureAtlases = new HashMap<>();

    public void registerGridTextureAtlas(String name, GridTextureAtlas gridTextureAtlas){
        gridTextureAtlases.put(name, gridTextureAtlas);
    }
    public GridTextureAtlas gridTextureAtlas(String name){
        return gridTextureAtlases.get(name);
    }
    public void registerAnimationTextureAtlas(String name, AnimationTextureAtlas animationTextureAtlas){
        animationTextureAtlases.put(name, animationTextureAtlas);
    }
    public AnimationTextureAtlas animationTextureAtlas(String name){
        return animationTextureAtlases.get(name);
    }
}
