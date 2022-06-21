package com.game.walkingpixels.model;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.util.vector.Vector3;

public class RenderedSpell {

    private final int TEXTURE_SLOT = 3;

    private final Texture texture;
    private final int damage;
    private final double initialTTL;
    private double TTL;

    private boolean isEnabled = true;
    private boolean isFinished = false;

    public RenderedSpell(Bitmap spell, double TTL, int damage){
        this.texture = new Texture(generateGlow(spell), TEXTURE_SLOT);
        this.initialTTL = TTL;
        this.TTL = TTL;
        this.damage = damage;
    }

    private Bitmap generateGlow(Bitmap spell){
        Bitmap scaledSpell = Bitmap.createScaledBitmap(spell, spell.getWidth() * 4, spell.getHeight() * 4, false);

        //https://stackoverflow.com/questions/12157412/android-how-to-make-an-icon-glow-on-touch/12162080#12162080
        // An added margin to the initial image
        int margin = 100;
        int halfMargin = margin / 2;
        // the glow radius
        int glowRadius = 25;
        // the glow color
        int glowColor = Color.rgb(200, 50, 0);
        // extract the alpha from the source image
        Bitmap alpha = scaledSpell.extractAlpha();
        // The output bitmap (with the icon + glow)
        Bitmap glowingSpell =  Bitmap.createBitmap(scaledSpell.getWidth() + margin, scaledSpell.getHeight() + margin, Bitmap.Config.ARGB_8888);
        // The canvas to paint on the image
        Canvas canvas = new Canvas(glowingSpell);
        Paint paint = new Paint();
        paint.setColor(glowColor);
        // outer glow
        paint.setMaskFilter(new BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER));//For Inner glow set Blur.INNER
        //just don't look at the next few lines below. It works and I don't wanna improve it. Too bad!
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint);
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint);
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint);
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint);
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint);
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint);
        // original icon
        canvas.drawBitmap(scaledSpell, halfMargin, halfMargin, null);

        return glowingSpell;
    }

    public void update(double dt){
        TTL -= dt;
        if(TTL < 0.0){
            TTL = 0.0;
            isFinished = true;
        }
    }

    public float getAlpha(){
        return (float) ((initialTTL - TTL) / (TTL * 0.6));
    }

    public PlaneVertex[] getVertices(Vector3 position, Camera camera){
        PlaneVertex[] vertices = new PlaneVertex[4];

        Vector3 center = new Vector3(position.x + 0.5f, position.y, position.z + 0.5f);

        float width = 1.0f;
        float height = 1.0f;

        float deltaX = (float) Math.cos(Math.toRadians(camera.rotationY)) * (width / 2.0f);
        float deltaZ = (float) Math.sin(Math.toRadians(camera.rotationY)) * (width / 2.0f);

        Vector3 normals = new Vector3(-deltaZ, 0.0f, deltaX);
        normals.normalize();

        Vector3 centerTop = new Vector3(normals);
        centerTop.scale((float) (-Math.cos(Math.toRadians(camera.rotationX)) * height));
        centerTop.y += Math.sin(Math.toRadians(camera.rotationX)) * height;

        Vector3 distance = new Vector3(normals);
        center = center.add(distance);

        vertices[0] = new PlaneVertex(
                new float[]{ center.x - deltaX, position.y, center.z - deltaZ },
                new float[]{ 0.0f, 0.0f },
                TEXTURE_SLOT);

        vertices[1] = new PlaneVertex(
                new float[]{ center.x + deltaX, position.y, center.z + deltaZ },
                new float[]{ 1.0f, 0.0f },
                TEXTURE_SLOT);

        vertices[2] = new PlaneVertex(
                new float[]{ center.x + centerTop.x - deltaX, position.y + centerTop.y, center.z + centerTop.z - deltaZ },
                new float[]{ 0.0f, 1.0f },
                TEXTURE_SLOT);

        vertices[3] = new PlaneVertex(
                new float[]{ center.x + centerTop.x + deltaX, position.y + centerTop.y, center.z + centerTop.z + deltaZ },
                new float[]{ 1.0f, 1.0f },
                TEXTURE_SLOT);

        return vertices;
    }

    public boolean isFinished(){
        if(isFinished)
            isEnabled = false;

        return isFinished;
    }

    public boolean isEnabled(){
        return isEnabled;
    }

    public Texture getTexture(){
        return texture;
    }

    public void bind(){
        texture.bind(TEXTURE_SLOT);
    }
    public void unbind(){
        texture.unbind();
    }

    public int getDamage(){
        return damage;
    }
}
