package com.example.cameinw.service;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OnSwipeTouchListener implements OnTouchListener {

    private Integer swipeThreshold = 100;
    private Integer swipeVelocityThreshold = 100;
    private float x1, x2, y1, y2;
    private static final String TAG = "Swipe position";
    private static final int MIN_DISTANCE = 100;
    private final GestureDetector gestureDetector;
    private Context context;

    public OnSwipeTouchListener(Context context) {
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft() {
        Toast.makeText(context.getApplicationContext(), "Sign up failed! Try again later!", Toast.LENGTH_SHORT).show();
    }

    public void onSwipeRight() {
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            switch (e1.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = e1.getX();
                    y1 = e1.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = e2.getX();
                    y2 = e2.getY();


                    float valueX = x2 - x1;
                    float valueY = y2 -y1;

                    if (Math.abs(valueX)>MIN_DISTANCE) {
                        if (x2>x1) {
                            Toast.makeText(context.getApplicationContext(), "Right!", Toast.LENGTH_SHORT).show();

                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        return true;
                    } else if (Math.abs(valueY)>MIN_DISTANCE) {
                        return false;
                    }
            }
            return false;
        }
    }
}

