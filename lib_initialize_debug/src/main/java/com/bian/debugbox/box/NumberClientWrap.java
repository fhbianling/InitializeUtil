package com.bian.debugbox.box;

import com.bian.debugbox.box.client.NumberClient;

/**
 * author 边凌
 * date 2017/4/7 15:51
 * desc ${TODO}
 */

class NumberClientWrap extends AbsOptionsClientWrap<Long> implements NumberClient {
    private NumberClient numberClient;
    private SharedPrefUtil sharedPrefUtil;

    NumberClientWrap(NumberClient numberClient) {
        this.numberClient = numberClient;
        sharedPrefUtil = SharedPrefUtil.open(InitializeUtil.CONFIG_NAME);
    }

    @Override
    public String getOptionsName() {
        return numberClient.getOptionsName();
    }

    @Override
    public void onNumberResult(long number) {
        numberClient.onNumberResult(number);
        sharedPrefUtil.putLong(getOptionsName(), number);
    }


    @Override
    public void onResult(Long aLong) {
        onNumberResult(aLong);
    }

    public Long getCurrentValue(){
        return sharedPrefUtil.getLong(getOptionsName());
    }
}
