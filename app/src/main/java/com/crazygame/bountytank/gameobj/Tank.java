package com.crazygame.bountytank.gameobj;

import android.util.Log;

import com.crazygame.bountytank.controllers.DriveWheel;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class Tank extends GameObject {
    private final static TankTemplate template = new TankTemplate();
    private int direction;
    private boolean moving = false;
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

    public void update(Map map, float timeDelta) {
        if(moving) {
            switch (direction) {
                case DriveWheel.UP:
                    moveUp(map, timeDelta);
                    break;
                case DriveWheel.LEFT:
                    moveLeft(map, timeDelta);
                    break;
                case DriveWheel.DOWN:
                    moveDown(map, timeDelta);
                    break;
                case DriveWheel.RIGHT:
                    moveRight(map, timeDelta);
                    break;
            }
        }
    }

    public void setState(Map map, int newDirection, boolean isFiring) {
        if(newDirection == DriveWheel.NOT_MOVE) {
            moving = false;
        } else {
            moving = true;
            if(newDirection != direction) {
                changeDirection(map, newDirection);
            }
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
    public float getLeft() {
        return position[0] - getWidth()/2f;
    }

    @Override
    public float getRight() {
        return position[0] + getWidth()/2f;
    }

    @Override
    public float getTop() {
        return position[1] + getHeight()/2f;
    }

    @Override
    public float getBottom() {
        return position[1] - getHeight()/2f;
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

        float rightDelta = position[0] + getWidth() / 2f - map.width;
        if(rightDelta > 0f) {
            position[0] -= rightDelta;
            bumpBound = true;
        }

        float bottom = position[1] - getHeight() / 2f;
        if(bottom < 0f) {
            position[1] -= bottom;
            bumpBound = true;
        }

        float topDelta = position[1] + getHeight() / 2f - map.height;
        if(topDelta > 0f) {
            position[1] -= topDelta;
            bumpBound = true;
        }

        return bumpBound;
    }

    private void moveUp(Map map, float timeDelta) {
        float height = getHeight();
        float newY = Math.min(position[1] + speed * timeDelta, map.height - height/2f);

        int startRow = map.getRow(getTop());
        if(startRow >= map.yBlocks) {
            startRow = map.yBlocks - 1;
        }

        int endRow = map.getRow(newY + (height + Map.MAX_OBJ_BREATH)/2f);
        if(endRow >= map.yBlocks) {
            endRow = map.yBlocks - 1;
        }

        float left = getLeft();
        float right = getRight();

        int startCol = map.getCol(left - Map.MAX_OBJ_BREATH/2f);
        if(startCol < 0) {
            startCol = 0;
        }

        int endCol = map.getCol(right + Map.MAX_OBJ_BREATH/2f);
        if(endCol >= map.xBlocks) {
            endCol = map.xBlocks - 1;
        }

        float minCollisionY = 1e9f;
        boolean collisionHappened = false;
        float newTop = newY + height/2f;

        for(int row = startRow; row <= endRow; ++row) {
            for(int col = startCol; col <= endCol; ++col) {
                for(GameObject obj = map.objects[row][col]; obj != null; obj = obj.next) {
                    if (obj == null || obj == this) {
                        continue;
                    }

                    if ((obj instanceof Tank) || (obj instanceof Tile)) {
                        float bottom = obj.getBottom();
                        if (obj.getLeft() < right && obj.getRight() > left && bottom < newTop) {
                            collisionHappened = true;
                            if (bottom < minCollisionY) {
                                minCollisionY = bottom;
                            }
                        }
                    }
                }
            }
        }

        if(collisionHappened) {
            newY = minCollisionY - height / 2f;
        }

        map.moveObject(this, position[0], newY);
    }

    private void moveDown(Map map, float timeDelta) {
        float height = getHeight();
        float newY = Math.max(position[1] - speed*timeDelta, height/2f);

        int startRow = map.getRow(getBottom());
        if(startRow < 0) {
            startRow = 0;
        }

        int endRow = map.getRow(newY - (height + Map.MAX_OBJ_BREATH)/2f);
        if(endRow < 0) {
            endRow = 0;
        }

        float left = getLeft();
        float right = getRight();

        int startCol = map.getCol(left - Map.MAX_OBJ_BREATH/2f);
        if(startCol < 0) {
            startCol = 0;
        }

        int endCol = map.getCol(right + Map.MAX_OBJ_BREATH/2f);
        if(endCol >= map.xBlocks) {
            endCol = map.xBlocks - 1;
        }

        float maxCollisionY = -1e9f;
        boolean collisionHappened = false;
        float newBottom = newY - height/2f;

        for(int row = startRow; row >= endRow; --row) {
            for(int col = startCol; col <= endCol; ++col) {
                for(GameObject obj = map.objects[row][col]; obj != null; obj = obj.next) {
                    if (obj == null || obj == this) {
                        continue;
                    }

                    if ((obj instanceof Tank) || (obj instanceof Tile)) {
                        float top = obj.getTop();
                        if (obj.getLeft() < right && obj.getRight() > left && top > newBottom) {
                            collisionHappened = true;
                            if (top > maxCollisionY) {
                                maxCollisionY = top;
                            }
                        }
                    }
                }
            }
        }

        if(collisionHappened) {
            newY = maxCollisionY + height/2f;
        }

        map.moveObject(this, position[0], newY);
    }

    public void moveLeft(Map map, float timeDelta) {
        float width = getWidth();
        float newX = Math.max(position[0] - speed*timeDelta, width/2f);

        int startCol = map.getCol(getLeft());
        if(startCol < 0) {
            startCol = 0;
        }

        int endCol = map.getCol(newX - (width + Map.MAX_OBJ_BREATH)/2f);
        if(endCol < 0) {
            endCol = 0;
        }

        float top = getTop();
        float bottom = getBottom();

        int startRow = map.getRow(bottom - Map.MAX_OBJ_BREATH/2f);
        if(startRow < 0) {
            startRow = 0;
        }

        int endRow = map.getRow(top + Map.MAX_OBJ_BREATH/2f);
        if(endRow >= map.yBlocks) {
            endRow = map.yBlocks - 1;
        }

        float maxCollisionX = -1e9f;
        boolean collisionHappened = false;
        float newLeft = newX - width/2f;

        for(int col = startCol; col >= endCol; --col) {
            for(int row = startRow; row <= endRow; ++row) {
                for(GameObject obj = map.objects[row][col]; obj != null; obj = obj.next) {
                    if (obj == this) {
                        continue;
                    }

                    if ((obj instanceof Tank) || (obj instanceof Tile)) {
                        float right = obj.getRight();

                        if (obj.getBottom() < top && obj.getTop() > bottom && right > newLeft) {
                            collisionHappened = true;
                            if (right > maxCollisionX) {
                                maxCollisionX = right;
                            }
                        }
                    }
                }
            }
        }

        if(collisionHappened) {
            newX = maxCollisionX + width/2f;
        }

        map.moveObject(this, newX, position[1]);
    }

    public void moveRight(Map map, float timeDelta) {
        float width = getWidth();
        float newX = Math.min(position[0] + speed*timeDelta, map.width - width/2f);

        int startCol = map.getCol(getRight());
        if(startCol >= map.xBlocks) {
            startCol = map.xBlocks - 1;
        }

        int endCol = map.getCol(newX + (width + Map.MAX_OBJ_BREATH)/2f);
        if(endCol >= map.xBlocks) {
            endCol = map.xBlocks - 1;
        }

        float top = getTop();
        float bottom = getBottom();

        int startRow = map.getRow(bottom - Map.MAX_OBJ_BREATH/2f);
        if(startRow < 0) {
            startRow = 0;
        }

        int endRow = map.getRow(top + Map.MAX_OBJ_BREATH/2f);
        if(endRow >= map.yBlocks) {
            endRow = map.yBlocks - 1;
        }

        float minCollisionX = 1e9f;
        boolean collisionHappened = false;
        float newRight = newX + width/2f;

        for(int col = startCol; col <= endCol; ++col) {
            for(int row = startRow; row <= endRow; ++row) {
                for(GameObject obj = map.objects[row][col]; obj != null; obj = obj.next) {
                    if (obj == null || obj == this) {
                        continue;
                    }

                    if ((obj instanceof Tank) || (obj instanceof Tile)) {
                        float left = obj.getLeft();

                        if (obj.getBottom() < top && obj.getTop() > bottom && left < newRight) {
                            collisionHappened = true;
                            if (left < minCollisionX) {
                                minCollisionX = left;
                            }
                        }
                    }
                }
            }
        }

        if(collisionHappened) {
            newX = minCollisionX - width/2f;
        }

        map.moveObject(this, newX, position[1]);
    }
}
