package com.game.walkingpixels.util.vector;

import android.renderscript.Matrix4f;

public class MatrixAdapter {

    public static Vector3 multiply(Matrix4f m, Vector3 v){
        Vector3 result = new Vector3(v);
        result.x =
                m.get(0, 0) * v.x +
                m.get(0, 1) * v.y +
                m.get(0, 2) * v.z +
                m.get(0, 3);
        result.y =
                m.get(1, 0) * v.x +
                m.get(1, 1) * v.y +
                m.get(1, 2) * v.z +
                m.get(1, 3);
        result.z =
                m.get(2, 0) * v.x +
                m.get(2, 1) * v.y +
                m.get(2, 2) * v.z +
                m.get(2, 3);

        return result;
    }
}
