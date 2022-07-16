package com.game.walkingpixels.controller.renderer;

import android.content.Context;
import android.content.SharedPreferences;

import com.game.walkingpixels.controller.renderer.Renderer;
import com.game.walkingpixels.model.Camera;
import com.game.walkingpixels.model.Background;
import com.game.walkingpixels.model.MainWorld;
import com.game.walkingpixels.model.Model3D;
import com.game.walkingpixels.model.Model3DManager;
import com.game.walkingpixels.model.Sun;
import com.game.walkingpixels.model.World;
import com.game.walkingpixels.openGL.Batch;
import com.game.walkingpixels.openGL.LightManager;
import com.game.walkingpixels.openGL.Shader;
import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.PlaneVertex;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.meshbuilder.Model3DBuilder;
import com.game.walkingpixels.util.meshbuilder.SpriteMeshBuilder;
import com.game.walkingpixels.util.meshbuilder.BlockMeshBuilder;
import com.game.walkingpixels.util.vector.Vector3;
import com.game.walkingpixels.util.vector.Vector4;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

public class WalkingRenderer extends Renderer {

    private Background background;

    private final Sun sun = new Sun();

    private final SpriteMeshBuilder spriteMeshBuilder = new SpriteMeshBuilder();
    private final BlockMeshBuilder blockMeshBuilder = new BlockMeshBuilder();
    private Model3DBuilder model3DBuilder;

    private boolean shadow;
    private boolean models;

    public WalkingRenderer(Context context) {
        super(context);
    }

    @Override
    public void init() {
        //init camera
        camera = new Camera(new Vector3(0.0f, 0.0f, 12.8f), new Vector3(0.0f, 0.0f, -1.0f));
        camera.rotationX = 50;
        SharedPreferences sharedPreferences = context.getSharedPreferences("World", Context.MODE_PRIVATE);
        camera.rotationY = sharedPreferences.getFloat("rotation", 0);

        model3DBuilder = new Model3DBuilder(context);
        Model3DManager model3DManager = Model3DManager.getInstance(context);

        //init shaders
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        shadow = sharedPref.getBoolean("shadow_enabled", false);
        models = sharedPref.getBoolean("3d_models", true);
        if(shadow)
            registerShader("main", new Shader(context, "Shaders/BasicShadow.shaders"));
        else
            registerShader("main", new Shader(context, "Shaders/Basic.shaders"));
        registerShader("background", new Shader(context, "Shaders/Background.shaders"));

        //init background
        background = new Background(new Texture(context, "textures/clouds.png", 0), 20);
        shader("background").bind();
        shader("background").setUniform1iv("u_Textures", 1, new int[] {0}, 0);
        registerBatch("background", new Batch(shader("background").getID(), 1, PlaneVertex.SIZE, PlaneVertex.getLayout()));
        batch("background").addVertices("Background", background.getVertices());
        batch("background").addTexture(background.getTexture());

        //init light
        registerLightManager("main", new LightManager());
        lightManager("main").initShader(new Shader(context, "Shaders/ShadowGeometry.shaders"));
        lightManager("main").initWorldShader(shader("main"));
        lightManager("main").createPointLight(sun.getPosition(), new Vector4(1f, 1f, 1f, 1.0f), 1000f, camera);

        //init world
        shader("main").bind();
        registerBatch("world", new Batch(shader("main").getID(), 2000, WorldVertex.SIZE, WorldVertex.getLayout()));
        batch("world").addVertices("World", blockMeshBuilder.generateMesh(MainWorld.getWorld()));
        batch("world").addTexture(new Texture(context, "textures/block_atlas.png", 0));

        if(models){
            registerBatch("models", new Batch(shader("main").getID(), (long)8000, WorldVertex.SIZE, WorldVertex.getLayout()));
            batch("models").addVertices("Player", model3DBuilder.generatePlayer(MainWorld.getWorld(), 0));
            batch("models").addVertices("Trees", model3DBuilder.generateTrees(MainWorld.getWorld()));
            batch("models").addVertices("Mobs", model3DBuilder.generateMobs(MainWorld.getWorld()));
            batch("models").addVertices("Obelisks", model3DBuilder.generateObelisks(MainWorld.getWorld()));
            batch("models").addTexture(model3DManager.getTexture(context, "player"));
            batch("models").addTexture(model3DManager.getTexture(context, "tree"));
            batch("models").addTexture(model3DManager.getTexture(context, "mobs"));
        }
        else{
            registerBatch("models", new Batch(shader("main").getID(), 400, WorldVertex.SIZE, WorldVertex.getLayout()));
            batch("models").addVertices("Sprites", spriteMeshBuilder.generateMesh(MainWorld.getWorld(), camera, false, !shadow));
            batch("models").addTexture(new Texture(context, "textures/mob_texture_atlas.png", 1));
            batch("models").addTexture(new Texture(context, "textures/tree.png", 2));
        }

        shader("main").setUniform1iv("u_Textures", 4, new int[] {0, 1, 2, 3}, 0);
    }

    @Override
    public void update(double dt) {
        //update camera rotation
        MainWorld.getWorld().setDirection((int) camera.rotationY);

        //update sun
        lightManager("main").setLightPosition(0, sun.getPosition());

        //update background
        background.update(dt / 1000, width, height);
        batch("background").updateVertices("Background", background.getVertices());

        if(models){
            batch("models").updateVertices("Player", model3DBuilder.generatePlayer(MainWorld.getWorld(), camera.rotationY + 180));
            batch("models").updateVertices("Mobs", model3DBuilder.generateMobs(MainWorld.getWorld()));
            batch("models").updateVertices("Obelisks", model3DBuilder.generateObelisks(MainWorld.getWorld()));
        }
        else {
            //update rotations
            batch("models").updateVertices("Sprites", spriteMeshBuilder.generateMesh(MainWorld.getWorld(), camera, false, !shadow));
        }

        //move world
        if(MainWorld.getWorld().hasMoved()){
            batch("world").updateVertices("World" , blockMeshBuilder.generateMesh(MainWorld.getWorld()));
            if(models)
                batch("models").updateVertices("Trees", model3DBuilder.generateTrees(MainWorld.getWorld()));
        }
    }

    @Override
    public void render(double dt) {
        //calculate sun shadow
        lightManager("main").calculateShadow(new Batch[]{batch("world"), batch("models")}, width, height);

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
        //render world
        shader("main").bind();
        shader("main").setUniformMatrix4fv("mvpmatrix", camera.getMVPMatrix());
        batch("world").bind();
        batch("world").draw();
        //render 3D Models
        batch("models").bind();
        batch("models").draw();
    }
}
