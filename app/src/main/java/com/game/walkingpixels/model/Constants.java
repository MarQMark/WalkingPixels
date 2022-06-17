package com.game.walkingpixels.model;

import com.game.walkingpixels.controller.Map;

public class Constants {

    public static final int[] tierSpellUsages = new int[]{2, 3, 4};

    public static final int baseStamina = 5000;
    public static final int baseHealth = 100;
    public static final float baseTime = 1.0f;
    public static final float baseStrength = 1.0f;

    public static final float baseMaxXp = 10;

    public static float levelFunction(int level){
        return (float) (0.25f * Math.sqrt(level) + 0.75f);
    }
    public static float xpFunction(int level){
        return (float) (0.05f * Math.pow(level - 1, 2) + 1.0f);
    }
}
