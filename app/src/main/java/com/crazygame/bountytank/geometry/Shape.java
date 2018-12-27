package com.crazygame.bountytank.geometry;

import com.crazygame.bountytank.data.VertexBuffer;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public abstract class Shape {
    public final VertexBuffer vertices;
    public final int numVertices;

    public Shape(int numVertices) {
        this.numVertices = numVertices;
        vertices = new VertexBuffer(
                numVertices * SimpleShaderProgram.POSITION_COMPONENT_COUNT);
    }

    public abstract void draw(SimpleShaderProgram program, float[] fillColor,
                              float[] borderColor, float lineWidth);
}
