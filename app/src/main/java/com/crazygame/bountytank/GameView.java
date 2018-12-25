package com.crazygame.bountytank;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.View;

import com.crazygame.bountytank.controllers.DriveWheel;
import com.crazygame.bountytank.controllers.FireButton;
import com.crazygame.bountytank.event.TouchEvent;
import com.crazygame.bountytank.event.TouchEventQueue;
import com.crazygame.bountytank.opengl.SimpleShaderProgram;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameView extends GLSurfaceView implements GLSurfaceView.Renderer, Runnable,
        View.OnTouchListener {
    public final static int RUNNING = 0;
    public final static int PAUSED = 1;
    public final static int END = 2;

    private final Context context;
    private float width, height;

    private SimpleShaderProgram simpleShaderProgram;
    private final float[] projectionMatrix = new float[16];
    private final float[] viewportOrigin = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];

    private final TouchEventQueue[] eventQueues = new TouchEventQueue[2];
    private final int queueSize = 100;
    private Object waitQueueLock = new Object();
    private int waitQueueIdx;

    private Thread gameThread;
    private AtomicBoolean running = new AtomicBoolean();
    private AtomicInteger gameState = new AtomicInteger();

    private Object gameLock = new Object();
    private DriveWheel driveWheel;
    private FireButton fireButton;

    public GameView(Context context, Point size) {
        super(context);

        this.context = context;

        width = size.x;
        height = size.y;

        viewportOrigin[0] = width / 2f;
        viewportOrigin[1] = height / 2f;

        eventQueues[0] = new TouchEventQueue(queueSize);
        eventQueues[1] = new TouchEventQueue(queueSize);
        waitQueueIdx = 0;

        gameThread = new Thread(this);

        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        simpleShaderProgram = new SimpleShaderProgram(context);

        Matrix.setIdentityM(projectionMatrix, 0);

        driveWheel = new DriveWheel(width, height);
        fireButton = new FireButton(width, height);

        running.set(true);
        gameThread.start();

        gameState.set(RUNNING);

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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        simpleShaderProgram.useProgram();
        simpleShaderProgram.setProjectionMatrix(projectionMatrix, 0);
        simpleShaderProgram.setViewportOrigin(viewportOrigin, 0);

        synchronized (gameLock) {
            driveWheel.draw(simpleShaderProgram);
            fireButton.draw(simpleShaderProgram);
        }
    }

    private void updateProjectionMatrix() {
        projectionMatrix[0] = 2f / width;
        projectionMatrix[5] = 2f / height;
    }

    @Override
    public void run() {
        while(running.get()) {
            synchronized (waitQueueLock) {
                if (!eventQueues[waitQueueIdx].empty()) {
                    waitQueueIdx = (waitQueueIdx + 1) % eventQueues.length;
                }
            }

            synchronized (gameLock) {
                int processQueueIdx = (waitQueueIdx + 1) % eventQueues.length;
                TouchEventQueue processQueue = eventQueues[processQueueIdx];

                while (!processQueue.empty()) {
                    TouchEvent touchEvent = processQueue.first();
                    driveWheel.onTouch(touchEvent);
                    fireButton.onTouch(touchEvent);
                    processQueue.removeFirst();
                }
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        synchronized (waitQueueLock) {
            switch(motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                {
                    int pointerIdx = motionEvent.getActionIndex();
                    int pointerId = motionEvent.getPointerId(pointerIdx);
                    float x = translateMotionX(motionEvent.getX(pointerIdx));
                    float y = translateMotionY(motionEvent.getY(pointerIdx));
                    eventQueues[waitQueueIdx].enqueue(TouchEvent.DOWN, pointerId, x, y);
                    return true;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    int count = motionEvent.getPointerCount();
                    for(int i = 0; i < count; ++i) {
                        int pointerId = motionEvent.getPointerId(i);
                        float x = translateMotionX(motionEvent.getX(i));
                        float y = translateMotionY(motionEvent.getY(i));
                        eventQueues[waitQueueIdx].enqueue(TouchEvent.MOVE, pointerId, x, y);
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
                    eventQueues[waitQueueIdx].enqueue(TouchEvent.UP, pointerId, x, y);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onPause() {
        running.set(true);
        try {
            gameThread.join();
        } catch(InterruptedException e) {
        }
    }

    private float translateMotionX(float x) {
        return -width/2f + x;
    }

    private float translateMotionY(float y) {
        return height/2f - y;
    }
}
