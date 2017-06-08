package com.bian.debugbox.box;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;

import com.bian.debugbox.box.client.BooleanClient;
import com.bian.debugbox.box.client.FloatClient;
import com.bian.debugbox.box.client.IpSettingClient;
import com.bian.debugbox.box.client.NumberClient;
import com.bian.debugbox.box.client.StringClient;

/**
 * author 边凌
 * date 2017/3/28 15:12
 * desc ${TODO}
 */

public class InitializeUtil {
    public final static String CONFIG_NAME = "initialize";
    private static String LOG_TAG = "InitializeUtil";
    @SuppressLint("StaticFieldLeak")
    private static Application application;
    private static Class<?> startActivity;
    private static boolean debug = true;
    private static boolean inflated = false;

    public static void init(Application application, Class<?> launcherActivity) {
        InitializeUtil.application = application;
        InitializeUtil.startActivity = launcherActivity;
        checkAppNull();
        ActivityLifeCycleCallBackImpl activityLifeCycleCallBackImpl = new ActivityLifeCycleCallBackImpl();
        Log.i(LOG_TAG, "init");
        application.registerActivityLifecycleCallbacks(activityLifeCycleCallBackImpl);
        SharedPrefUtil.init(application);
    }

    private static void checkAppNull() {
        if (application == null) {
            throw new UnsupportedOperationException("init failed:" + "didn't call InitializeUtil.init(...) method");
        }
    }

    public static void setDefaultIp(String host, String port) {
        checkAppNull();
        IPDbManager.getInstance(application.getApplicationContext()).setDefaultIp(host, port);
    }

    public static void setDefaultIp(String url) {
        checkAppNull();
        String host = InternalUtil.getHostFromUrl(url);
        String port = InternalUtil.getPortFromUrl(url);
        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(port)) {
            Log.e(LOG_TAG, "setDefaultIp(String url):parse url error");
        } else {
            setDefaultIp(host, port);
        }
    }

    public static void setDebug(boolean debug) {
        checkAppNull();
        InitializeUtil.debug = debug;
    }

    public static void addIpSettingClient(IpSettingClient ipSettingClient) {
        checkAppNull();
        OptionsClientManager.addIpSettingClient(application.getApplicationContext(), ipSettingClient);
    }

    public static void addStringClient(StringClient stringClient) {
        checkAppNull();
        OptionsClientManager.addStringClient(stringClient);
    }

    public static void addNumberClient(NumberClient numberClient) {
        checkAppNull();
        OptionsClientManager.addNumberClient(numberClient);
    }

    public static void addFloatClient(FloatClient floatClient) {
        checkAppNull();
        OptionsClientManager.addFloatClient(floatClient);
    }

    public static void addBooleanClient(BooleanClient booleanClient) {
        checkAppNull();
        OptionsClientManager.addBooleanClient(booleanClient);
    }

    private static void inflateFloatingButton(Activity activity) {
        Log.i(LOG_TAG, "inflate FloatingButton");
        inflated = true;
        boolean permissionGranted = PermissionChecker.checkSelfPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW)
                == PermissionChecker.PERMISSION_GRANTED;
        if (permissionGranted) {
            FloatingButton.inflateButton(activity);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 0x12);
        }
    }

    private static class ActivityLifeCycleCallBackImpl implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            String simpleName = activity.getClass().getSimpleName();
            Log.i(LOG_TAG, "onActivityCreated:" + simpleName);
            if (inflated) return;
            boolean shouldInflateUtil = TextUtils.equals(simpleName, startActivity.getSimpleName()) && debug;
            if (shouldInflateUtil) {
                inflateFloatingButton(activity);
            }
        }


        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
}
