package com.crazygame.bountytank.gameobj;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.crazygame.bountytank.geometry.Line;
import com.crazygame.bountytank.geometry.Paint;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Map {
    public final static float MAX_OBJ_BREATH = 160f;
    public final static float BLOCK_BREATH = 80f;
    public final static int MAP_BLOCKS_X = 14;

    private final int borderColor = Color.argb(255, 0, 0, 0);
    private final Line leftBorder, rightBorder;
    private final GameObject[][] objects;
    private final int xBlocks, yBlocks;
    private final float width, height;
    private final float extraX, extraY;
    private int xStart, xEnd, yStart, yEnd;
    private final float[] viewportOrigin = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    private final Paint paint = new Paint();
    private final float[] borderLocation = {0f, 0f};

    public Map(Context context, int resourceId, float viewportWidth, float viewportHeight) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);

            xBlocks = MAP_BLOCKS_X;
            yBlocks = Integer.parseInt(tokenizer.nextToken());
            width =  xBlocks * BLOCK_BREATH;
            height = yBlocks * BLOCK_BREATH;

            float borderAbsX = width/2f, borderAbsY = viewportHeight/2f;
            leftBorder = new Line(-borderAbsX, borderAbsY, -borderAbsX, -borderAbsY);
            rightBorder = new Line(borderAbsX, borderAbsY, borderAbsX, -borderAbsY);

            paint.drawBorder = true;
            paint.lineWidth = 1f;
            paint.setBorderColor(borderColor);
            
            objects = new GameObject[yBlocks][xBlocks];

            extraX = (viewportWidth + MAX_OBJ_BREATH) / 2f;
            extraY = (viewportHeight + MAX_OBJ_BREATH) / 2f;

            line = reader.readLine();
            viewportOrigin[0] = Float.parseFloat(line);
            viewportOrigin[1] = viewportHeight / 2f;
            updateEffectiveRegion();

            while((line = reader.readLine()) != null) {
                tokenizer = new StringTokenizer(line);

                String type = tokenizer.nextToken();
                float x = Float.parseFloat(tokenizer.nextToken());
                float y = Float.parseFloat(tokenizer.nextToken());

                if(type.equals("tile")) {
                    Tile tile = new Tile(x, y);
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
        simpleShaderProgram.setRelativeToViewport(true);
        simpleShaderProgram.setViewportOrigin(viewportOrigin, 0);

        for(int row = yStart; row <= yEnd; ++row) {
            for(int col = xStart; col <= xEnd; ++col) {
                for(GameObject obj = objects[row][col]; obj != null; obj = obj.next) {
                    obj.draw(simpleShaderProgram);
                }
            }
        }

        simpleShaderProgram.setRelativeToViewport(false);
        simpleShaderProgram.setUseObjRef(false);
        leftBorder.draw(simpleShaderProgram, paint);
        rightBorder.draw(simpleShaderProgram, paint);
    }
}
