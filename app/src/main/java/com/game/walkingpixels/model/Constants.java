package com.game.walkingpixels.model;

public class Constants {

    public static final int[] tierSpellUsages = new int[]{20, 30, 50};

    public static final int baseStamina = 5000;
    public static final int baseHealth = 100;
    public static final float baseTime = 1.0f;
    public static final float baseStrength = 1.0f;
    public static final float baseMaxXp = 10;

    public static final int enemyBaseHealth = 100;
    public static final int enemyBaseMaxAttack = 15;
    public static final int enemyBaseXp = 9;


    public static float levelFunction(int level){
        return (float) (0.25f * Math.sqrt(level) + 0.75f);
    }
    public static float xpFunction(int level){
        return (float) (0.05f * Math.pow(level - 1, 2) + 1.0f);
    }

    public static float enemyLevelFunction(int level){
        return (float) ((0.75f * Math.sqrt(level) + 2.25f) + Math.pow(2, level / 50.0f) - 3.01f);
    }
    public static float enemyXpFunction(int level){
        return (float) (0.05f * Math.pow(level - 1, 1.5) + 1.0f);
    }
}
