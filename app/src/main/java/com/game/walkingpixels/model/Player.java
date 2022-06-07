package com.game.walkingpixels.model;

import android.content.Context;
import android.content.SharedPreferences;

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

    public Player(Context context){
        sharedPreferences = context.getSharedPreferences("Stats", Context.MODE_PRIVATE);
        loadStats();
        health = maxHealth;
    }

    private void loadStats(){
        level = sharedPreferences.getInt("level", 1);
        xp = sharedPreferences.getInt("xp", 0);
        maxXp = sharedPreferences.getInt("maxXp", 10);
        maxStamina = sharedPreferences.getInt("maxStamina", 10000);
        maxHealth = sharedPreferences.getInt("maxHealth", 100);
        time = sharedPreferences.getFloat("time", 1.0f);
        strength = sharedPreferences.getFloat("strength", 1.0f);
    }
    private void saveStats(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("level", level);
        editor.putInt("xp", xp);
        editor.putInt("maxXp", maxXp);
        editor.putInt("maxStamina", maxStamina);
        editor.putInt("maxHealth", maxHealth);
        editor.putFloat("time", time);
        editor.putFloat("strength", strength);
        editor.apply();
    }



    public void damage(int damage){
        health -= damage;
    }
    public int getHealth() {
        return health;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public int getMaxStamina(){
        return maxStamina;
    }
}
