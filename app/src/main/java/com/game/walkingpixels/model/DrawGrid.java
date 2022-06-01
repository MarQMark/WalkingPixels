package com.game.walkingpixels.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.renderscript.Matrix4f;

import com.game.walkingpixels.util.EventHandler;
import com.game.walkingpixels.util.vector.Vector2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawGrid {

    private static final int DISTANCE_EQUILIBRIUM = 2;
    private final int[][] grid;
    private final int size;
    private int brushSize = 2;

    private final boolean[][] shapeGrid;
    private int shapePixels = 0;

    public final float scale;
    public final Vector2 offset;

    private boolean isEnabled = false;

    public DrawGrid(int size, float scale, Vector2 offset){
        grid = new int[size][size];
        shapeGrid = new boolean[size][size];
        this.size = size;
        this.scale = scale;
        this.offset = offset;

        clear();
    }

    public void clear(){
        shapePixels = 0;
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++) {
                grid[x][y] = 0;
                shapeGrid[x][y] = false;
            }
        }
    }

    public void update(int width, int height){
        if(isEnabled && EventHandler.lastTouchPosition.x != -1){

            Vector2 touchPosition = EventHandler.touchPosition;

            if (touchPosition.x >= width * offset.x && touchPosition.x <= width - width * offset.x){
                if(touchPosition.y >= height - width * (offset.y + scale) && touchPosition.y <= height - width * offset.y){

                    int x0 = (int)((touchPosition.x - width * offset.x) / (width * scale) * size);
                    int y0 = (int)((height - touchPosition.y - width * offset.y) / (width * scale) * size);
                    int lastX = (int)((EventHandler.lastTouchPosition.x - width * offset.x) / (width * scale) * size);
                    int lastY = (int)((height - EventHandler.lastTouchPosition.y - width * offset.y) / (width * scale) * size);

                    for (Vector2 p: findLine(x0, y0, lastX, lastY)){
                        for (int py = 0; py < size; py++) {
                            for (int px = 0; px < size; px++) {
                                if (inside_circle(new Vector2(p.x, p.y), new Vector2(px, py), brushSize)) {
                                    grid[px][py] = 1;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void loadShape(Context context, String path){

        Bitmap shape = loadTexture(context, path);

        if(shape.getWidth() == size && shape.getHeight() == size){
            for (int y = 0; y < size; y++){
                for (int x = 0; x < size; x++) {
                    if(Color.alpha(shape.extractAlpha().getPixel(x, size - 1 - y))  != 0.0f){
                        grid[x][y] = 2;
                        shapeGrid[x][y] = true;
                        shapePixels++;
                    }
                }
            }
        }

        enable();
    }

    public float calculateScore(){
        double score = 0;

        final int POINT_PER_CORRECT_PIXEL = 1;
        final int MAX_POINT_LOSS_PER_PIXEL = 2;

        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++) {

                if(grid[x][y] == 1 && shapeGrid[x][y]){
                    score += POINT_PER_CORRECT_PIXEL;
                }
                else if(grid[x][y] == 1){
                    double distance = closestDistance(x, y, false);
                    if (distance <= DISTANCE_EQUILIBRIUM)
                        score += POINT_PER_CORRECT_PIXEL * ((DISTANCE_EQUILIBRIUM - distance) / DISTANCE_EQUILIBRIUM);
                    else
                        score += MAX_POINT_LOSS_PER_PIXEL * ((DISTANCE_EQUILIBRIUM - distance) / DISTANCE_EQUILIBRIUM);
                }
                else if(shapeGrid[x][y]){
                    double distance = closestDistance(x, y, true);
                    if (distance <= DISTANCE_EQUILIBRIUM)
                        score += POINT_PER_CORRECT_PIXEL * ((DISTANCE_EQUILIBRIUM - distance) / DISTANCE_EQUILIBRIUM);
                    else
                        score += MAX_POINT_LOSS_PER_PIXEL * ((DISTANCE_EQUILIBRIUM - distance) / DISTANCE_EQUILIBRIUM);
                }

            }
        }

        float roundedScore = Math.round(score);
        return (roundedScore / (float)shapePixels) * 100.0f;
    }

    private double closestDistance(int px, int py, boolean isGrid){
        double distance = DISTANCE_EQUILIBRIUM * 2.0f;
        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++) {

                int d = (x - px) * (x - px) + (y - py) * (y - py);
                if(isGrid){
                    if(grid[x][y] == 1)
                        distance = Math.min(distance, Math.sqrt(d));
                }else {
                    if(shapeGrid[x][y])
                        distance = Math.min(distance, Math.sqrt(d));
                }

            }
        }
        return distance;
    }

    public int[][] getGrid(){
        return grid;
    }

    public int getSize(){
        return size;
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

    private Boolean inside_circle(Vector2 center, Vector2 tile, float radius) {
        float dx = center.x - tile.x,
                dy = center.y - tile.y;
        float distance = (float)Math.sqrt(dx*dx + dy*dy);
        return distance <= radius;
    }

    private List<Vector2> findLine(int x0, int y0, int x1, int y1){
        List<Vector2> line = new ArrayList<>();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx-dy;
        int e2;

        while (true)
        {
            line.add(new Vector2(x0, y0));

            if (x0 == x1 && y0 == y1)
                break;

            e2 = 2 * err;
            if (e2 > -dy)
            {
                err = err - dy;
                x0 = x0 + sx;
            }

            if (e2 < dx)
            {
                err = err + dx;
                y0 = y0 + sy;
            }
        }
        return line;
    }

    public void enable(){
        isEnabled = true;
    }
    public void disable(){
        isEnabled = false;
    }
}
