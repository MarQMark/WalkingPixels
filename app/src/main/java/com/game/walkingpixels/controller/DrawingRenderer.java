package com.game.walkingpixels.controller;

import android.content.Context;
import android.renderscript.Matrix4f;
import android.util.Log;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.DrawGrid;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.RenderedSpell;
import com.game.walkingpixels.model.Spell;
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
import com.game.walkingpixels.util.vector.Vector3;

import java.util.Random;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class DrawingRenderer extends Renderer {

    private DrawGrid drawGrid;
    private final Enemy enemy;

    private Spell spell;
    private RenderedSpell renderedSpell;

    public DrawingRenderer(Context context, Enemy enemy) {
        super(context);
        this.enemy = enemy;
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3(1.0f, -2.5f, 6.5f), new Vector3(0.0f, 0.0f, -1.0f));
        camera.rotationY = 135;
        camera.rotationX = 55;
        drawGrid = new DrawGrid(64, 0.8f, new Vector2(0.1f, 0.1f));

        World.Block[][][] world = generateWorld();


        registerShader("draw", new Shader(context, "Shaders/DrawGrid.shaders"));
        registerShader("world", new Shader(context, "Shaders/Basic.shaders"));


        shader("draw").bind();
        registerBatch("draw", new Batch(shader("draw").getID(), drawGrid.getSize() * drawGrid.getSize(), drawGridVertex.size, drawGridVertex.getLayout()));
        batch("draw").addVertices("Grid", DrawGridMeshBuilder.generateMesh(drawGrid));


        registerBatch("world", new Batch(shader("world").getID(), 2000, worldVertex.size, worldVertex.getLayout()));
        batch("world").addVertices("ground", WorldMeshBuilder.generateMesh(world, 5, 2));
        batch("world").addVertices("mobs", MobMeshBuilder.generateMesh(world, 5, 2, camera));
        //batch("world").addVertices("spell", new worldVertex[0]);
        batch("world").bind();

        Texture tx = new Texture(context, "textures/texture_atlas.png", 0);
        tx.bind(0);
        Texture tx2 = new Texture(context, "textures/christina.png", 1);
        tx2.bind(1);
        Texture tx3 = new Texture(context, enemy.getSpritePath(), 2);
        tx3.bind(2);

        shader("world").bind();
        shader("world").setUniform1f("u_LightCount", 0);
        shader("world").setUniformMatrix4fv("mvpmatrix", camera.getMVPMatrix());
        shader("world").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);
    }

    @Override
    public void update(double dt) {
        //update DrawGrid
        drawGrid.update(width, height);
        batch("draw").updateVertices("Grid", DrawGridMeshBuilder.generateMesh(drawGrid));

        //lower time while drawing
        if(drawGrid.isDrawing())
            GameState.updateDrawTime(dt / 1000);

        //finished spell
        if(GameState.getDrawTime() == 0.0 && drawGrid.isEnabled()){
            drawGrid.disable();

            float score = drawGrid.calculateScore();
            Log.e("score", "Score: " + score);

            //enemy.damage((int) ((Math.max(score, 0.0) / 100.0) * spell.getMaxDamage()));

            renderedSpell = new RenderedSpell(drawGrid.getDrawnAsBitmap(), 1.0, (int) ((Math.max(score, 0.0) / 100.0) * spell.getMaxDamage()));

            drawGrid.clear();
        }

        if(renderedSpell != null && renderedSpell.isEnabled()){
            if(renderedSpell.isFinished()){
                batch("world").removePart("spell");
                enemy.damage(renderedSpell.getDamage());
            }
            else {
                renderedSpell.update(dt / 1000);
                batch("world").updateVertices("spell", renderedSpell.getVertices(new Vector3(0.0f - 2.0f, 1.0f, 4.0f - 2.0f) , camera));
            }
        }
    }

    @Override
    public void render(double dt) {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //render draw grid
        shader("draw").bind();
        shader("draw").setUniformMatrix4fv("viewmatrix", getViewMatrix());
        batch("draw").bind();
        batch("draw").draw();


        //render World
        shader("world").bind();
        shader("world").setUniformMatrix4fv("mvpmatrix", camera.getMVPMatrix());
        batch("world").bind();
        batch("world").draw();
    }


    public void loadSpell(Spell spell){
        this.spell = spell;
        drawGrid.loadShape(context, spell.getPath());
    }


    private Matrix4f getViewMatrix(){
        Matrix4f view = new Matrix4f();
        view.loadIdentity();
        view.translate(-1.0f + 2 * drawGrid.offset.x, -1.0f + 2 * drawGrid.offset.y * (width / (float)height), 0.0f);
        float scale = 2.0f * drawGrid.scale * (1.0f / drawGrid.getSize());
        view.scale(scale,  scale * (width / (float)height), 1.0f);
        return view;
    }

    private World.Block[][][] generateWorld(){
        World.Block[][][] world = new World.Block[5][5][2];
        for (int x = 0; x < 5; x++){
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 2; z++) {
                    world[x][y][z] = World.Block.AIR;
                }
            }
        }
        world[0][0][0] = World.Block.GRASS;
        world[0][0][1] = World.Block.PLAYER;
        world[0][4][0] = World.Block.GRASS;
        world[0][4][1] = World.Block.SLIME;

        return world;
    }
}
