package com.bian.debugbox.box;

import android.text.TextUtils;

import com.bian.debugbox.box.client.StringClient;

/**
 * author 边凌
 * date 2017/4/7 10:46
 * desc ${TODO}
 */

class StringClientWrap extends AbsOptionsClientWrap<String> implements StringClient {
    private StringClient stringClient;
    private SharedPrefUtil sharedPrefUtil;

    StringClientWrap(StringClient stringClient) {
        this.stringClient = stringClient;
        sharedPrefUtil = SharedPrefUtil.open(InitializeUtil.CONFIG_NAME);
    }

    @Override
    public String getOptionsName() {
        return stringClient.getOptionsName();
    }

    @Override
    public void onStringSetResult(String result) {
        stringClient.onStringSetResult(result);
        sharedPrefUtil.putString(getOptionsName(), result);
    }


    @Override
    public void onResult(String s) {
        onStringSetResult(s);
    }

    public String getCurrentValue(){
        String string = sharedPrefUtil.getString(getOptionsName());
        return TextUtils.isEmpty(string)?"":string;
    }
}
