package com.bian.debugbox.box;

import android.content.Context;
import android.support.annotation.Nullable;

import com.bian.debugbox.box.client.BooleanClient;
import com.bian.debugbox.box.client.FloatClient;
import com.bian.debugbox.box.client.IpSettingClient;
import com.bian.debugbox.box.client.NumberClient;
import com.bian.debugbox.box.client.OptionsClient;
import com.bian.debugbox.box.client.StringClient;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/3/28 15:56
 * desc ${TODO}
 */
class OptionsClientManager {
    private static final List<OptionsClientWrap> clients = new ArrayList<>();
    private static IpSettingClient ipSettingClient;

    static List<OptionsClientWrap> getClients() {
        return clients;
    }

    static void addIpSettingClient(Context context, IpSettingClient ipSettingClient) {
        OptionsClientManager.ipSettingClient = ipSettingClient;
        clients.add(new IpSettingClientWrap(ipSettingClient, context));
    }

    static IpSettingClient getIpSettingClient() {
        return ipSettingClient;
    }

    static void addStringClient(StringClient stringClient) {
        if (checkRepeat(stringClient)) return;
        clients.add(new StringClientWrap(stringClient));
    }

    static
    @Nullable
    OptionsClientWrap getOptionsClientWrap(String optionsName) {
        for (OptionsClientWrap client : clients) {
            if (client.getOptionsName().equals(optionsName)) {
                return client;
            }
        }
        return null;
    }

    static void addNumberClient(NumberClient numberClient) {
        if (checkRepeat(numberClient)) return;
        clients.add(new NumberClientWrap(numberClient));
    }

    static void addFloatClient(FloatClient floatClient) {
        if (checkRepeat(floatClient)) return;
        clients.add(new FloatClientWrap(floatClient));
    }

    static void addBooleanClient(BooleanClient booleanClient) {
        if (checkRepeat(booleanClient)) return;
        clients.add(new BooleanClientWrap(booleanClient));
    }


    private static boolean checkRepeat(OptionsClient optionsClient) {
        for (OptionsClientWrap client : clients) {
            if (client.getOptionsName().equals(optionsClient.getOptionsName())) {
                return true;
            }
        }
        return false;
    }
}
