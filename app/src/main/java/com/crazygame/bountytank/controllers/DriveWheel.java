package com.crazygame.bountytank.controllers;

import android.graphics.Color;
import android.graphics.Path;

import com.crazygame.bountytank.event.TouchEvent;
import com.crazygame.bountytank.geometry.Pie;
import com.crazygame.bountytank.geometry.Polygon;
import com.crazygame.bountytank.opengl.OpenGLHelper;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class DriveWheel {
    public final static int UP = 0;
    public final static int LEFT = 1;
    public final static int DOWN = 2;
    public final static int RIGHT = 3;
    public final static int NOT_MOVE = -1;

    private final float[] borderColor =
            OpenGLHelper.getColor(Color.argb(255, 0, 0, 9));
    private final float[] arrowFillColor =
            OpenGLHelper.getColor(Color.argb(255, 255, 200, 30));
    private final float[] normalPieColor =
            OpenGLHelper.getColor(Color.argb(255, 227, 255, 164));
    private final float[] pressedPieColor =
            OpenGLHelper.getColor(Color.argb(255, 0, 255, 0));

    private final float[] center = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    private float radius;
    private final float[] arrowLocations =
            new float[4 * SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    private final Polygon[] arrows = new Polygon[4];
    private final Pie[] pies = new Pie[4];
    private int direction = NOT_MOVE;
    private int curPointerId = -1;

    public DriveWheel(float width, float height) {
        setDriveWheel(width, height);
    }

    private void setDriveWheel(float width, float height) {
        radius = 150f;

        final float gapBorder = 30f;
        final float distArrowToCenter = radius * 0.75f;

        center[0] = -width/2f + radius + gapBorder;
        center[1] = -height/2f + radius + gapBorder;

        // Create wheel
        float angle = 45;
        for(int i = 0; i < 4; ++i) {
            pies[i] = new Pie(radius, angle, angle+90f, 20);
            angle += 90f;
        }

        // location of up arrow
        arrowLocations[0] = center[0];
        arrowLocations[1] = center[1] + distArrowToCenter;
        // location of right arrow
        arrowLocations[2] = center[0] - distArrowToCenter;
        arrowLocations[3] = center[1];
        // location of down arrow
        arrowLocations[4] = center[0];
        arrowLocations[5] = center[1] - distArrowToCenter;
        // location of left arrow
        arrowLocations[6] = center[0] + distArrowToCenter;
        arrowLocations[7] = center[1];

        final float arrowHeight = radius * 0.25f;
        final float arrowWidth = radius * 0.8f;

        // up arrow
        arrows[0] = new Polygon(new float[]{
            0f, arrowHeight/2f,
            -arrowWidth/2f, -arrowHeight/2f,
            arrowWidth/2f, -arrowHeight/2f
        });

        // right arrow
        arrows[1] = new Polygon(new float[]{
            arrowHeight/2f, arrowWidth/2f,
            -arrowHeight/2f, 0f,
            arrowHeight/2f, -arrowWidth/2f
        });

        // down arrow
        arrows[2] = new Polygon(new float[]{
            arrowWidth/2f, arrowHeight/2f,
            -arrowWidth/2f, arrowHeight/2f,
            0f, -arrowHeight/2f
        });

        // left arrow
        arrows[3] = new Polygon(new float[]{
            arrowHeight/2f, 0f,
            -arrowHeight/2f, arrowWidth/2f,
            -arrowHeight/2f, -arrowWidth/2f
        });
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setUseObjRef(true);

        simpleShaderProgram.setObjRef(center, 0);
        for(int i = 0; i < 4; ++i) {
            pies[i].draw(simpleShaderProgram, direction == i ? pressedPieColor : normalPieColor,
                    borderColor, 1.0f);
        }

        for(int i = 0; i < 4; ++i) {
            simpleShaderProgram.setObjRef(arrowLocations, i*2);
            arrows[i].draw(simpleShaderProgram, arrowFillColor, borderColor, 1.0f);
        }
    }

    public int getDirection() {
        return direction;
    }

    public void onTouch(TouchEvent event) {
        if(curPointerId != -1) {
            if(curPointerId != event.pointerId) {
                return;
            } else if(event.action == TouchEvent.UP) {
                curPointerId = -1;
                direction = NOT_MOVE;
                return;
            }
        } else if(event.action == TouchEvent.UP) {
            return;
        }

        float xdist = event.x - center[0];
        float ydist = event.y - center[1];

        if(xdist * xdist + ydist * ydist > radius * radius) {
            if(curPointerId != -1) {
                curPointerId = -1;
                direction = NOT_MOVE;
            }
            return;
        }

        curPointerId = event.pointerId;

        float xabs = Math.abs(xdist);
        float yabs = Math.abs(ydist);

        if(xabs >= yabs) {
            direction =  xdist >= 0f ? RIGHT : LEFT;
        } else {
            direction = ydist >= 0f ? UP : DOWN;
        }
    }
}
