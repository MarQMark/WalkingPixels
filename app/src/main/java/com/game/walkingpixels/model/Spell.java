package com.game.walkingpixels.model;

public class Spell {

    private final String path;
    private final String name;
    private final String description;
    private final double castTime;
    private final double maxDamage;

    public Spell(String name, String description, String path, double castTime, double maxDamage){
        this.path = path;
        this.name = name;
        this.description = description;
        this.castTime = castTime;
        this.maxDamage = maxDamage;
    }

    public String getPath(){
        return path;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public double getCastTime() {return castTime;}
    public double getMaxDamage() {return maxDamage;}
}
