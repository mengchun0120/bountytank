package com.crazygame.bountytank.gameobj;

import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public abstract class GameObject {
    protected final float[] position = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    protected GameObject next = null, prev = null;

    public GameObject() {
    }

    public float[] getPosition() {
        return position;
    }



    public abstract void draw(SimpleShaderProgram simpleShaderProgram);
}
