package com.game.walkingpixels.model.atlas;


import com.game.walkingpixels.util.vector.Vector2;

public class GridTextureAtlas {

    private final int width;
    private final int height;
    private final int textureSize;
    private final int texturesPerRow;
    private final int texturesPerColumn;

    public GridTextureAtlas(int width, int height, int textureSize){
        this.width = width;
        this.height = height;
        this.textureSize = textureSize;
        texturesPerRow = width / textureSize;
        texturesPerColumn = height / textureSize;
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

    private Vector2 getTextureLocation(int id){
        Vector2 location = new Vector2();
        location.x = id % texturesPerRow;
        location.y = id / texturesPerRow;

        return location;
    }

    public Vector2[] getTextureCoordinates(int id){
        Vector2[] textureCoordinates = new Vector2[4];

        float textureWidth = 1.0f / (float) texturesPerRow;
        float textureHeight = 1.0f / (float) texturesPerColumn;
        Vector2 location = getTextureLocation(id);
        location.x *= textureWidth;
        location.y *= textureHeight;

        textureCoordinates[0] = new Vector2(location.x, 1 - location.y - textureHeight);
        textureCoordinates[1] = new Vector2(location.x + textureWidth, 1 - location.y - textureHeight);
        textureCoordinates[2] = new Vector2(location.x, 1 - location.y);
        textureCoordinates[3] = new Vector2(location.x + textureWidth, 1 - location.y);

        return textureCoordinates;
    }
}
