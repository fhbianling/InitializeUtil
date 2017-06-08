package com.bian.debugbox.box;

/**
 * author 边凌
 * date 2017/4/7 10:31
 * desc ${TODO}
 */

abstract class AbsOptionsClientWrap<T> implements OptionsClientWrap<T>{
    private boolean first=true;
    public String getCurrentState() {
        if (first){
            onResult(getCurrentValue());
            first=false;
        }
        return "当前" + getOptionsName() + ":" + String.valueOf(getCurrentValue());
    }
}
