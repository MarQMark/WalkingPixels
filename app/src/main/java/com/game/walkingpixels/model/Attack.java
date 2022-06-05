package com.game.walkingpixels.model;

import java.util.Random;

public class Attack {

    private final int maxDamage;
    private final int minDamage;
    private final int weight;

    public Attack(int maxDamage, int minDamage, int weight){
        this.maxDamage = maxDamage;
        this.minDamage = minDamage;
        this.weight = weight;
    }

    public int getDamage(){
        Random random = new Random();
        return minDamage + Math.abs(random.nextInt()) % (maxDamage - minDamage);
    }

    public int getWeight(){
        return weight;
    }
}
