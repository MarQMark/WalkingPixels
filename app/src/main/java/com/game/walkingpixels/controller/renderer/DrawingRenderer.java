package com.game.walkingpixels.controller.renderer;

import android.content.Context;
import android.content.SharedPreferences;
import android.renderscript.Matrix4f;

import com.game.walkingpixels.model.Camera;
import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.model.Block;
import com.game.walkingpixels.model.DrawGrid;
import com.game.walkingpixels.model.DrawTimer;
import com.game.walkingpixels.model.Enemy;
import com.game.walkingpixels.model.Model3DManager;
import com.game.walkingpixels.model.Player;
import com.game.walkingpixels.model.RenderedSpell;
import com.game.walkingpixels.model.Spell;
import com.game.walkingpixels.model.Sun;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.openGL.vertices.DrawGridVertex;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.meshbuilder.DrawGridMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.Model3DBuilder;
import com.game.walkingpixels.util.meshbuilder.SpriteMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.BlockMeshBuilder;
import com.game.walkingpixels.util.vector.Vector2;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;


public class DrawingRenderer extends Renderer {

    private DrawGrid drawGrid;
    private final Enemy enemy;
    private World world;

    private final DrawTimer drawTimer;
    private Spell spell;
    private RenderedSpell renderedSpell;
    private Player player;

    private Background background;

    private final Sun sun = new Sun();

    private final DrawGridMeshBuilder drawGridMeshBuilder = new DrawGridMeshBuilder();
    private final BlockMeshBuilder blockMeshBuilder = new BlockMeshBuilder();
    private final SpriteMeshBuilder spriteMeshBuilder = new SpriteMeshBuilder();
    private Model3DBuilder model3DBuilder;

    private boolean models;
    private boolean shadow;
    private final Vector3 enemyPosition = new Vector3(-1, 1, 2);

    public DrawingRenderer(Context context, Enemy enemy, DrawTimer drawTimer) {
        super(context);
        this.enemy = enemy;
        this.drawTimer = drawTimer;
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3(0.5f, -2.0f, 6.0f), new Vector3(0.0f, 0.0f, -1.0f));
        camera.rotationY = 135;
        camera.rotationX = 55;

        player = new Player(context);

        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        models = sharedPref.getBoolean("3d_models", true);
        shadow = sharedPref.getBoolean("shadow_enabled", false);

        model3DBuilder = new Model3DBuilder(context);
        Model3DManager model3DManager = Model3DManager.getInstance(context);

        //init shader
        registerShader("draw", new Shader(context, "Shaders/DrawGrid.shaders"));
        if(shadow)
            registerShader("world", new Shader(context, "Shaders/BasicShadow.shaders"));
        else
            registerShader("world", new Shader(context, "Shaders/Basic.shaders"));
        registerShader("background", new Shader(context, "Shaders/Background.shaders"));
        registerShader("spell", new Shader(context, "Shaders/Spell.shaders"));

        //init light
        registerLightManager("world", new LightManager());
        lightManager("world").initShader(new Shader(context, "Shaders/ShadowGeometry.shaders"));
        lightManager("world").initWorldShader(shader("world"));
        lightManager("world").createPointLight(sun.getPosition(), new Vector4(1f, 1f, 1f, 1.0f), 1000f, camera);

        //init background
        background = new Background(new Texture(context, "textures/clouds.png", 0), 20);
        shader("background").bind();
        shader("background").setUniform1iv("u_Textures", 1, new int[] {0}, 0);
        registerBatch("background", new Batch(shader("background").getID(), 1, PlaneVertex.SIZE, PlaneVertex.getLayout()));
        batch("background").addVertices("Background", background.getVertices());
        batch("background").addTexture(background.getTexture());

        //init spell
        registerBatch("spell", new Batch(shader("spell").getID(), 1, PlaneVertex.SIZE, PlaneVertex.getLayout()));
        shader("spell").bind();
        shader("spell").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);

        //init draw grid
        drawGrid = new DrawGrid(64, 0.8f, new Vector2(0.1f, 0.1f));
        shader("draw").bind();
        registerBatch("draw", new Batch(shader("draw").getID(), drawGrid.getSize() * drawGrid.getSize(), DrawGridVertex.SIZE, DrawGridVertex.getLayout()));
        batch("draw").addVertices("Grid", drawGridMeshBuilder.generateMesh(drawGrid));

        //init world
        world = new World(0);
        generateWorld(world);
        registerBatch("world", new Batch(shader("world").getID(), 300, WorldVertex.SIZE, WorldVertex.getLayout()));
        batch("world").addVertices("ground", blockMeshBuilder.generateMesh(world));
        batch("world").addTexture(new Texture(context, "textures/block_atlas.png", 0));

        if(models){
            registerBatch("models", new Batch(shader("world").getID(), (long)2000, WorldVertex.SIZE, WorldVertex.getLayout()));
            batch("models").addVertices("Player", model3DBuilder.generatePlayer(world, 0));
            batch("models").addVertices("Mobs", model3DBuilder.generateMobVertices(enemyPosition, enemy.getType(), 0));
            batch("models").addTexture(model3DManager.getTexture(context, "player"));
            batch("models").addTexture(model3DManager.getTexture(context, "mobs"));
        }
        else {
            registerBatch("models", new Batch(shader("world").getID(), 40, WorldVertex.SIZE, WorldVertex.getLayout()));
            batch("models").addVertices("Sprites", spriteMeshBuilder.generateMesh(world, camera,true, !shadow));
            batch("world").addTexture(new Texture(context, "textures/mob_texture_atlas.png", 1));
        }

        shader("world").bind();
        shader("world").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);
    }

    @Override
    public void update(double dt) {
        //update DrawGrid
        drawGrid.update(width, height);
        batch("draw").updateVertices("Grid", drawGridMeshBuilder.generateMesh(drawGrid));

        //update sun
        lightManager("world").setLightPosition(0, sun.getPosition());

        //update background
        background.update(dt / 1000, width, height);
        batch("background").updateVertices("Background", background.getVertices());

        //update enemy
        if(enemy.isEnemyTurn())
            enemy.update(dt / 1000);

        //lower time while drawing
        if(drawGrid.isDrawing())
            drawTimer.updateDrawTime(dt / 1000);

        //finished drawing spell
        if(drawTimer.getDrawTime() == 0.0 && drawGrid.isEnabled()){
            drawGrid.disable();
            renderedSpell = new RenderedSpell(drawGrid.getDrawnAsBitmap(), 1.0, (int) ((Math.max(drawGrid.calculateScore(), 0.0) / 100.0) * spell.getMaxDamage()));
            batch("spell").addTexture(renderedSpell.getTexture());
            drawGrid.clear();
        }

        //update cast spell
        if(renderedSpell != null && renderedSpell.isEnabled()){
            if(renderedSpell.isFinished()){
                batch("spell").removePart("spell");
                enemy.damage((int) (renderedSpell.getDamage() * player.getStrength()));
                enemy.setEnemyTurn(true);
            }
            else {
                renderedSpell.update(dt / 1000);
                batch("spell").updateVertices("spell", renderedSpell.getVertices(new Vector3(1.0f - 2.0f, 1.0f, 4.0f - 2.0f) , camera));
            }
        }

        //update animations
        if (models){
            batch("models").updateVertices("Player", model3DBuilder.generatePlayer(world, 90));
            batch("models").updateVertices("Mobs", model3DBuilder.generateMobVertices(enemyPosition, enemy.getType(), 0));
        }
        else {
            batch("models").updateVertices("Sprites", spriteMeshBuilder.generateMesh(world, camera, true, !shadow));
        }
    }

    @Override
    public void render(double dt) {
        //calculate sun shadow
        lightManager("world").calculateShadow(new Batch[]{batch("world"), batch("models")}, width, height);

        //set background color according to the time
        Vector4 clearColor = sun.getColor();
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        glClear(GL_COLOR_BUFFER_BIT);

        //render background
        glDisable(GL_DEPTH_TEST);
        shader("background").bind();
        batch("background").bind();
        batch("background").draw();
        glEnable(GL_DEPTH_TEST);
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
        //render models
        batch("models").bind();
        batch("models").draw();

        //render spell
        if(renderedSpell != null && renderedSpell.isEnabled()) {
            glDisable(GL_DEPTH_TEST);
            shader("spell").bind();
            shader("spell").setUniformMatrix4fv("mvpmatrix", camera.getMVPMatrix());
            shader("spell").setUniform1f("alpha", renderedSpell.getAlpha());
            batch("spell").bind();
            batch("spell").draw();
            glEnable(GL_DEPTH_TEST);
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
