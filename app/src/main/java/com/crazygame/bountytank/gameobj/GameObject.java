package com.crazygame.bountytank.gameobj;

import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public abstract class GameObject {
    public final float[] position = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    protected GameObject next = null;

    public GameObject() {
    }

    public abstract void draw(SimpleShaderProgram simpleShaderProgram);
}
