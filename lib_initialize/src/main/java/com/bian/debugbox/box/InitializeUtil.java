package com.bian.debugbox.box;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bian.debugbox.box.client.BooleanClient;
import com.bian.debugbox.box.client.FloatClient;
import com.bian.debugbox.box.client.IpSettingClient;
import com.bian.debugbox.box.client.NumberClient;
import com.bian.debugbox.box.client.OptionsClient;
import com.bian.debugbox.box.client.StringClient;

import java.util.List;

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
    private static boolean debug = true;
    private static boolean inflated = false;
    private static boolean sEnableValueCallBackWhenAppStart=true;

    public static void init(Application application) {
        InitializeUtil.application = application;
        checkAppNull();
        ActivityLifeCycle activityLifeCycleCallBackImpl = new ActivityLifeCycle();
        Log.i(LOG_TAG, "init");
        application.registerActivityLifecycleCallbacks(activityLifeCycleCallBackImpl);
    }

    @SuppressWarnings("WeakerAccess")
    public static void setDefaultIp(String clientName, String host, String port) {
        checkAppNull();
        IPDbManager.getInstance(application.getApplicationContext()).setDefaultIp(clientName, host, port);
    }

    public static void setDefaultIp(String clientName, String url) {
        checkAppNull();
        String host = InternalUtil.getHostFromUrl(url);
        String port = InternalUtil.getPortFromUrl(url);
        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(port)) {
            Log.e(LOG_TAG, "setDefaultIp(String url):parse url error");
        } else {
            setDefaultIp(clientName, host, port);
        }
    }

    public static void setEnableValueCallBackWhenAppStart(boolean enableValueCallBackWhenAppStart){
        InitializeUtil.sEnableValueCallBackWhenAppStart =enableValueCallBackWhenAppStart;
    }

    public static void setDebug(boolean debug) {
        checkAppNull();
        InitializeUtil.debug = debug;
    }

    public static void addIpSettingClient(IpSettingClient ipSettingClient) {
        addClient(ipSettingClient);
    }

    public static void addStringClient(StringClient stringClient) {
        addClient(stringClient);
    }

    public static void addNumberClient(NumberClient numberClient) {
        addClient(numberClient);
    }

    public static void addFloatClient(FloatClient floatClient) {
        addClient(floatClient);
    }

    public static void addBooleanClient(BooleanClient booleanClient) {
        addClient(booleanClient);
    }

    private static void checkAppNull() {
        if (application == null) {
            throw new UnsupportedOperationException("init failed:" + "didn't call InitializeUtil.init(...) method");
        }
    }

    private static void addClient(OptionsClient client) {
        checkAppNull();
        OptionsClientManager.addClient(client);
    }

    private static void checkPermissionAndInflate(Activity activity) {
        Log.d(LOG_TAG, "在" + activity.getClass().getName() + "检查悬浮窗权限");

        boolean hasPermission = isPermissionGranted(activity);
        Log.d(LOG_TAG,"悬浮窗权限检查结果："+hasPermission);
        if (hasPermission) {
            inflateButton(activity);
        } else {
            requestPermission(activity);
        }
    }

    private static void requestPermission(Activity activity) {
        Log.d(LOG_TAG,"申请悬浮窗权限");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(activity, "使用初始化弹窗需要悬浮窗权限，请开启", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, REQUEST_CODE);
        }
    }

    private static boolean isPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT > 18 && Build.VERSION.SDK_INT < 25) return true;
        boolean hasPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermission = Settings.canDrawOverlays(activity);
        } else {
            int permission = PermissionChecker.
                    checkSelfPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW);
            hasPermission = permission
                    == PermissionChecker.PERMISSION_GRANTED;
        }
        return hasPermission;
    }

    private static void inflateButton(Context context) {
        Log.d(LOG_TAG,"加载悬浮窗");
        inflated = true;
        FloatingButton.inflateButton(context);
        if (sEnableValueCallBackWhenAppStart){
            OptionsClientManager.callBackAllValue(context);
        }
    }

    public static void onPermissionResult(Activity activity) {
        checkAppNull();
        if (isPermissionGranted(activity)){
            inflateButton(activity);
        }
    }

    static void inflatedButtonProcess(Activity activity) {
        String simpleName = activity.getClass().getSimpleName();
        Log.i(LOG_TAG, "onActivityCreated:" + simpleName);
        if (inflated || !debug) return;
        checkPermissionAndInflate(activity);
    }
}
