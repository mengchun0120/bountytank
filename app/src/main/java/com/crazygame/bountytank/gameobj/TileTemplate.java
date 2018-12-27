package com.crazygame.bountytank.gameobj;

import android.graphics.Color;

import com.crazygame.bountytank.geometry.Rectangle;
import com.crazygame.bountytank.opengl.OpenGLHelper;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

public class TileTemplate {
    public final float width = 25f;
    public final float height = 25f;
    private final Rectangle tile = new Rectangle(width, height);
    private final float[] fillColor =
            OpenGLHelper.getColor(Color.argb(255, 133, 235, 235));
    private final float[] borderColor =
            OpenGLHelper.getColor(Color.argb(255, 0, 10, 96));

    public void draw(SimpleShaderProgram simpleShaderProgram, float[] position) {
        simpleShaderProgram.setUseObjRef(true);
        simpleShaderProgram.setObjRef(position, 0);
        tile.draw(simpleShaderProgram, fillColor, borderColor, 1.0f);
    }
}
