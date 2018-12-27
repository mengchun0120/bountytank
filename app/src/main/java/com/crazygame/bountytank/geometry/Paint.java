package com.crazygame.bountytank.geometry;

import com.crazygame.bountytank.opengl.OpenGLHelper;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class Paint {
    public boolean fill = true;
    public final float[] fillColor = new float[SimpleShaderProgram.COLOR_COMPONENT_COUNT];
    public boolean drawBorder = false;
    public final float[] borderColor = new float[SimpleShaderProgram.COLOR_COMPONENT_COUNT];
    public float lineWidth = 1f;

    public void setFillColor(int rgba) {
        OpenGLHelper.getColor(fillColor, rgba);
    }

    public void setBorderColor(int rgba) {
        OpenGLHelper.getColor(borderColor, rgba);
    }
}
