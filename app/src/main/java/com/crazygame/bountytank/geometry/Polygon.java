package com.crazygame.bountytank.geometry;

import android.opengl.GLES20;

import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class Polygon extends Shape {
    public Polygon(float[] sideVertices) {
        super(sideVertices.length / SimpleShaderProgram.POSITION_COMPONENT_COUNT + 2);
        vertices.floatBuffer.position(0);
        vertices.floatBuffer.put(0f);
        vertices.floatBuffer.put(0f);
        vertices.floatBuffer.put(sideVertices);
        vertices.floatBuffer.put(sideVertices, 0, 2);
        vertices.bindData();
    }

    @Override
    public void draw(SimpleShaderProgram program, Paint paint) {
        program.setUseFixedColor(true);
        program.setPosition(vertices, 0, 0);

        if(paint.fill) {
            program.setFixedColor(paint.fillColor, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, numVertices);
        }

        if(paint.drawBorder) {
            program.setFixedColor(paint.borderColor, 0);
            GLES20.glLineWidth(paint.lineWidth);
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 1, numVertices - 2);
        }
    }
}
