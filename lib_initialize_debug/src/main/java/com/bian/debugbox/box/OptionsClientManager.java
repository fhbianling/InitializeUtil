package com.bian.debugbox.box;

import android.support.annotation.Nullable;
import android.util.Log;

import com.bian.debugbox.box.client.OptionsClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * author 边凌
 * date 2017/3/28 15:56
 * desc ${TODO}
 */
class OptionsClientManager {
    private static final LinkedHashMap<String,OptionsClient> clients = new LinkedHashMap<>();

    static List<OptionsClient> getClients() {
        List<OptionsClient> list=new ArrayList<>();
        for (Map.Entry<String, OptionsClient> entry : clients.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }

    static void addClient(OptionsClient optionsClient){
        logRepeat(optionsClient);
        clients.put(optionsClient.getOptionsName(),optionsClient);
    }

    static
    @Nullable
    OptionsClient getOptionsClient(String optionsName) {
        return clients.get(optionsName);
    }

    private static void logRepeat(OptionsClient optionsClient) {
        if (clients.containsKey(optionsClient.getOptionsName())){
            Log.e(InitializeUtil.LOG_TAG,"重复添加的统一设置项将只以最后一项为准");
        }
    }
}
