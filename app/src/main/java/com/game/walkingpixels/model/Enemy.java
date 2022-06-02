package com.game.walkingpixels.model;

import android.util.Log;

import java.util.ArrayList;

public class Enemy {

    private int health;
    private final String spritePath;
    private final World.Block type;
    private ArrayList<Attack> attacks = new ArrayList<>();

    public Enemy(World.Block type, int health){
        this.type = type;
        this.health = health;
        this.spritePath = lookupSpritePath();
    }

    private String lookupSpritePath(){
        switch (type){
            case SLIME:
                return "textures/slime.png";
        }

        return "textures/slime.png";
    }

    public String getSpritePath(){
        return spritePath;
    }

    public int getHealth() {
        return health;
    }

    public void damage(int damage){
        health -= damage;
        if(health < 0)
            health = 0;
    }
}
