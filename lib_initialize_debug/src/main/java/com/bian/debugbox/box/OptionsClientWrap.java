package com.bian.debugbox.box;

import com.bian.debugbox.box.client.OptionsClient;

/**
 * author 边凌
 * date 2017/4/7 16:34
 * desc ${TODO}
 */

interface OptionsClientWrap<T> extends OptionsClient {
    void onResult(T t);

    T getCurrentValue();

    String getCurrentState();
}
