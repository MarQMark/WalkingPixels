package com.game.walkingpixels.openGL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLUtils;
import java.io.IOException;
import java.io.InputStream;

import static android.opengl.GLES31.*;

public class Texture {

    private final int id;
    private final int width;
    private final int height;

    public Texture(Context context, String path, int slot){

        Bitmap img = loadTexture(context, path);
        width = img.getWidth();
        height = img.getHeight();

        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        img = Bitmap.createBitmap(img,0,0, width, height, matrix,true);

        glActiveTexture(GL_TEXTURE0 + slot);
        int[] ib = new int[1];
        glGenTextures(1, ib, 0);
        id = ib[0];
        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, img, 0);
        glGenerateMipmap(GL_TEXTURE_2D);
        unbind();
    }

    public void bind(){
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, id);
    }
    public void bind(int slot){
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private Bitmap loadTexture(Context context, String path){
        Bitmap img = null;
        try {
            InputStream is = context.getAssets().open(path);
            img = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }
}
