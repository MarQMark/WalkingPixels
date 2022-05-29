package com.game.walkingpixels.model;

import com.game.walkingpixels.util.EventHandler;
import com.game.walkingpixels.util.vector.Vector2;

import java.util.ArrayList;
import java.util.List;

public class DrawGrid {

    private final int[][] grid;
    private final int size;
    private int brushSize = 1;

    public final float scale;
    public final Vector2 offset;

    public DrawGrid(int size, float scale, Vector2 offset){
        grid = new int[size][size];
        this.size = size;
        this.scale = scale;
        this.offset = offset;

        clear();
    }

    public void clear(){
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++) {
                grid[x][y] = 0;
            }
        }
    }

    public void update(int width, int height){
        if(EventHandler.lastTouchPosition.x != -1){

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

    public int[][] getGrid(){
        return grid;
    }

    public int getSize(){
        return size;
    }
}
