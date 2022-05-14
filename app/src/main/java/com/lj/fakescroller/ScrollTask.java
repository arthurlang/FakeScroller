package com.lj.fakescroller;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.MotionEvent;

import java.lang.reflect.Method;

class ScrollTask implements Runnable {
    private final String TAG = ScrollTask.this.getClass().getSimpleName();
    private static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;
    public final float srcX;
        public final float desX;
        public final float srcY;
        public final float desY;
        public final long duration;
    private static final Object[] params = new Object[]{InputEvent.class, INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH};
        public ScrollTask(Context context, float x1, float y1, float x2, float y2, long d) {
            srcX = x1;
            desX = x2;
            srcY = y1;
            desY = y2;
            duration = d;
            try {
                mInjectMethod = InputManager.class.getMethod(
                        "injectInputEvent", InputEvent.class, Integer.TYPE);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            mInputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        }

        @Override
        public void run() {
            Log.d(TAG, "run: ===> injectEvent");
            long now = SystemClock.uptimeMillis();
            injectMotionEvent(
                    MotionEvent.ACTION_DOWN, now, srcX, srcY, 1.0f);
            long startTime = now;
            long endTime = startTime + duration;
            while (now < endTime) {
                long elapsedTime = now - startTime;
                float alpha = (float) elapsedTime / duration;
                injectMotionEvent(
                        MotionEvent.ACTION_MOVE, now, compute(srcX, desX, alpha),
                        compute(srcY, desY, alpha), 1.0f);
                now = SystemClock.uptimeMillis();
            }

            injectMotionEvent(
                    MotionEvent.ACTION_UP, now, desX, desY, 0.0f);
        }
        private Method mInjectMethod;
        private final InputManager mInputManager;
        private boolean injectMotionEvent(int action, long when, float x, float y, float pressure) {
            final float DEFAULT_SIZE = 1.0f;
            final int DEFAULT_META_STATE = 0;
            final float DEFAULT_PRECISION_X = 1.0f;
            final float DEFAULT_PRECISION_Y = 1.0f;
            final int DEFAULT_DEVICE_ID = 0;
            final int DEFAULT_EDGE_FLAGS = 0;
            MotionEvent event = MotionEvent.obtain(when, when, action, x, y, pressure, DEFAULT_SIZE,
                    DEFAULT_META_STATE, DEFAULT_PRECISION_X, DEFAULT_PRECISION_Y, DEFAULT_DEVICE_ID,
                    DEFAULT_EDGE_FLAGS);
            event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
            return injectInputEvent(event);
        }

        private boolean injectInputEvent(InputEvent event) {
            boolean success = false;
            try {
                params[0] = event;
                mInjectMethod.invoke(mInputManager, params);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;
        }

        private float compute(float a, float b, float alpha) {
            return (b - a) * alpha + a;
        }

    }