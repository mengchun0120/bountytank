package com.crazygame.bountytank.gameobj;

import android.util.Log;

import com.crazygame.bountytank.controllers.DriveWheel;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class Tank extends GameObject {
    private final static TankTemplate template = new TankTemplate();
    private int direction;
    private boolean firing = false;
    private int side;
    private float speed = 200f;

    public Tank(int direction, int side, float x, float y) {
        this.direction = direction;
        this.side = side;
        position[0] = x;
        position[1] = y;
    }

    public int getDirection() {
        return direction;
    }

    public void changeDirection(Map map, int newDirection) {
        direction = newDirection;
        checkBounds(map);
    }

    public void move(Map map, float timeDelta) {
        float delta = speed * timeDelta;
        int oldRow = map.getRow(position[1]);
        int oldCol = map.getCol(position[0]);

        switch (direction) {
            case DriveWheel.UP:
                position[1] += delta;
                break;
            case DriveWheel.LEFT:
                position[0] -= delta;
                break;
            case DriveWheel.DOWN:
                position[1] -= delta;
                break;
            case DriveWheel.RIGHT:
                position[0] += delta;
                break;
        }

        checkBounds(map);

        int newRow = map.getRow(position[1]);
        int newCol = map.getCol(position[0]);
        if(newRow != oldRow || newCol != oldCol) {
            map.removeObject(this, oldRow, oldCol);
            map.addObject(this, newRow, newCol);
        }
    }

    public void update(Map map, int newDirection, boolean isFiring, float timeDelta) {
        if(newDirection == DriveWheel.NOT_MOVE) {
            return;
        }

        if(newDirection != direction) {
            changeDirection(map, newDirection);
        } else {
            move(map, timeDelta);
        }
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        template.draw(simpleShaderProgram, direction, side, position);
    }

    @Override
    public float getWidth() {
        return template.getWidth(direction);
    }

    @Override
    public float getHeight() {
        return template.getHeight(direction);
    }

    @Override
    public boolean checkCollision(GameObject obj1, float x, float y) {
        return false;
    }

    private boolean checkBounds(Map map) {
        boolean bumpBound = false;

        float left = position[0] - getWidth() / 2f;
        if(left < 0f) {
            position[0] -= left;
            bumpBound = true;
        }

        float rightDelta = position[0] + getWidth() / 2f - map.getWidth();
        if(rightDelta > 0f) {
            position[0] -= rightDelta;
            bumpBound = true;
        }

        float bottom = position[1] - getHeight() / 2f;
        if(bottom < 0f) {
            position[1] -= bottom;
            bumpBound = true;
        }

        float topDelta = position[1] + getHeight() / 2f - map.getHeight();
        if(topDelta > 0f) {
            position[1] -= topDelta;
            bumpBound = true;
        }

        return bumpBound;
    }
}
