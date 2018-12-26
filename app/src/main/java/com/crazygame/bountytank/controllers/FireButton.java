package com.crazygame.bountytank.controllers;

import android.graphics.Color;

import com.crazygame.bountytank.event.TouchEvent;
import com.crazygame.bountytank.geometry.Circle;
import com.crazygame.bountytank.geometry.Paint;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class FireButton {
    private final int borderColor = Color.argb(120, 20, 20, 20);
    private final int pressedFillColor = Color.argb(20, 255, 0, 0);

    private float radius;
    private final float[] center = new float[2];

    private int curPointerId = -1;
    private boolean pressed = false;

    private Circle button;
    private Paint paint = new Paint();

    public FireButton(float width, float height) {
        radius = 150f;

        final float gapBorder = 50f;

        center[0] = width/2f - radius - gapBorder;
        center[1] = -height/2f + radius + gapBorder;

        button = new Circle(radius, 60);

        paint.drawBorder = true;
        paint.relativeToViewport = false;
        paint.setBorderColor(borderColor);
        paint.setFillColor(pressedFillColor);
    }

    public boolean isPressed() {
        return pressed;
    }

    public void onTouch(TouchEvent event) {
        if(curPointerId != -1) {
            if(curPointerId != event.pointerId) {
                return;
            } else if(event.action == TouchEvent.UP) {
                curPointerId = -1;
                pressed = false;
                return;
            }
        } else if(event.action == TouchEvent.UP) {
            return;
        }

        float xdist = event.x - center[0];
        float ydist = event.y - center[1];

        if(xdist*xdist + ydist*ydist > radius*radius) {
            if(curPointerId != -1) {
                curPointerId = -1;
                pressed = false;
            }
            return;
        }

        curPointerId = event.pointerId;
        pressed = true;
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        paint.fill = pressed;
        button.draw(simpleShaderProgram, center, 0, paint);
    }
}
