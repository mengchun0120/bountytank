package com.crazygame.bountytank.gameobj;

import android.graphics.Color;

import com.crazygame.bountytank.geometry.Paint;
import com.crazygame.bountytank.geometry.Rectangle;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class Tile extends GameObject {
    private static final TileTemplate template = new TileTemplate();

    public Tile(float x, float y) {
        position[0] = x;
        position[1] = y;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setUseObjRef(true);
        simpleShaderProgram.setObjRef(position, 0);
        template.draw(simpleShaderProgram);
    }
}
