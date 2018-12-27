package com.crazygame.bountytank.gameobj;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

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
    private final GameObject[][] objects;
    private final int xBlocks = 12;
    private final int yBlocks;
    private final float width = (float)xBlocks * BLOCK_BREATH;
    private final float height;
    private final float extraY;
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
            viewportOrigin[1] = viewportHeight / 2f;

            extraY = (viewportHeight + MAX_OBJ_BREATH) / 2f;
            updateEffectiveRegion();

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
                    addObject(tile);
                } else if(type.equals("tank")) {
                    int direction = Integer.parseInt(tokenizer.nextToken());
                    int side = Integer.parseInt(tokenizer.nextToken());
                    float x = Float.parseFloat(tokenizer.nextToken());
                    float y = Float.parseFloat(tokenizer.nextToken());
                    Tank tank = new Tank(direction, side, x, y);
                    addObject(tank);
                }
            }

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

    public boolean addObject(GameObject obj) {
        int col = getCol(obj.position[0]);
        if(col < 0 || col >= xBlocks) {
            return false;
        }

        int row = getRow(obj.position[1]);
        if(row < 0 || row >= yBlocks) {
            return false;
        }

        obj.next = objects[row][col];
        objects[row][col] = obj;

        return true;
    }

    public boolean removeObject(GameObject obj) {
        int col = getCol(obj.position[0]);
        if(col < 0 || col >= xBlocks) {
            return false;
        }

        int row = getRow(obj.position[1]);
        if(row < 0 || row >= yBlocks) {
            return false;
        }

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

    public void updateObjects() {

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
