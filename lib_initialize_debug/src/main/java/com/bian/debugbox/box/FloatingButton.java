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
import android.view.WindowManager;

import static com.bian.debugbox.box.InitializeUtil.LOG_TAG;

/**
 * author 边凌
 * date 2017/6/8 17:43
 * desc ${TODO}
 */

class FloatingButton {
    private int statusBarHeight;
    private View mView;
    private Context context;
    private WindowManager wm;
    private float mY;
    private float mX;
    private int screenWidth;
    private long tapTime;
    private long lastDownTime = -1;

    @SuppressLint("StaticFieldLeak")
    private static volatile FloatingButton sInstance;

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

    private Object readResolve() {
        return sInstance;
    }

    private FloatingButton(Context context) {
        mView = View.inflate(context.getApplicationContext(), R.layout.float_window, null);
        this.context = context.getApplicationContext();
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point outSize = new Point();
        wm.getDefaultDisplay().getSize(outSize);
        screenWidth = outSize.x;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        tapTime = ViewConfiguration.getTapTimeout();
    }

    private View getView() {
        return mView;
    }

    private void setListener() {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InitializeActivity.isExisting()) return;
                InitializeActivity.start(context);
            }
        });
    }

    private void setMoveMethod() {
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mX = event.getRawX();
                mY = event.getRawY();
                final int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastDownTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateWindowPosition(getMoveX(), getMoveY());
                        break;

                    case MotionEvent.ACTION_UP:
                        if (isTapClick()) {
                            return true;
                        } else {
                            updateWindowPosition(getMoveX(), getMoveY());
                        }
                        break;
                }

                return false;
            }
        });
    }

    static void setVisible(boolean visible) {
        if (sInstance != null) {
            sInstance.getView().setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isTapClick() {
        return lastDownTime != -1 && System.currentTimeMillis() - lastDownTime < tapTime;
    }

    private int getMoveY() {
        return (int) (mY - statusBarHeight);
    }

    private int getMoveX() {
        return (int) (screenWidth - mX);
    }

    private void updateWindowPosition(int x, int y) {
        Log.i(LOG_TAG, "updateWindowPosition:(" + x + "," + y + ")");
        WindowManager.LayoutParams layoutParams = getLayoutParams();
        layoutParams.x = x;
        layoutParams.y = y;
        wm.updateViewLayout(mView, layoutParams);
    }

    static void inflateButton(final Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = getLayoutParams();
        FloatingButton button = FloatingButton.getInstance(context);
        final View inflate = button.getView();
        wm.addView(inflate, layoutParams);
        button.setListener();
        button.setMoveMethod();
        Log.i(LOG_TAG, "floating button inflated");
    }

    private static WindowManager.LayoutParams sLayoutParams;

    @NonNull
    private static WindowManager.LayoutParams getLayoutParams() {
        if (sLayoutParams == null) {
            int typePhone;
            if (Build.VERSION.SDK_INT > 18) {
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
}
