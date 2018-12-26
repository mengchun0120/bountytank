package com.crazygame.bountytank.gameobj;

import android.graphics.Color;

import com.crazygame.bountytank.geometry.Paint;
import com.crazygame.bountytank.geometry.Rectangle;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class TileTemplate {
    public final float width = 40f;
    public final float height = 40f;
    private final Rectangle tile = new Rectangle(width, height);
    private final Paint paint = new Paint();

    public TileTemplate() {
        paint.relativeToViewport = true;
        paint.fill = true;
        paint.setFillColor(Color.argb(255, 133, 235, 235));
        paint.drawBorder = true;
        paint.setBorderColor(Color.argb(255, 0, 10, 96));
    }

    public void draw(SimpleShaderProgram simpleShaderProgram, float[] position) {
        tile.draw(simpleShaderProgram, position, 0, paint);
    }
}
