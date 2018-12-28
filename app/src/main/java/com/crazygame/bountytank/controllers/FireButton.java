package com.crazygame.bountytank.controllers;

import android.graphics.Color;

import com.crazygame.bountytank.event.TouchEvent;
import com.crazygame.bountytank.geometry.Circle;
import com.crazygame.bountytank.opengl.OpenGLHelper;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class FireButton {
    private final float[] borderColor =
            OpenGLHelper.getColor(Color.argb(255, 120, 0, 0));
    private final float[] normalFillColor =
            OpenGLHelper.getColor(Color.argb(255, 255, 210, 210));
    private final float[] pressedFillColor =
            OpenGLHelper.getColor(Color.argb(255, 255, 0, 0));

    private float radius;
    private final float[] center = new float[2];

    private int curPointerId = -1;
    private boolean pressed = false;

    private Circle button;

    public FireButton(float width, float height) {
        radius = 150f;

        final float gapBorder = 30f;

        center[0] = width/2f - radius - gapBorder;
        center[1] = -height/2f + radius + gapBorder;

        button = new Circle(radius, 60);
    }

    public boolean isPressed() {
        return pressed;
    }

    public void onTouch(int action, int pointerId, float x, float y) {
        if(curPointerId != -1) {
            if(curPointerId != pointerId) {
                return;
            } else if(action == TouchEvent.UP) {
                curPointerId = -1;
                pressed = false;
                return;
            }
        } else if(action == TouchEvent.UP) {
            return;
        }

        float xdist = x - center[0];
        float ydist = y - center[1];

        if(xdist*xdist + ydist*ydist > radius*radius) {
            if(curPointerId != -1) {
                curPointerId = -1;
                pressed = false;
            }
            return;
        }

        curPointerId = pointerId;
        pressed = true;
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setUseObjRef(true);
        simpleShaderProgram.setObjRef(center, 0);

        button.draw(simpleShaderProgram, pressed ? pressedFillColor : normalFillColor,
                borderColor, 1.0f);
    }
}
