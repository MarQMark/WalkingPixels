package com.game.walkingpixels;

import android.renderscript.Matrix4f;

import com.game.walkingpixels.util.vector.Vector3;

public class Camera {

    public final Vector3 position;
    public final Vector3 orientation;
    public final Vector3 up = new Vector3( 0.0f, 1.0f, 0.0f);

    private static final Matrix4f projection = new Matrix4f();

    public float fov = 90;
    public float nearPlane = 0.1f;
    public float farPlane = 1000f;

    public float rotationX = 0;
    public float rotationY = 0;
    public float rotationZ = 0;

    public Camera(Vector3 position, Vector3 orientation){
        this.position = position;
        //orientation.normalize();
        this.orientation = orientation;
    }

    public void setAspectRatio(float ratio){
        projection.loadIdentity();
        projection.loadPerspective(fov, ratio, nearPlane, farPlane);
    }

    public Matrix4f getMVPMatrix(){
        Matrix4f view = lookAt(position, position.add(orientation), up);
        Matrix4f mvp = new Matrix4f(projection.getArray());
        mvp.multiply(view);
        mvp.rotate(rotationX, 1.0f, 0.0f, 0.0f);
        mvp.rotate(rotationY, 0.0f, 1.0f, 0.0f);
        mvp.rotate(rotationZ, 0.0f, 0.0f, 1.0f);
        return mvp;
    }

    public static Matrix4f lookAt(Vector3 eye, Vector3 center, Vector3 up){
        Matrix4f M = new Matrix4f();

        Vector3 f = center.sub(eye);
        f.normalize();

        Vector3 u = new Vector3(up);
        u.normalize();

        Vector3 s = f.cross(u);
        s.normalize();

        M.loadIdentity();
        M.set( 0, 0,  s.x);
        M.set( 1, 0,  s.y);
        M.set( 2, 0,  s.z);
        M.set( 0, 1,  u.x);
        M.set( 1, 1,  u.y);
        M.set( 2, 1,  u.z);
        M.set( 0, 2, -f.x);
        M.set( 1, 2, -f.y);
        M.set( 2, 2, -f.z);
        M.set( 3, 0, -s.dot(eye));
        M.set( 3, 1, -u.dot(eye));
        M.set( 3, 2,  f.dot(eye));

        return M;
    }

}
