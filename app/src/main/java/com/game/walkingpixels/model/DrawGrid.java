package com.game.walkingpixels.model;

import android.graphics.drawable.Drawable;

public class DrawGrid {

    private Boolean[][] grid;
    private final int size;

    public DrawGrid(int size){
        grid = new Boolean[size][size];
        this.size = size;
        clear();

        grid[0][0] = true;
    }

    public void clear(){
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++) {
                grid[x][y] = true;
            }
        }
    }

    public Boolean[][] getGrid(){
        return grid;
    }

    public int getSize(){
        return size;
    }
}
