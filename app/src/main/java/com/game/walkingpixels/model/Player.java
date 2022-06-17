package com.game.walkingpixels.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.util.vector.Vector2;

import java.util.ArrayList;

public class Player {

    private final ArrayList<Spell> spells = new ArrayList<>();
    private int health;

    //stats
    private final SharedPreferences sharedPreferences;
    private int level;
    private int xp;
    private int maxXp;

    private int maxStamina;
    private int maxHealth;
    private float time;
    private float strength;

    private int staminaLevel;
    private int healthLevel;
    private int timeLevel;
    private int strengthLevel;

    private Vector2 lastSavePosition;

    public Player(Context context){
        sharedPreferences = context.getSharedPreferences("Stats", Context.MODE_PRIVATE);
        loadStats();
    }

    public void reset(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("level", 1);
        editor.putInt("xp", 0);
        editor.putInt("maxXp", (int) Constants.xpFunction(1));

        editor.putInt("maxStamina", Constants.baseStamina);
        editor.putInt("maxHealth", Constants.baseHealth);
        editor.putFloat("time", Constants.baseTime);
        editor.putFloat("strength", Constants.baseStrength);

        editor.putInt("staminaLevel", 1);
        editor.putInt("healthLevel", 1);
        editor.putInt("timeLevel", 1);
        editor.putInt("strengthLevel", 1);

        editor.putInt("health", Constants.baseHealth);
        editor.putFloat("lastSavePositionX", 0.0f);
        editor.putFloat("lastSavePositionY", 0.0f);

        for (Spell spell : spells)
            editor.putInt("spell_" + spell.getId(), -2);

        editor.apply();
    }

    public void loadStats(){
        level = sharedPreferences.getInt("level", 1);
        xp = sharedPreferences.getInt("xp", 0);
        maxXp = sharedPreferences.getInt("maxXp", (int) Constants.xpFunction(1));

        maxStamina = sharedPreferences.getInt("maxStamina", Constants.baseStamina);
        maxHealth = sharedPreferences.getInt("maxHealth", Constants.baseHealth);
        time = sharedPreferences.getFloat("time", Constants.baseTime);
        strength = sharedPreferences.getFloat("strength", Constants.baseStrength);

        staminaLevel = sharedPreferences.getInt("staminaLevel", 1);
        healthLevel = sharedPreferences.getInt("healthLevel", 1);
        timeLevel = sharedPreferences.getInt("timeLevel", 1);
        strengthLevel = sharedPreferences.getInt("strengthLevel", 1);

        health = sharedPreferences.getInt("health", maxHealth);
        lastSavePosition= new Vector2(
                sharedPreferences.getFloat("lastSavePositionX", 0.0f),
                sharedPreferences.getFloat("lastSavePositionY", 0.0f));

        spells.clear();
        for (int i = 0; i <= Spell.maxID; i++){
            int usages = sharedPreferences.getInt("spell_" + i, -2);
            if(usages != -2)
                spells.add(new Spell(i, usages));
        }

        addSpell(0);
        addSpell(4);
        addSpell(8);
        addSpell(12);
    }
    public void saveStats(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("level", level);
        editor.putInt("xp", xp);
        editor.putInt("maxXp", maxXp);

        editor.putInt("maxStamina", maxStamina);
        editor.putInt("maxHealth", maxHealth);
        editor.putFloat("time", time);
        editor.putFloat("strength", strength);

        editor.putInt("staminaLevel", staminaLevel);
        editor.putInt("healthLevel", healthLevel);
        editor.putInt("timeLevel", timeLevel);
        editor.putInt("strengthLevel", strengthLevel);

        editor.putInt("health", health);
        editor.putFloat("lastSavePositionX", lastSavePosition.x);
        editor.putFloat("lastSavePositionY", lastSavePosition.x);

        for (Spell spell : spells)
            editor.putInt("spell_" + spell.getId(), spell.getUsages());

        editor.apply();
    }


    public void addSpell(int id){
        for (Spell spell : spells){
            if(id == spell.getId())
                return;
        }

        spells.add(new Spell(id, 0));
    }

    public boolean hasSpell(int id){
        for (Spell spell : spells){
            if(id == spell.getId())
                return true;
        }
        return false;
    }

    public ArrayList<Spell> getSpells(){
        return spells;
    }

    public int getLevel() {
        return level;
    }

    public void addXp(int xp){
        this.xp += xp;
    }
    public int getXp() {
        return xp;
    }
    public int getMaxXp() {
        return maxXp;
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
    public float getTime() {
        return time;
    }
    public float getStrength() {
        return strength;
    }

    public int getStaminaLevel() {
        return staminaLevel;
    }
    public int getHealthLevel() {
        return healthLevel;
    }
    public int getTimeLevel() {
        return timeLevel;
    }
    public int getStrengthLevel() {
        return strengthLevel;
    }

    public void setLevel(int level){
        for (int i = this.level; i < level; i++)
            xp -= Constants.baseMaxXp * Constants.xpFunction(i);

        this.level = level;
        maxXp = (int) (Constants.baseMaxXp * Constants.xpFunction(level));
    }
    public void setStaminaLevel(int level){
        staminaLevel = level;
        maxStamina = (int) (Constants.baseStamina * Constants.levelFunction(level));
    }
    public void setHealthLevel(int level){
        healthLevel = level;
        maxHealth = (int) (Constants.baseHealth * Constants.levelFunction(level));
    }
    public void setTimeLevel(int level){
        timeLevel = level;
        time = Constants.baseTime * Constants.levelFunction(level);
    }
    public void setStrengthLevel(int level){
        strengthLevel = level;
        strength = Constants.baseStrength * Constants.levelFunction(level);
    }

    public Vector2 getLastSavePosition() {
        return lastSavePosition;
    }
    public void kill(){
        xp = 0;
        health = maxHealth;
    }

    public void setLastSavePosition(Vector2 lastSavePosition){
        this.lastSavePosition = lastSavePosition;
    }
}
