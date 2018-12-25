package com.crazygame.bountytank.geometry;

public class Rectangle extends Polygon {
    public final float width, height;

    public Rectangle(float width, float height) {
        super(new float[]{
            width / 2f, height / 2f,
            -width / 2f, height / 2f,
            -width / 2f, -height / 2f,
            width / 2f, -height / 2f
        });

        this.width = width;
        this.height = height;
    }
}
