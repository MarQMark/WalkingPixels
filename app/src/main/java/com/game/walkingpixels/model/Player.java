package com.game.walkingpixels.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.util.vector.Vector2;

import java.util.ArrayList;

public class Player {

    private final SharedPreferences sharedPreferences;

    public Player(Context context){
        sharedPreferences = context.getSharedPreferences("Stats", Context.MODE_PRIVATE);
    }

    public void reset(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("level", 1);
        editor.putInt("xp", 0);
        editor.putInt("maxXp", (int)(Constants.baseMaxXp  * Constants.xpFunction(1)));

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

        for (int i = 0; i <= Spell.maxID; i++){
            editor.putInt("spell_" + i, -2);
        }

        editor.apply();
    }

    public void setSpellUsages(int id, int usages){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("spell_" + id, usages);
        editor.apply();
    }
    public void addSpell(int id){
        for (Spell spell : getSpells()){
            if(id == spell.getId())
                return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("spell_" + id, 0);
        editor.apply();
    }
    public boolean hasSpell(int id){
        for (Spell spell : getSpells()){
            if(id == spell.getId())
                return true;
        }
        return false;
    }
    public ArrayList<Spell> getSpells(){
        ArrayList<Spell> spells = new ArrayList<>();
        for (int i = 0; i <= Spell.maxID; i++){
            int usages = sharedPreferences.getInt("spell_" + i, -2);
            if(usages != -2)
                spells.add(new Spell(i, usages));
            else if(i == 0 || i == 4 || i == 8 || i == 12)
                spells.add(new Spell(i, 0));
        }

        return spells;
    }

    public int getLevel() {
        return sharedPreferences.getInt("level", 1);
    }

    public void addXp(int xp){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("xp", getXp() + xp);
        editor.apply();
    }
    public int getXp() {
        return sharedPreferences.getInt("xp", 0);
    }
    public int getMaxXp() {
        return sharedPreferences.getInt("maxXp", (int)(Constants.baseMaxXp  * Constants.xpFunction(1)));
    }

    public void damage(int damage){
        int health = getHealth();
        health -= damage;
        if(health < 0)
            health = 0;
        setHealth(health);
    }
    public int getHealth() {
        return sharedPreferences.getInt("health", getMaxHealth());
    }
    public void setHealth(int health){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("health", health);
        editor.apply();
    }
    public int getMaxHealth() {
        return sharedPreferences.getInt("maxHealth", Constants.baseHealth);
    }
    public int getMaxStamina(){
        return sharedPreferences.getInt("maxStamina", Constants.baseStamina);
    }
    public float getTime() {
        return sharedPreferences.getFloat("time", Constants.baseTime);
    }
    public float getStrength() {
        return sharedPreferences.getFloat("strength", Constants.baseStrength);
    }

    public int getStaminaLevel() {
        return sharedPreferences.getInt("staminaLevel", 1);
    }
    public int getHealthLevel() {
        return sharedPreferences.getInt("healthLevel", 1);
    }
    public int getTimeLevel() {
        return sharedPreferences.getInt("timeLevel", 1);
    }
    public int getStrengthLevel() {
        return sharedPreferences.getInt("strengthLevel", 1);
    }

    public void setLevel(int level){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int xp = getXp();
        for (int i = getLevel(); i < level; i++)
            xp -= Constants.baseMaxXp * Constants.xpFunction(i);

        editor.putInt("level", level);
        editor.putInt("xp", xp);
        editor.putInt("maxXp", (int) (Constants.baseMaxXp * Constants.xpFunction(level)));
        editor.apply();
    }
    public void setStaminaLevel(int level){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("staminaLevel", level);
        editor.putInt("maxStamina", (int) (Constants.baseStamina * Constants.levelFunction(level)));
        editor.apply();
    }
    public void setHealthLevel(int level){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("healthLevel", level);
        editor.putInt("maxHealth", (int) (Constants.baseHealth * Constants.levelFunction(level)));
        editor.apply();
    }
    public void setTimeLevel(int level){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("timeLevel", level);
        editor.putFloat("time", Constants.baseTime * Constants.levelFunction(level));
        editor.apply();
    }
    public void setStrengthLevel(int level){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("strengthLevel", level);
        editor.putFloat("strength", Constants.baseStrength * Constants.levelFunction(level));
        editor.apply();
    }

    public Vector2 getLastSavePosition() {
        return new Vector2(
                sharedPreferences.getFloat("lastSavePositionX", 0.0f),
                sharedPreferences.getFloat("lastSavePositionY", 0.0f));
    }
    public void setLastSavePosition(Vector2 lastSavePosition){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("lastSavePositionX", lastSavePosition.x);
        editor.putFloat("lastSavePositionY", lastSavePosition.y);
        editor.apply();
    }

    public void kill(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("xp", 0);
        editor.apply();
        setHealth(getMaxHealth());
    }

}
