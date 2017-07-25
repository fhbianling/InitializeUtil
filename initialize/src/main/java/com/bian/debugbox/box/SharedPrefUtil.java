package com.bian.debugbox.box;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.bian.debugbox.box.client.OptionsClient;

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

    private SharedPreferences preference;

    private SharedPrefUtil(String preferenceName,Context context) {
        preference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    void putString(String key, String value) {
        SharedPreferences.Editor editor = this.preference.edit();
        editor.putString(key, value);
        editor.apply();
    }

    String getString(OptionsClient client) {
        String result = this.preference.getString(client.getOptionsName(), "");
        return TextUtils.isEmpty(result)?"":result;
    }

}
