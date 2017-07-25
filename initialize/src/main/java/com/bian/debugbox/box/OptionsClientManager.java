package com.bian.debugbox.box;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bian.debugbox.box.client.BooleanClient;
import com.bian.debugbox.box.client.FloatClient;
import com.bian.debugbox.box.client.IpSettingClient;
import com.bian.debugbox.box.client.NumberClient;
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
    private static final LinkedHashMap<String, OptionsClient> clients = new LinkedHashMap<>();

    static List<OptionsClient> getClients() {
        List<OptionsClient> list = new ArrayList<>();
        for (Map.Entry<String, OptionsClient> entry : clients.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }

    static void addClient(OptionsClient optionsClient) {
        checkClientIllgal(optionsClient);
        clients.put(optionsClient.getOptionsName(), optionsClient);
    }

    static
    @Nullable
    OptionsClient getOptionsClient(String optionsName) {
        return clients.get(optionsName);
    }

    private static void checkClientIllgal(OptionsClient optionsClient) {
        if (clients.containsKey(optionsClient.getOptionsName())) {
            Log.e(InitializeUtil.LOG_TAG, "repeat options client");
        }
        if (TextUtils.isEmpty(optionsClient.getOptionsName())){
            throw new IllegalArgumentException("OptionsClient must have a name");
        }
    }

    static void callBackAllValue(Context context) {
        List<OptionsClient> clients = OptionsClientManager.getClients();
        for (OptionsClient client : clients) {
            if (!(client instanceof IpSettingClient)) {
                try {
                    client.onResult(parseCallBackValue(context, client));
                } catch (Exception e) {
                    Log.e(InitializeUtil.LOG_TAG, "callback current value of(" + client.getOptionsName() + ") failed", e);
                }
            } else {
                IPDbManager.IPEntity ipEntity =
                        IPDbManager.getInstance(context).querySelected(client.getOptionsName());
                ((IpSettingClient) client).onResult(
                        ipEntity != null ?
                                ipEntity.getIp() :
                                ((IpSettingClient) client).getDefaultValue());
            }
        }
    }

    private static Object parseCallBackValue(Context context, OptionsClient client) throws Exception {
        SharedPrefUtil sharedPrefUtil = SharedPrefUtil.getInstance(context);
        String currentValue = sharedPrefUtil.getString(client);

        if (TextUtils.isEmpty(currentValue)) return client.getDefaultValue();

        if (client instanceof BooleanClient) {
            return Boolean.parseBoolean(currentValue);
        } else if (client instanceof NumberClient) {
            return Long.parseLong(currentValue);
        } else if (client instanceof FloatClient) {
            return Float.parseFloat(currentValue);
        } else {
            return currentValue;
        }
    }
}
