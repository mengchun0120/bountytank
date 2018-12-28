package com.crazygame.bountytank;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.crazygame.bountytank.controllers.DriveWheel;
import com.crazygame.bountytank.controllers.FireButton;
import com.crazygame.bountytank.event.TouchEvent;
import com.crazygame.bountytank.event.TouchEventQueue;
import com.crazygame.bountytank.gameobj.Map;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;
import com.crazygame.bountytank.utils.TimeDeltaCalculator;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameView extends GLSurfaceView implements GLSurfaceView.Renderer,
        View.OnTouchListener {
    public final static int RUNNING = 0;
    public final static int PAUSED = 1;
    public final static int END = 2;

    private final Context context;
    private float width, height;

    private SimpleShaderProgram simpleShaderProgram;
    private final float[] projectionMatrix = new float[16];

    private boolean running;

    private final TouchEventHandlerPool touchEventHandlerPool =
            new TouchEventHandlerPool(100);

    private Map map;
    private DriveWheel driveWheel;
    private FireButton fireButton;

    TimeDeltaCalculator timeDeltaCalculator = new TimeDeltaCalculator(3);

    public GameView(Context context, Point size) {
        super(context);

        this.context = context;

        width = size.x;
        height = size.y;

        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        simpleShaderProgram = new SimpleShaderProgram(context);
        simpleShaderProgram.useProgram();

        Matrix.setIdentityM(projectionMatrix, 0);

        driveWheel = new DriveWheel(width, height);
        fireButton = new FireButton(width, height);
        map = new Map(context, R.raw.map1, height);

        timeDeltaCalculator.start();

        setOnTouchListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        this.width = (float)width;
        this.height = (float)height;

        updateProjectionMatrix();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        map.updateAll(timeDeltaCalculator.curTimeDelta());

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        simpleShaderProgram.setProjectionMatrix(projectionMatrix, 0);

        map.draw(simpleShaderProgram);
        driveWheel.draw(simpleShaderProgram);
        fireButton.draw(simpleShaderProgram);
    }

    private void updateProjectionMatrix() {
        projectionMatrix[0] = 2f / width;
        projectionMatrix[5] = 2f / height;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            {
                int pointerIdx = motionEvent.getActionIndex();
                int pointerId = motionEvent.getPointerId(pointerIdx);
                float x = translateMotionX(motionEvent.getX(pointerIdx));
                float y = translateMotionY(motionEvent.getY(pointerIdx));
                queueEvent(touchEventHandlerPool.alloc(TouchEvent.DOWN, pointerId, x, y));
                return true;
            }

            case MotionEvent.ACTION_MOVE:
            {
                int count = motionEvent.getPointerCount();
                for(int i = 0; i < count; ++i) {
                    int pointerId = motionEvent.getPointerId(i);
                    float x = translateMotionX(motionEvent.getX(i));
                    float y = translateMotionY(motionEvent.getY(i));
                    queueEvent(touchEventHandlerPool.alloc(TouchEvent.MOVE, pointerId, x, y));
                }
                return true;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            {
                int pointerIdx = motionEvent.getActionIndex();
                int pointerId = motionEvent.getPointerId(pointerIdx);
                float x = translateMotionX(motionEvent.getX(pointerIdx));
                float y = translateMotionY(motionEvent.getY(pointerIdx));
                queueEvent(touchEventHandlerPool.alloc(TouchEvent.UP, pointerId, x, y));
                return true;
            }
        }

        return false;
    }

    @Override
    public void onPause() {
    }

    private float translateMotionX(float x) {
        return -width/2f + x;
    }

    private float translateMotionY(float y) {
        return height/2f - y;
    }

    public class TouchEventHandler implements Runnable {
        public int pointerId;
        public int action;
        public float x, y;
        public TouchEventHandler next = null;

        public TouchEventHandler(int action, int pointerId, float x, float y) {
            this.action = action;
            this.pointerId = pointerId;
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {
            driveWheel.onTouch(action, pointerId, x, y);
            fireButton.onTouch(action, pointerId, x, y);
            map.updatePlayer(driveWheel.getDirection(), fireButton.isPressed());
            GameView.this.requestRender();
            touchEventHandlerPool.free(this);
        }
    }

    public class TouchEventHandlerPool {
        private TouchEventHandler firstAvailHandler = null;
        private int count = 0;
        private int maxSize;

        public TouchEventHandlerPool(int maxSize) {
            this.maxSize = maxSize;
        }

        public synchronized TouchEventHandler alloc(int action, int pointerId, float x, float y) {
            TouchEventHandler handler = null;
            if(firstAvailHandler != null) {
                handler = firstAvailHandler;
                handler.action = action;
                handler.pointerId = pointerId;
                handler.x = x;
                handler.y = y;
                firstAvailHandler = firstAvailHandler.next;
                --count;
            } else {
                handler = new TouchEventHandler(action, pointerId, x, y);
            }
            return handler;
        }

        public synchronized void free(TouchEventHandler handler) {
            if(count == maxSize) {
                return;
            }

            handler.next = firstAvailHandler;
            firstAvailHandler = handler;
            ++count;
        }
    }
}
