package com.game.walkingpixels.model;

import java.util.ArrayList;

public class Enemy {

    public int health;
    private ArrayList<Attack> attacks = new ArrayList<>();
    private final String spritePath;

    public Enemy(String spritePath){
        this.spritePath = spritePath;
    }

    public String getSpritePath(){
        return spritePath;
    }
}
