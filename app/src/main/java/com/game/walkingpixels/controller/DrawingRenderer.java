package com.game.walkingpixels.controller;

import android.content.Context;
import android.renderscript.Matrix4f;
import android.util.Log;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.DrawGrid;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.drawGridVertex;
import com.game.walkingpixels.openGL.vertices.worldVertex;
import com.game.walkingpixels.util.meshbuilder.DrawGridMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.MobMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.WorldMeshBuilder;
import com.game.walkingpixels.util.vector.Vector2;

import java.util.Random;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class DrawingRenderer extends Renderer {

    public DrawGrid drawGrid;
    private final Enemy enemy;

    public DrawingRenderer(Context context, Enemy enemy) {
        super(context);
        this.enemy = enemy;
    }

    @Override
    public void init() {
        drawGrid = new DrawGrid(64, 0.8f, new Vector2(0.1f, 0.1f));

        World.Block[][][] world = new World.Block[4][4][2];
        for (int x = 0; x < 4; x++){
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 2; z++) {
                    world[x][y][z] = World.Block.AIR;
                }
            }
        }
        world[0][0][0] = World.Block.GRASS;
        world[0][0][1] = World.Block.PLAYER;
        world[0][3][0] = World.Block.GRASS;
        world[0][3][1] = World.Block.SLIME;


        registerShader("draw", new Shader(context, "Shaders/DrawGrid.shaders"));
        registerShader("world", new Shader(context, "Shaders/Basic.shaders"));


        shader("draw").bind();
        registerBatch("draw", new Batch(shader("draw").getID(), drawGrid.getSize() * drawGrid.getSize(), drawGridVertex.size, drawGridVertex.getLayout()));
        batch("draw").addVertices("Grid", DrawGridMeshBuilder.generateMesh(drawGrid));


        registerBatch("world", new Batch(shader("world").getID(), 2000, worldVertex.size, worldVertex.getLayout()));
        batch("world").addVertices("ground", WorldMeshBuilder.generateMesh(world, 4, 2));
        batch("world").addVertices("mobs", MobMeshBuilder.generateMesh(world, 4, 2));
        batch("world").bind();

        Texture tx = new Texture(context, "textures/texture_atlas.png", 0);
        tx.bind(0);
        Texture tx2 = new Texture(context, "textures/christina.png", 1);
        tx2.bind(1);
        Texture tx3 = new Texture(context, enemy.getSpritePath(), 2);
        tx3.bind(2);


        Matrix4f cameraMatrix = Camera.getMVPMatrix();
        //cameraMatrix.translate(0.0f, -10.0f, 0.0f);

        shader("world").bind();
        shader("world").setUniform1f("u_LightCount", 0);
        shader("world").setUniformMatrix4fv("mvpmatrix", cameraMatrix);
        shader("world").setUniform1iv("u_Textures", 3, new int[] {0, 1, 2}, 0);
    }

    @Override
    public void update(double dt) {
        drawGrid.update(width, height);
        batch("draw").updateVertices("Grid", DrawGridMeshBuilder.generateMesh(drawGrid));

        if(drawGrid.isDrawing())
            GameState.updateDrawTime(dt / 1000);

        if(GameState.getDrawTime() == 0.0){
            drawGrid.disable();

            float score = drawGrid.calculateScore();
            Log.e("score", "Score: " + score);
            GameState.setDrawTime(5.0);
            drawGrid.clear();
        }
    }

    @Override
    public void render(double dt) {
        glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //render draw grid
        shader("draw").bind();
        shader("draw").setUniformMatrix4fv("viewmatrix", getViewMatrix());
        batch("draw").bind();
        batch("draw").draw();


        //render World
        shader("world").bind();
        batch("world").bind();
        batch("world").draw();
    }


    private Matrix4f getViewMatrix(){
        Matrix4f view = new Matrix4f();
        view.loadIdentity();
        view.translate(-1.0f + 2 * drawGrid.offset.x, -1.0f + 2 * drawGrid.offset.y * (width / (float)height), 0.0f);
        float scale = 2.0f * drawGrid.scale * (1.0f / drawGrid.getSize());
        view.scale(scale,  scale * (width / (float)height), 1.0f);
        return view;
    }
}
