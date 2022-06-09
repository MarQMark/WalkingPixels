package com.game.walkingpixels.model;


import com.game.walkingpixels.util.vector.Vector2;

public class TextureAtlas {

    private final int width;
    private final int height;
    private final int textureSize;
    private final int texturesPerRow;

    public TextureAtlas(int width, int height, int textureSize){
        this.width = width;
        this.height = height;
        this.textureSize = textureSize;
        texturesPerRow = width / textureSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureSize() {
        return textureSize;
    }

    public int getTexturesPerRow(){
        return texturesPerRow;
    }

    public Vector2 getTextureLocation(int id){
        Vector2 location = new Vector2();
        location.x = id % texturesPerRow;
        location.y = id / texturesPerRow;

        return location;
    }
}
