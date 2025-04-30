package com.wonbin.autoclick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 * Created by wilburnLee on 2019/4/22.
 */
public class AutoService extends AccessibilityService {

    public static final String ACTION = "action";
    public static final String SHOW = "show";
    public static final String HIDE = "hide";
    public static final String PLAY = "play";
    public static final String STOP = "stop";

    public static final String MODE = "mode";
    public static final String TAP = "tap";
    public static final String SWIPE = "swipe";
    private FloatingView mFloatingView;
    private int mInterval;
    private int mX;
    private int mY;
    private String mMode;

    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = new FloatingView(this);
        HandlerThread handlerThread = new HandlerThread("auto-handler");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 打印开始信息
        Log.d("AutoService", "onStartCommand 被调用");
        if (intent != null) {
            String action = intent.getStringExtra(ACTION);
            Log.d("AutoService", "收到的 ACTION 是: " + action);
            if (SHOW.equals(action)) {
                mInterval = intent.getIntExtra("interval", 16) * 1000;
                mMode = intent.getStringExtra(MODE);
                Log.d("AutoService", "执行 SHOW 操作，interval=" + mInterval + ", mode=" + mMode);
                mFloatingView.show();
            } else if (HIDE.equals(action)) {
                Log.d("AutoService", "执行 HIDE 操作");
                mFloatingView.hide();
                mHandler.removeCallbacksAndMessages(null);
            } else if (PLAY.equals(action)) {
                mX = intent.getIntExtra("x", 500);
                mY = intent.getIntExtra("y", 1000);
                Log.d("AutoService", "执行 PLAY 操作，坐标 x=" + mX + ", y=" + mY + ", mInterval=" + mInterval);
                if (mRunnable == null) {
                    Log.d("AutoService", "创建新的 IntervalRunnable");
                    mRunnable = new IntervalRunnable();
                }
                Toast.makeText(getBaseContext(), "已开始", Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(mRunnable, mInterval);
            } else if (STOP.equals(action)) {
                Log.d("AutoService", "执行 STOP 操作");
                mHandler.removeCallbacksAndMessages(null);
                Toast.makeText(getBaseContext(), "已暂停", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("AutoService", "intent 为 null，未传递参数");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void playTap(int x, int y) {
        Log.d("AutoService", "执行 playTap 坐标: " + x + ", " + y);
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 100L, 1000L));
        GestureDescription gestureDescription = builder.build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                mHandler.postDelayed(mRunnable, mInterval);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
        }, null);
    }

    private void playSwipe(int fromX, int fromY, int toX, int toY) {
        Log.d("AutoService", "执行 playSwipe 坐标: (" + fromX + "," + fromY + ") -> (" + toX + "," + toY + ")");
        Path path = new Path();
        path.moveTo(fromX, fromY);
        path.lineTo(toX, toY);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 100L, 2000L));
        GestureDescription gestureDescription = builder.build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                mHandler.postDelayed(mRunnable, mInterval);
                Log.d("AutoService", "手势完成");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e("AutoService", "手势被取消");
            }
        }, null);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    private IntervalRunnable mRunnable;

    private class IntervalRunnable implements Runnable {
        @Override
        public void run() {
            Log.d("AutoService", "IntervalRunnable 正在运行，mode=" + mMode);
            if (SWIPE.equals(mMode)) {
                playSwipe(mX, mY, mX, mY - 300);
            } else {
                playTap(mX, mY);
            }
        }
    }
}
