package com.bian.debugbox.box;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.bian.debugbox.box.client.BooleanClient;
import com.bian.debugbox.box.client.FloatClient;
import com.bian.debugbox.box.client.NumberClient;
import com.bian.debugbox.box.client.OptionsClient;
import com.bian.debugbox.box.client.StringClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * SharePreferences工具类
 * Created by BianLing on 2016/8/29.
 */
class SharedPrefUtil {
    @SuppressLint("StaticFieldLeak")
    private static volatile SharedPrefUtil sInstance;
    public static SharedPrefUtil getInstance(Context context){
        if(sInstance==null){
            synchronized(SharedPrefUtil.class){
                if(sInstance==null){
                    sInstance=new SharedPrefUtil(InitializeUtil.CONFIG_NAME,context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private Object readResolve() {
        return sInstance;
    }

    private SharedPreferences prefrence;

    private SharedPrefUtil(String preferenceName,Context context) {
        prefrence = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    void putString(String key, String value) {
        SharedPreferences.Editor editor = this.prefrence.edit();
        editor.putString(key, value);
        editor.apply();
    }

    String getString(OptionsClient client) {
        String result = this.prefrence.getString(client.getOptionsName(), "");
        return TextUtils.isEmpty(result)?"":result;
    }

}
