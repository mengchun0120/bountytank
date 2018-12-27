package com.crazygame.bountytank.geometry;

import android.opengl.GLES20;

import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class Line extends Shape {
    public Line(float startX, float startY, float endX, float endY) {
        super(2);
        vertices.floatBuffer.position(0);
        vertices.floatBuffer.put(startX);
        vertices.floatBuffer.put(startY);
        vertices.floatBuffer.put(endX);
        vertices.floatBuffer.put(endY);
        vertices.bindData();
    }

    public void draw(SimpleShaderProgram program, Paint paint) {
        if(!paint.drawBorder) {
            return;
        }
        program.setPosition(vertices, 0, 0);
        program.setUseFixedColor(true);
        program.setFixedColor(paint.borderColor, 0);
        GLES20.glLineWidth(paint.lineWidth);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
    }
}
