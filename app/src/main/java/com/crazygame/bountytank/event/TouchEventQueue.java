package com.crazygame.bountytank.event;

public class TouchEventQueue {
    private final TouchEvent[] queue;
    private int first, last;

    public TouchEventQueue(int capacity) {
        first = -1;
        last = -1;
        queue = new TouchEvent[capacity];
        for(int i = 0; i < capacity; ++i) {
            queue[i] = new TouchEvent();
        }
    }

    public TouchEvent first() {
        return !empty() ? queue[first] :null;
    }

    public boolean empty() {
        return first == -1;
    }

    public void removeFirst() {
        if(empty()) {
            return;
        }

        if(first != last) {
            first = (first + 1) % queue.length;
        } else {
            first = -1;
        }
    }

    public void enqueue(int action, int pointerId, float x, float y) {
        if(empty()) {
            first = last = 0;
        } else {
            last = (last + 1) % queue.length;
            if(last == first) {
                first = (first + 1) % queue.length;
            }
        }
        queue[last].action = action;
        queue[last].pointerId = pointerId;
        queue[last].x = x;
        queue[last].y = y;
    }
}
