package com.game.walkingpixels.controller;

import android.content.Context;
import android.renderscript.Matrix4f;
import android.util.Log;

import com.game.walkingpixels.model.DrawGrid;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.vertices.drawGridVertex;
import com.game.walkingpixels.util.meshbuilder.DrawGridMeshBuilder;
import com.game.walkingpixels.util.vector.Vector2;

import java.util.Random;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class DrawingRenderer extends Renderer {

    DrawGrid drawGrid;

    public DrawingRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        drawGrid = new DrawGrid(64, 0.8f, new Vector2(0.1f, 0.1f));
        drawGrid.loadShape(context, "shapes/triangle.png");

        registerShader("draw", new Shader(context, "Shaders/DrawGrid.shaders"));
        shader("draw").bind();

        registerBatch("draw", new Batch(shader("draw").getID(), drawGrid.getSize() * drawGrid.getSize(), drawGridVertex.size, drawGridVertex.getLayout()));
        batch("draw").addVertices("Grid", DrawGridMeshBuilder.generateMesh(drawGrid));
    }

    @Override
    public void update(double dt) {
        drawGrid.update(width, height);
        batch("draw").updateVertices("Grid", DrawGridMeshBuilder.generateMesh(drawGrid));

        GameState.updateDrawTime(dt / 1000);
        if(GameState.getDrawTime() == 0.0){
            float score = drawGrid.calculateScore();
            Log.e("score", "Score: " + score);
            GameState.setDrawTime(5.0);
            drawGrid.clear();

            Random r = new Random();
            int ri = r.nextInt() % 3;
            String a = (ri == 0) ? "triangle" : (ri == 1) ? "fire" : "circle";

            drawGrid.loadShape(context, "shapes/" + a + ".png");
        }
    }

    @Override
    public void render(double dt) {
        Matrix4f view = new Matrix4f();
        view.loadIdentity();
        view.translate(-1.0f + 2 * drawGrid.offset.x, -1.0f + 2 * drawGrid.offset.y * (width / (float)height), 0.0f);
        float scale = 2.0f * drawGrid.scale * (1.0f / drawGrid.getSize());
        view.scale(scale,  scale * (width / (float)height), 1.0f);

        shader("draw").bind();

        shader("draw").setUniformMatrix4fv("viewmatrix", view);

        batch("draw").bind();
        glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        batch("draw").draw();
    }
}
