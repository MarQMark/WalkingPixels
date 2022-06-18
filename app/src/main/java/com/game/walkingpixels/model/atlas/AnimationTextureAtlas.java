package com.game.walkingpixels.model.atlas;

import com.game.walkingpixels.util.vector.Vector2;

import java.util.ArrayList;

public class AnimationTextureAtlas {

    private int width = 0;
    private int height = 0;

    private final ArrayList<Animation> animations = new ArrayList<>();

    public AnimationTextureAtlas(){
    }

    public void addAnimation(int frameWidth, int frameHeight, int numberOfFrames){
        height += frameHeight;
        width = Math.max(width, frameWidth * numberOfFrames);

        int yOffset = 0;
        if(animations.size() != 0)
            yOffset = animations.get(animations.size() - 1).yOffset + animations.get(animations.size() - 1).frameHeight;

        animations.add(new Animation(yOffset, frameWidth, frameHeight, numberOfFrames));
    }

    public Vector2[] getTextureCoordinates(int id, int frame){
        Vector2[] textureCoordinates = new Vector2[4];

        Vector2 position = new Vector2( (frame * (float)animations.get(id).frameWidth) / width,  (float)animations.get(id).yOffset / height);
        float textureWidth = animations.get(id).frameWidth / (float)width;
        float textureHeight = animations.get(id).frameHeight / (float)height;

        textureCoordinates[0] = new Vector2(position.x, 1 - position.y - textureHeight);
        textureCoordinates[1] = new Vector2(position.x + textureWidth, 1 - position.y - textureHeight);
        textureCoordinates[2] = new Vector2(position.x, 1 - position.y);
        textureCoordinates[3] = new Vector2(position.x + textureWidth, 1 - position.y);

        return textureCoordinates;
    }

    public int getAnimationNumberOfFrames(int id){
        return animations.get(id).numberOfFrames;
    }

    private static class Animation{

        public final int yOffset;
        public final int frameWidth;
        public final int frameHeight;
        public final int numberOfFrames;

        public Animation(int yOffset, int frameWidth, int frameHeight, int numberOfFrames){
            this.yOffset = yOffset;
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;
            this.numberOfFrames = numberOfFrames;
        }
    }
}
