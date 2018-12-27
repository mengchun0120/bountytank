package com.crazygame.bountytank.gameobj;

import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class Tile extends GameObject {
    private static final TileTemplate template = new TileTemplate();

    public Tile(float x, float y) {
        position[0] = x;
        position[1] = y;
    }

    @Override
    public float getWidth() {
        return template.width;
    }

    @Override
    public float getHeight() {
        return template.height;
    }

    @Override
    public boolean checkCollision(GameObject obj1, float x, float y) {
        return false;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        template.draw(simpleShaderProgram, position);
    }
}
