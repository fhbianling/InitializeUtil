package com.bian.debugbox.box;

import com.bian.debugbox.box.client.BooleanClient;

/**
 * author 边凌
 * date 2017/4/7 16:17
 * desc ${TODO}
 */

class BooleanClientWrap extends AbsOptionsClientWrap<Boolean> implements BooleanClient{
    private BooleanClient booleanClient;
    private SharedPrefUtil sharedPrefUtil;
    BooleanClientWrap(BooleanClient booleanClient) {
        this.booleanClient = booleanClient;
        sharedPrefUtil=SharedPrefUtil.open(InitializeUtil.CONFIG_NAME);
    }

    @Override
    public String getOptionsName() {
        return booleanClient.getOptionsName();
    }

    @Override
    public void onBooleanResult(boolean result) {
        booleanClient.onBooleanResult(result);
        sharedPrefUtil.putBoolean(getOptionsName(),result);
    }

    @Override
    public void onResult(Boolean aBoolean) {
        onBooleanResult(aBoolean);
    }

    @Override
    public Boolean getCurrentValue() {
        return sharedPrefUtil.getBoolean(getOptionsName());
    }
}
