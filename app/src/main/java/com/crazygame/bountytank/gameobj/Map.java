package com.crazygame.bountytank.gameobj;

import android.content.Context;
import android.util.Log;

import com.crazygame.bountytank.opengl.SimpleShaderProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Map {
    public final static float MAX_OBJ_BREATH = 200f;
    public final static float BLOCK_BREATH = 100f;

    public final GameObject[][] objects;
    public final int xBlocks, yBlocks;
    public final float width, height;
    private final float extraX, extraY;
    private int xStart, xEnd, yStart, yEnd;
    private final float[] viewportOrigin = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];

    public Map(int xBlocks, int yBlocks, float viewportWidth, float viewportHeight) {
        this.xBlocks = xBlocks;
        this.yBlocks = yBlocks;
        width = xBlocks * BLOCK_BREATH;
        height = yBlocks * BLOCK_BREATH;

        extraX = (viewportWidth + MAX_OBJ_BREATH) / 2f;
        extraY = (viewportHeight + MAX_OBJ_BREATH) / 2f;

        objects = new GameObject[yBlocks][xBlocks];
    }

    public Map(Context context, int resourceId, float viewportWidth, float viewportHeight) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);

            xBlocks = Integer.parseInt(tokenizer.nextToken());
            yBlocks = Integer.parseInt(tokenizer.nextToken());
            width = xBlocks * BLOCK_BREATH;
            height = yBlocks * BLOCK_BREATH;

            objects = new GameObject[yBlocks][xBlocks];

            extraX = (viewportWidth + MAX_OBJ_BREATH) / 2f;
            extraY = (viewportHeight + MAX_OBJ_BREATH) / 2f;

            line = reader.readLine();
            viewportOrigin[0] = Float.parseFloat(line);
            viewportOrigin[1] = viewportHeight / 2f;
            Log.d("maptag", "viewportOrigin:" + viewportOrigin[0] + " " + viewportOrigin[1]);
            updateEffectiveRegion();

            while((line = reader.readLine()) != null) {
                tokenizer = new StringTokenizer(line);

                String type = tokenizer.nextToken();
                float x = Float.parseFloat(tokenizer.nextToken());
                float y = Float.parseFloat(tokenizer.nextToken());

                if(type.equals("tile")) {
                    Tile tile = new Tile(x, y);
                    Log.d("maptag", "add tile:" + x + " " + y + " " + getRow(y) + " " + getCol(x));
                    addObject(tile, getRow(y), getCol(x));
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

    public void addObject(GameObject obj, int row, int col) {
        Log.d("maptag", "objects " + (objects == null));
        Log.d("maptag", "objects: " + row + " " + col);
        obj.next = objects[row][col];
        objects[row][col] = obj;
    }

    public void removeObject(GameObject obj, int row, int col) {
        GameObject prev = null, obj1;

        for(obj1= objects[row][col]; obj1!= null; obj1= obj1.next) {
            if(obj1== obj) {
                break;
            }
            prev = obj1;
        }

        if(obj1!= null) {
            if(prev != null) {
                prev.next = obj1.next;
            } else {
                objects[row][col] = obj1.next;
            }
        }
    }

    private void updateEffectiveRegion() {
        xStart = (int)Math.floor((viewportOrigin[0] - extraX) / BLOCK_BREATH);
        if(xStart < 0) {
            xStart = 0;
        }

        xEnd = (int)Math.floor((viewportOrigin[0] + extraX) / BLOCK_BREATH);
        if(xEnd >= xBlocks) {
            xEnd = xBlocks - 1;
        }

        yStart = (int)Math.floor((viewportOrigin[1] - extraY) / BLOCK_BREATH);
        if(yStart < 0) {
            yStart = 0;
        }

        yEnd = (int)Math.floor((viewportOrigin[1] + extraY) / BLOCK_BREATH);
        if(yEnd >= yBlocks) {
            yEnd = yBlocks - 1;
        }

        Log.d("maptag", "update region:" + yStart + " " + yEnd + " " + xStart + " " + xEnd);
    }

    public void updateObjects() {

    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setViewportOrigin(viewportOrigin, 0);
        for(int row = yStart; row <= yEnd; ++row) {
            for(int col = xStart; col <= xEnd; ++col) {
                for(GameObject obj = objects[row][col]; obj != null; obj = obj.next) {
                    obj.draw(simpleShaderProgram);
                }
            }
        }
    }
}
