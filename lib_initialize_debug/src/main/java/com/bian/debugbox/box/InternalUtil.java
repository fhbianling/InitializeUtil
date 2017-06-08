package com.bian.debugbox.box;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * author 边凌
 * date 2017/5/22 11:04
 * desc ${TODO}
 */

class InternalUtil {
    public static String getHostFromUrl(String url) {
        try {
            URL url1 = new URL(url);
            return url1.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPortFromUrl(String url) {
        try {
            URL url1 = new URL(url);
            return String.valueOf(url1.getPort());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
