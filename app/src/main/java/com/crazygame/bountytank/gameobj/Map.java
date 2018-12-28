package com.crazygame.bountytank.gameobj;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.crazygame.bountytank.GameView;
import com.crazygame.bountytank.controllers.DriveWheel;
import com.crazygame.bountytank.geometry.Line;
import com.crazygame.bountytank.opengl.OpenGLHelper;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Map {
    public final static float MAX_OBJ_BREATH = 100f;
    public final static float BLOCK_BREATH = 100f;

    private final float[] borderColor =
            OpenGLHelper.getColor(Color.argb(255, 0, 0, 0));
    private final Line leftBorder, rightBorder;
    public final GameObject[][] objects;
    public final int xBlocks = 12;
    public final int yBlocks;
    public final float width = (float)xBlocks * BLOCK_BREATH;
    public final float height;
    private final float minViewportOriginY, maxViewportOriginY;
    private final float extraY;
    private Tank player;
    private int yStart, yEnd;
    private final float[] viewportOrigin = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];

    public Map(Context context, int resourceId, float viewportHeight) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);

            yBlocks = Integer.parseInt(tokenizer.nextToken());
            height = yBlocks * BLOCK_BREATH;

            viewportOrigin[0] = width/2f;

            minViewportOriginY = viewportHeight/2f;
            maxViewportOriginY = height - viewportHeight/2f;

            extraY = (viewportHeight + MAX_OBJ_BREATH) / 2f;

            float borderAbsX = width/2f, borderAbsY = viewportHeight/2f;
            leftBorder = new Line(-borderAbsX, borderAbsY, -borderAbsX, -borderAbsY);
            rightBorder = new Line(borderAbsX, borderAbsY, borderAbsX, -borderAbsY);

            objects = new GameObject[yBlocks][xBlocks];
            while((line = reader.readLine()) != null) {
                tokenizer = new StringTokenizer(line);

                String type = tokenizer.nextToken();

                if(type.equals("tile")) {
                    float x = Float.parseFloat(tokenizer.nextToken());
                    float y = Float.parseFloat(tokenizer.nextToken());
                    Tile tile = new Tile(x, y);
                    addObject(tile, getRow(y), getCol(x));
                } else if(type.equals("tank")) {
                    int direction = Integer.parseInt(tokenizer.nextToken());
                    int side = Integer.parseInt(tokenizer.nextToken());
                    float x = Float.parseFloat(tokenizer.nextToken());
                    float y = Float.parseFloat(tokenizer.nextToken());
                    Tank tank = new Tank(direction, side, x, y);
                    addObject(tank, getRow(y), getCol(x));
                    if(side == 1) {
                        player = tank;
                    }
                }
            }

            updateViewportOrigin();
            updateEffectiveRegion();

        } catch(IOException e) {
            throw new RuntimeException("Failed to read resource");
        }
    }

    public int getRow(float y) {
        return (int)Math.floor(y / BLOCK_BREATH);
    }
    
    public int getCol(float x) {
        return (int)Math.floor(x / BLOCK_BREATH);
    }

    public void addObject(GameObject obj, int row, int col) {
        obj.next = objects[row][col];
        objects[row][col] = obj;
    }

    public boolean removeObject(GameObject obj, int row, int col) {
        GameObject prev = null, obj1;

        for(obj1= objects[row][col]; obj1!= null; obj1= obj1.next) {
            if(obj1== obj) {
                break;
            }
            prev = obj1;
        }

        if(obj1 == null) {
            return false;
        }

        if(prev != null) {
            prev.next = obj1.next;
        } else {
            objects[row][col] = obj1.next;
        }

        return true;
    }

    public void moveObject(GameObject obj, float x, float y) {
        int oldRow = getRow(obj.position[1]), oldCol = getCol(obj.position[0]);
        int newRow = getRow(y), newCol = getCol(x);

        if(oldRow != newRow || oldCol != newCol) {
            removeObject(obj, oldRow, oldCol);
            addObject(obj, newRow, newCol);
        }

        obj.position[0] = x;
        obj.position[1] = y;
    }

    private void updateEffectiveRegion() {
        yStart = (int)Math.floor((viewportOrigin[1] - extraY) / BLOCK_BREATH);
        if(yStart < 0) {
            yStart = 0;
        }

        yEnd = (int)Math.floor((viewportOrigin[1] + extraY) / BLOCK_BREATH);
        if(yEnd >= yBlocks) {
            yEnd = yBlocks - 1;
        }
    }

    private void updateViewportOrigin() {
        if(player.position[1] >= minViewportOriginY && player.position[1] <= maxViewportOriginY) {
            viewportOrigin[1] = player.position[1];
        } else if (player.position[1] < minViewportOriginY) {
            viewportOrigin[1] = minViewportOriginY;
        } else {
            viewportOrigin[1] = maxViewportOriginY;
        }
    }

    public void updateAll(float timeDelta) {
        player.update(this, timeDelta);
        updateViewportOrigin();
        updateEffectiveRegion();

        for(int row = yStart; row < yEnd; ++row) {
            for(int col = 0; col < xBlocks; ++col) {
                for(GameObject obj = objects[row][col]; obj != null; obj = obj.next) {
                    if(obj instanceof Tank) {
                        Tank tank = (Tank)obj;
                        if(tank != player) {
                            tank.update(this, timeDelta);
                        }
                    }
                }
            }
        }
    }

    public void updatePlayer(int direction, boolean firing) {
        player.setState(this, direction, firing);
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setRelativeToViewport(true);
        simpleShaderProgram.setViewportOrigin(viewportOrigin, 0);

        for(int row = yStart; row <= yEnd; ++row) {
            for(int col = 0; col < xBlocks; ++col) {
                for(GameObject obj = objects[row][col]; obj != null; obj = obj.next) {
                    obj.draw(simpleShaderProgram);
                }
            }
        }

        simpleShaderProgram.setRelativeToViewport(false);
        simpleShaderProgram.setUseObjRef(false);
        leftBorder.draw(simpleShaderProgram, null, borderColor, 1.0f);
        rightBorder.draw(simpleShaderProgram, null, borderColor, 1.0f);
    }
}
