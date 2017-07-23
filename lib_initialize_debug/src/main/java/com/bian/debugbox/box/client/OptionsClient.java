package com.bian.debugbox.box.client;

/**
 * author 边凌
 * date 2017/3/28 15:46
 * desc ${TODO}
 */

public interface OptionsClient<T> {
    String getOptionsName();
    void onResult(T result);
}

