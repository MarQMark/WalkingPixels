package com.game.walkingpixels.model;

public class Spell {

    private final String iconPath;
    private final String name;
    private final String description;
    private final String shapePath;

    public Spell(String name, String description, String iconPath, String shapePath){
        this.iconPath = iconPath;
        this.name = name;
        this.description = description;
        this.shapePath = shapePath;
    }

    public String getIconPath(){
        return iconPath;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public String getShapePath() { return  shapePath;}
}
