package com.crazygame.bountytank.event;

public class TouchEvent {
    public final static int DOWN = 0;
    public final static int MOVE = 1;
    public final static int UP = 2;

    public int action;
    public float x, y;
    public int pointerId;

    public TouchEvent() {
    }
}
