package com.bian.debugbox.box;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * author 边凌
 * date 2017/5/22 11:04
 * desc ${TODO}
 */
class InternalUtil {
    static String getHostFromUrl(String url) {
        try {
            URL url1 = new URL(url);
            return url1.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    static String getPortFromUrl(String url) {
        try {
            URL url1 = new URL(url);
            return String.valueOf(url1.getPort());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point=new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.x;
    }

}
