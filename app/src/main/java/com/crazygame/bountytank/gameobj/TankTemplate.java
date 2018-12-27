package com.crazygame.bountytank.gameobj;

import android.graphics.Color;

import com.crazygame.bountytank.geometry.Circle;
import com.crazygame.bountytank.geometry.Rectangle;
import com.crazygame.bountytank.opengl.OpenGLHelper;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class TankTemplate {
    private final Circle turret = new Circle(25f, 20);
    private final Rectangle[] base = {
        new Rectangle(80, 100),
        new Rectangle(100, 80)
    };
    private final Rectangle[] barrel = {
        new Rectangle(10f, 40f),
        new Rectangle(40f, 10f)
    };
    private final float[][] turrentPosition = {
        {0f, -13f},
        {13f, 0f},
        {0f, 13f},
        {13f, 0f}
    };
    private final float[][] barrelPosition = {
        {0f, 25f},
        {-25f, 0f},
        {0f, -25f},
        {-25f, 0f}
    };
    private final float[] posBuffer = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    private final float[] borderColor =
            OpenGLHelper.getColor(Color.argb(255, 0, 0, 0));
    private final float[][] turretColor = {
        OpenGLHelper.getColor(Color.argb(255, 255, 177, 177)),
        OpenGLHelper.getColor(Color.argb(255, 183, 241, 143))
    };
    private final float[][] barrelColor = {
        OpenGLHelper.getColor(Color.argb(255, 73, 54, 18)),
        OpenGLHelper.getColor(Color.argb(255, 11, 43, 172))
    };
    private final float[][] baseColor = {
        OpenGLHelper.getColor(Color.argb(255, 177, 120, 82)),
        OpenGLHelper.getColor(Color.argb(255, 91, 168, 151))
    };

    public TankTemplate() {
    }

    public void draw(SimpleShaderProgram simpleShaderProgram, int direction, int side,
                     float[] position) {
        simpleShaderProgram.setUseObjRef(true);

        simpleShaderProgram.setObjRef(position, 0);
        base[direction % 2].draw(simpleShaderProgram, baseColor[side], borderColor,
                1.0f);

        posBuffer[0] = position[0] + barrelPosition[direction][0];
        posBuffer[1] = position[1] + barrelPosition[direction][1];
        simpleShaderProgram.setObjRef(posBuffer, 0);
        barrel[direction % 2].draw(simpleShaderProgram, barrelColor[side], borderColor,
                1.0f);

        posBuffer[0] = position[0] + turrentPosition[direction][0];
        posBuffer[1] = position[1] + turrentPosition[direction][1];
        simpleShaderProgram.setObjRef(posBuffer, 0);
        turret.draw(simpleShaderProgram, turretColor[side], borderColor, 1.0f);
    }

    public float getWidth(int direction) {
        return base[direction % 2].width;
    }

    public float getHeight(int direction) {
        return base[direction % 2].height;
    }
}
