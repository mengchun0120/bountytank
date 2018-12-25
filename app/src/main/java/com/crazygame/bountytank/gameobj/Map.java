package com.crazygame.bountytank.gameobj;

import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class Map {
    public final static float MAX_OBJ_BREATH = 200f;
    public final static float BLOCK_BREATH = 100f;

    public final GameObject[][] objects;
    public final int xBlocks, yBlocks;
    public final float width, height;
    private final float viewportWidth, viewportHeight;
    private final float extraX, extraY;

    public Map(int xBlocks, int yBlocks, float viewportWidth, float viewportHeight) {
        this.xBlocks = xBlocks;
        this.yBlocks = yBlocks;

        width = xBlocks * BLOCK_BREATH;
        height = yBlocks * BLOCK_BREATH;

        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;

        extraX = (viewportWidth + BLOCK_BREATH) / 2f;
        extraY = (viewportHeight + BLOCK_BREATH) / 2f;

        objects = new GameObject[yBlocks][xBlocks];
    }

    public boolean addObject(GameObject obj) {
        float[] pos = obj.getPosition();
        int col = (int)Math.floor(pos[0] / BLOCK_BREATH);
        int row = (int)Math.floor(pos[1] / BLOCK_BREATH);

        if(col < 0 || col >= xBlocks || row < 0 || row >= yBlocks) {
            return false;
        }

        obj.prev = null;
        obj.next = objects[row][col];
        objects[row][col] = obj;

        return true;
    }

    public void draw(SimpleShaderProgram simpleShaderProgram, float[] viewportOrigin,
                     float viewportWidth, float viewportHeight) {

        int leftCol = (int)Math.ceil((viewportOrigin[0] - extraX) / BLOCK_BREATH);
        if(leftCol < 0) {
            leftCol = 0;
        }

        int rightCol = (int)Math.ceil((viewportOrigin[0] + extraX) / BLOCK_BREATH);
        if(rightCol >= xBlocks) {
            rightCol = xBlocks - 1;
        }

        int topRow = (int)Math.ceil((viewportOrigin[1] + extraY) / BLOCK_BREATH);
        if(topRow >= yBlocks) {
            topRow = yBlocks - 1;
        }

        int bottomRow = (int)Math.ceil((viewportOrigin[1] - extraY) / BLOCK_BREATH)
    }
}
