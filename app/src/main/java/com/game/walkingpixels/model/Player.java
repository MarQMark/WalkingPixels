package com.game.walkingpixels.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.util.vector.Vector2;

import java.util.ArrayList;

public class Player {

    public ArrayList<Spell> spells = new ArrayList<>();
    private int health;

    //stats
    SharedPreferences sharedPreferences;
    private int level;
    private int xp;
    private int maxXp;
    private int maxStamina;
    private int maxHealth;
    private float time;
    private float strength;
    private Vector2 lastSavePosition;

    public Player(Context context){
        sharedPreferences = context.getSharedPreferences("Stats", Context.MODE_PRIVATE);
        loadStats();
    }

    private void loadStats(){
        level = sharedPreferences.getInt("level", 1);
        xp = sharedPreferences.getInt("xp", 0);
        maxXp = sharedPreferences.getInt("maxXp", 10);
        maxStamina = sharedPreferences.getInt("maxStamina", 10000);
        maxHealth = sharedPreferences.getInt("maxHealth", 100);
        health = sharedPreferences.getInt("health", maxHealth);
        time = sharedPreferences.getFloat("time", 1.0f);
        strength = sharedPreferences.getFloat("strength", 1.0f);
        lastSavePosition= new Vector2(
                sharedPreferences.getFloat("lastSavePositionX", 0.0f),
                sharedPreferences.getFloat("lastSavePositionY", 0.0f));
    }
    public void saveStats(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("level", level);
        editor.putInt("xp", xp);
        editor.putInt("maxXp", maxXp);
        editor.putInt("maxStamina", maxStamina);
        editor.putInt("maxHealth", maxHealth);
        editor.putInt("health", health);
        editor.putFloat("time", time);
        editor.putFloat("strength", strength);
        editor.putFloat("lastSavePositionX", lastSavePosition.x);
        editor.putFloat("lastSavePositionY", lastSavePosition.x);
        editor.apply();
    }


    public void addXp(int xp){
        this.xp = xp;
    }
    public void damage(int damage){
        health -= damage;
        if(health < 0)
            health = 0;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health){
        this.health = health;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public int getMaxStamina(){
        return maxStamina;
    }
    public Vector2 getLastSavePosition() {
        return lastSavePosition;
    }
    public void kill(){
        xp = 0;
        health = maxHealth;
    }
}
