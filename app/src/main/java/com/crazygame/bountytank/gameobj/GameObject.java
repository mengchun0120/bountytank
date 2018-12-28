package com.crazygame.bountytank.gameobj;

import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public abstract class GameObject {
    public final float[] position = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    protected GameObject next = null;

    public GameObject() {
    }

    public abstract float getLeft();

    public abstract float getRight();

    public abstract float getTop();

    public abstract float getBottom();

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract boolean checkCollision(GameObject obj1, float x, float y);

    public abstract void draw(SimpleShaderProgram simpleShaderProgram);
}
