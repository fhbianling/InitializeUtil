package com.bian.debugbox.box;

import android.util.Log;

final class L {
    private final static int E = 1;
    private final static int D = 2;
    private final static int I = 3;
    private final static int V = 4;
    private final static int JSON = 5;
    private static String TAG = "InitializeUtil";
    private volatile static boolean DEBUG = true;

    private L() {
        throw new UnsupportedOperationException();
    }

    static void setDEBUG(boolean DEBUG) {
        L.DEBUG = DEBUG;
    }

    static void i(Object message) {
        log(I, TAG, message);
    }

    static void e(Object message) {
        log(E, TAG, message);
    }

    static void d(Object message) {
        log(D, TAG, message);
    }

    private static void log(int level, String tag, Object message) {
        if (!DEBUG) {
            return;
        }

        String logContent = message == null ? "null" : message.toString();
        switch (level) {
            case E:
                Log.e(tag, logContent);
                break;
            case D:
                Log.d(tag, logContent);
                break;
            case I:
                Log.i(tag, logContent);
                break;
            case V:
                Log.v(tag, logContent);
                break;
            case JSON:
                Log.d(tag, logContent);
                break;
        }

    }

}
