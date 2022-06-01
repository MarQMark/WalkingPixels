package com.game.walkingpixels.model;

public class Spell {

    private int icon;
    private String name;
    private String description;

    public Spell(int icon, String name, String description){
        this.icon = icon;
        this.name = name;
        this.description = description;
    }

    public int getIcon(){
        return icon;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
}
