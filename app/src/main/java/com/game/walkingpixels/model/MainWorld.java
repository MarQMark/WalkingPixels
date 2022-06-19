package com.game.walkingpixels.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.controller.MainMenu;

public class MainWorld {

    private static World world;

    public static void init(Context context){
        if(world == null){
            SharedPreferences sharedPreferences = context.getSharedPreferences("World", Context.MODE_PRIVATE);
            float seed = sharedPreferences.getFloat("seed", Float.POSITIVE_INFINITY);
            if(seed == Float.POSITIVE_INFINITY){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                world = new World();
                editor.putFloat("seed", world.getSeed());
                editor.apply();
            }
            else{
                world = new World(seed);
                world.setPosition(
                        sharedPreferences.getInt("positionX", 0),
                        sharedPreferences.getInt("positionY", 0));
            }
        }
    }

    public static World getWorld(){
        return world;
    }

    public static void reset(Context context){
        SharedPreferences.Editor worldEditor = context.getSharedPreferences("World", Context.MODE_PRIVATE).edit();
        worldEditor.putFloat("seed", Float.POSITIVE_INFINITY);
        worldEditor.putFloat("rotation", 0f);
        worldEditor.putInt("positionX", 0);
        worldEditor.putInt("positionY", 0);
        worldEditor.apply();
        world = null;
        init(context);
    }
}
