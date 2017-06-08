package com.bian.debugbox.box;

import com.bian.debugbox.box.client.FloatClient;

/**
 * author 边凌
 * date 2017/4/7 15:54
 * desc ${TODO}
 */

class FloatClientWrap extends AbsOptionsClientWrap<Float> implements FloatClient{
    private FloatClient floatClient;
    private SharedPrefUtil sharedPrefUtil;
    FloatClientWrap(FloatClient floatClient) {
        this.floatClient = floatClient;
        sharedPrefUtil=SharedPrefUtil.open(InitializeUtil.CONFIG_NAME);
    }

    @Override
    public String getOptionsName() {
        return floatClient.getOptionsName();
    }

    @Override
    public void onFloatResult(float result) {
        floatClient.onFloatResult(result);
        sharedPrefUtil.putFloat(getOptionsName(),result);
    }

    @Override
    public void onResult(Float aFloat) {
        onFloatResult(aFloat);
    }

    public Float getCurrentValue(){
        return sharedPrefUtil.getFloat(getOptionsName());
    }
}
