package com.game.walkingpixels.controller;

import android.content.Context;
import android.renderscript.Matrix4f;

import com.game.walkingpixels.Camera;
import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.DrawGrid;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.GameState;
import com.game.walkingpixels.model.RenderedSpell;
import com.game.walkingpixels.model.Spell;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.openGL.vertices.DrawGridVertex;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.meshbuilder.DrawGridMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.MobMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.BlockMeshBuilder;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class DrawingRenderer extends Renderer {

    private DrawGrid drawGrid;
    private final Enemy enemy;

    private Spell spell;
    private RenderedSpell renderedSpell;

    private Background background;

    private final Sun sun = new Sun();

    private final DrawGridMeshBuilder drawGridMeshBuilder = new DrawGridMeshBuilder();
    private final BlockMeshBuilder blockMeshBuilder = new BlockMeshBuilder();
    private final MobMeshBuilder mobMeshBuilder = new MobMeshBuilder();

    public DrawingRenderer(Context context, Enemy enemy) {
        super(context);
        this.enemy = enemy;
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3(0.5f, -2.0f, 6.0f), new Vector3(0.0f, 0.0f, -1.0f));
        camera.rotationY = 135;
        camera.rotationX = 55;


        //init shader
        registerShader("draw", new Shader(context, "Shaders/DrawGrid.shaders"));
        registerShader("world", new Shader(context, "Shaders/Basic.shaders"));
        registerShader("background", new Shader(context, "Shaders/Background.shaders"));
        registerShader("spell", new Shader(context, "Shaders/Spell.shaders"));


        //init background
        background = new Background(new Texture(context, "textures/clouds.png", 0), 20);
        shader("background").bind();
        shader("background").setUniform1iv("u_Textures", 1, new int[] {0}, 0);
        registerBatch("background", new Batch(shader("background").getID(), 1, PlaneVertex.size, PlaneVertex.getLayout()));
        batch("background").addVertices("Background", background.getVertices());
        batch("background").addTexture(background.getTexture());


        //init spell
        registerBatch("spell", new Batch(shader("spell").getID(), 1, PlaneVertex.size, PlaneVertex.getLayout()));
        shader("spell").bind();
        shader("spell").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);


        //init draw grid
        drawGrid = new DrawGrid(64, 0.8f, new Vector2(0.1f, 0.1f));
        shader("draw").bind();
        registerBatch("draw", new Batch(shader("draw").getID(), drawGrid.getSize() * drawGrid.getSize(), DrawGridVertex.size, DrawGridVertex.getLayout()));
        batch("draw").addVertices("Grid", drawGridMeshBuilder.generateMesh(drawGrid));


        //init world
        World world = new World(0);
        generateWorld(world);
        registerBatch("world", new Batch(shader("world").getID(), 2000, WorldVertex.size, WorldVertex.getLayout()));
        batch("world").addVertices("ground", blockMeshBuilder.generateMesh(world));
        batch("world").addVertices("mobs", mobMeshBuilder.generateMesh(world, camera, true));
        batch("world").addTexture(new Texture(context, "textures/texture_atlas.png", 0));
        batch("world").addTexture(new Texture(context, "textures/christina.png", 1));
        batch("world").addTexture(new Texture(context, enemy.getSpritePath(), 2));

        shader("world").bind();
        shader("world").setUniform1f("u_LightCount", 0);
        shader("world").setUniformMatrix4fv("mvpmatrix", camera.getMVPMatrix());
        shader("world").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);
    }

    @Override
    public void update(double dt) {
        //update in-game time (1 day = 24 min)
        float sunRotation = (float) (System.currentTimeMillis() / 4000.0) % 360;

        //update DrawGrid
        drawGrid.update(width, height);
        batch("draw").updateVertices("Grid", drawGridMeshBuilder.generateMesh(drawGrid));


        //update background
        background.update(dt / 1000, width, height);
        batch("background").updateVertices("Background", background.getVertices());


        //update enemy
        if(enemy.isEnemyTurn())
            enemy.update(dt / 1000);

        //lower time while drawing
        if(drawGrid.isDrawing())
            GameState.updateDrawTime(dt / 1000);

        //finished drawing spell
        if(GameState.getDrawTime() == 0.0 && drawGrid.isEnabled()){
            drawGrid.disable();
            renderedSpell = new RenderedSpell(drawGrid.getDrawnAsBitmap(), 1.0, (int) ((Math.max(drawGrid.calculateScore(), 0.0) / 100.0) * spell.getMaxDamage()));
            batch("spell").addTexture(renderedSpell.getTexture());
            drawGrid.clear();
        }

        //update cast spell
        if(renderedSpell != null && renderedSpell.isEnabled()){
            if(renderedSpell.isFinished()){
                batch("spell").removePart("spell");
                enemy.damage(renderedSpell.getDamage());
                enemy.setEnemyTurn(true);
            }
            else {
                renderedSpell.update(dt / 1000);
                batch("spell").updateVertices("spell", renderedSpell.getVertices(new Vector3(1.0f - 2.0f, 1.0f, 4.0f - 2.0f) , camera));
            }
        }
    }

    @Override
    public void render(double dt) {
        //set background color according to the time
        Vector4 clearColor = sun.getColor();
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        glClear(GL_COLOR_BUFFER_BIT);

        //render background
        shader("background").bind();
        batch("background").bind();
        batch("background").draw();


        glClear(GL_DEPTH_BUFFER_BIT);
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

        //render spell
        if(renderedSpell != null && renderedSpell.isEnabled()) {
            shader("spell").bind();
            shader("spell").setUniformMatrix4fv("mvpmatrix", camera.getMVPMatrix());
            shader("spell").setUniform1f("alpha", renderedSpell.getAlpha());
            batch("spell").bind();
            batch("spell").draw();
        }
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

    private void generateWorld(World world){
        world.setWorldSize(5);
        world.clear();
        for (int x = 0; x < 5; x++){
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < world.getWorldMaxHeight(); z++) {
                    world.getBlockGrid()[x][y][z] = Block.AIR;
                }
            }
        }
        world.getBlockGrid()[1][0][0] = Block.GRASS;
        world.getBlockGrid()[1][0][1] = Block.PLAYER;
        world.getEnemyGrid()[1 + world.getDespawnRadius()][4 + world.getDespawnRadius()] = enemy;

        world.getBlockGrid()[1][1][0] = Block.GRASS;
        world.getBlockGrid()[1][2][0] = Block.GRASS;
        world.getBlockGrid()[1][3][0] = Block.GRASS;
        world.getBlockGrid()[1][4][0] = Block.GRASS;

        world.getBlockGrid()[2][1][0] = Block.GRASS;
        world.getBlockGrid()[2][2][0] = Block.GRASS;
        world.getBlockGrid()[2][3][0] = Block.GRASS;

        world.getBlockGrid()[0][1][0] = Block.GRASS;
        world.getBlockGrid()[0][2][0] = Block.GRASS;
        world.getBlockGrid()[0][3][0] = Block.GRASS;
    }
}
