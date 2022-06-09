package com.game.walkingpixels.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Enemy implements Serializable {

    private int health;
    private final String spritePath;
    private final Block type;
    private final ArrayList<Attack> attacks = new ArrayList<>();

    private boolean isEnemyTurn = false;
    private static final double maxAttackDelay = 1;
    private double attackDelay = maxAttackDelay;

    public Enemy(Block type, int health){
        this.type = type;
        this.health = health;
        this.spritePath = lookupSpritePath();
    }

    public void addAttack(Attack attack){
        for(int i = 0; i < attack.getWeight(); i++)
            attacks.add(attack);
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

    public boolean isEnemyTurn(){
        return isEnemyTurn;
    }
    public void setEnemyTurn(boolean isEnemyTurn){
        if(!isEnemyTurn)
            attackDelay = maxAttackDelay;

        this.isEnemyTurn = isEnemyTurn;
    }

    public void damage(int damage){
        health -= damage;
        if(health < 0)
            health = 0;
    }

    public int attack(){
        Random random = new Random();
        return attacks.get(Math.abs(random.nextInt()) % attacks.size()).getDamage();
    }

    public double getAttackDelay() {
        return attackDelay;
    }

    public void update(double dt){
        attackDelay -= dt;
        if(attackDelay < 0.0)
            attackDelay = 0.0;
    }

    public int getXp(){
        return 10;
    }
}
