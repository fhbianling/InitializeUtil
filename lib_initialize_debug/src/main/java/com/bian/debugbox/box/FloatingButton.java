package com.bian.debugbox.box;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import static com.bian.debugbox.box.InitializeUtil.LOG_TAG;
import static com.bian.debugbox.box.InternalUtil.getScreenWidth;
import static com.bian.debugbox.box.InternalUtil.getStatusBarHeight;

/**
 * author 边凌
 * date 2017/6/8 17:43
 * desc ${TODO}
 */

class FloatingButton implements View.OnTouchListener {
    @SuppressLint("StaticFieldLeak")
    private static volatile FloatingButton sInstance;
    private static WindowManager.LayoutParams sLayoutParams;
    private int statusBarHeight, screenWidth;
    private View mView;
    private Context context;
    private WindowManager wm;
    private Point mPos = new Point();
    private Point mLastPos = new Point();
    private int scaledTouchSlop;
    private int xOffSet, yOffSet;

    private FloatingButton(Context context) {
        mView = View.inflate(context.getApplicationContext(), R.layout.float_window, null);
        this.context = context.getApplicationContext();
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = getScreenWidth(context);
        statusBarHeight = getStatusBarHeight(context);
        mLastPos.x = -1;
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mView.setOnTouchListener(this);
        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                xOffSet =mView.getWidth()/2;
                yOffSet =mView.getHeight()/2;
            }
        });
    }

    private static FloatingButton getInstance(Context context) {
        if (sInstance == null) {
            synchronized (FloatingButton.class) {
                if (sInstance == null) {
                    sInstance = new FloatingButton(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    static void setVisible(boolean visible) {
        Log.d(LOG_TAG, "setVisible:" + visible);
        if (sInstance != null) {
            sInstance.getView().setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    static void inflateButton(final Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = getLayoutParams();
        FloatingButton button = FloatingButton.getInstance(context);
        final View inflate = button.getView();
        wm.addView(inflate, layoutParams);
    }

    @NonNull
    private static WindowManager.LayoutParams getLayoutParams() {
        if (sLayoutParams == null) {
            int typePhone;
            if (Build.VERSION.SDK_INT > 18 && Build.VERSION.SDK_INT < 25) {
                typePhone = WindowManager.LayoutParams.TYPE_TOAST;
            } else {
                typePhone = WindowManager.LayoutParams.TYPE_PHONE;
            }
            sLayoutParams = new WindowManager.LayoutParams(
                    typePhone, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            sLayoutParams.gravity = Gravity.TOP | Gravity.END;
            sLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            sLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            sLayoutParams.format = PixelFormat.RGBA_8888;
        }
        return sLayoutParams;
    }

    private Object readResolve() {
        return sInstance;
    }

    private View getView() {
        return mView;
    }

    private boolean shouldOnClick(MotionEvent event) {
        if (mLastPos.x == -1) {
            resetLastPos(event);
            return false;
        }
        double pow1 = Math.pow(mLastPos.x - event.getRawX(), 2);
        double pow2 = Math.pow(mLastPos.y - event.getRawY(), 2);

        resetLastPos(event);
        double sqrt = Math.sqrt(pow1 + pow2);
        return sqrt < scaledTouchSlop;
    }

    private void resetLastPos(MotionEvent event) {
        mLastPos.x = (int) event.getRawX();
        mLastPos.y = (int) event.getRawY();
    }

    private int getMoveY() {
        return mPos.y - statusBarHeight-yOffSet;
    }

    private int getMoveX() {
        return screenWidth - mPos.x-xOffSet;
    }

    private void updateWindowPosition(int x, int y) {
        WindowManager.LayoutParams layoutParams = getLayoutParams();
        layoutParams.x = x;
        layoutParams.y = y;
        wm.updateViewLayout(mView, layoutParams);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (InitializeHomeActivity.isExisting()) return false;
        mPos.x = (int) event.getRawX();
        mPos.y = (int) event.getRawY();
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                resetLastPos(event);
                break;
            case MotionEvent.ACTION_MOVE:
                updateWindowPosition(getMoveX(), getMoveY());
                break;

            case MotionEvent.ACTION_UP:
                if (shouldOnClick(event) && !InitializeHomeActivity.isExisting()) {
                    InitializeHomeActivity.start(context);
                } else {
                    updateWindowPosition(getMoveX(), getMoveY());
                }
                break;
        }
        return true;
    }
}