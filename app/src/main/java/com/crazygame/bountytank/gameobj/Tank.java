package com.crazygame.bountytank.gameobj;

import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class Tank extends GameObject {
    private final static TankTemplate template = new TankTemplate();
    public int direction;
    public boolean moving = false;
    public int side;

    public Tank(int direction, int side, float x, float y) {
        this.direction = direction;
        this.side = side;
        position[0] = x;
        position[1] = y;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        template.draw(simpleShaderProgram, direction, side, position);
    }
}
