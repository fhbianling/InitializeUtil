package com.bian.debugbox.box;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
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

@SuppressWarnings("unused")
public class InitializeUtil {
    final static String CONFIG_NAME = "initialize";
    final static String LOG_TAG = "InitializeUtil";
    private final static int REQUEST_CODE = 0x12;
    @SuppressLint("StaticFieldLeak")
    private static Application application;
    //    private static Class<?> startActivity;
    private static boolean debug = true;
    private static boolean inflated = false;

    public static void init(Application application) {
        InitializeUtil.application = application;
        checkAppNull();
        ActivityLifeCycle activityLifeCycleCallBackImpl = new ActivityLifeCycle();
        Log.i(LOG_TAG, "init");
        application.registerActivityLifecycleCallbacks(activityLifeCycleCallBackImpl);
        SharedPrefUtil.init(application);
    }

    private static void checkAppNull() {
        if (application == null) {
            throw new UnsupportedOperationException("init failed:" + "didn't call InitializeUtil.init(...) method");
        }
    }

    @SuppressWarnings("WeakerAccess")
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

    private static void checkPermissionAndInflate(Activity activity) {
        if (Build.VERSION.SDK_INT < 18) {
            boolean permissionGranted = PermissionChecker.checkSelfPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW)
                    == PermissionChecker.PERMISSION_GRANTED;
            if (permissionGranted) {
                inflateButton(activity);
            } else {
                Log.e(LOG_TAG, "Permissions denied");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, REQUEST_CODE);
            }
        } else {
            inflateButton(activity);
        }
    }

    private static void inflateButton(Context context) {
        inflated = true;
        FloatingButton.inflateButton(context);
    }

    public static void onPermissionResult(int requestCode, int resultCode) {
        checkAppNull();
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            inflateButton(application);
        }
    }

    static void inflatedButtonProcess(Activity activity) {
        String simpleName = activity.getClass().getSimpleName();
        Log.i(LOG_TAG, "onActivityCreated:" + simpleName);
        if (inflated || !debug) return;
        checkPermissionAndInflate(activity);
    }
}
