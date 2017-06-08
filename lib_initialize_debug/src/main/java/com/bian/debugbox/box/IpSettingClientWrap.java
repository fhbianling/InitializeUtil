package com.bian.debugbox.box;

import android.content.Context;
import android.support.annotation.Nullable;

import com.bian.debugbox.box.client.IpSettingClient;

/**
 * author 边凌
 * date 2017/4/7 10:29
 * desc ${TODO}
 */
class IpSettingClientWrap extends IpSettingClient implements OptionsClientWrap<String> {
    private IpSettingClient ipSettingClient;
    private Context context;

    IpSettingClientWrap(IpSettingClient ipSettingClient, Context context) {
        this.ipSettingClient = ipSettingClient;
        this.context = context;
    }

    @Override
    public void onIpSelected(@Nullable String ip) {
        ipSettingClient.onIpSelected(ip);
    }

    @Override
    public void onResult(String s) {
        //空实现即可
    }

    @Override
    public String getCurrentValue() {
        IPDbManager.IPEntity ipEntity = IPDbManager.getInstance(context).querySelected();
        return ipEntity==null?"":ipEntity.getIp();
    }

    @Override
    public String getCurrentState() {
        IPDbManager.IPEntity ipEntity = IPDbManager.getInstance(context).querySelected();
        if (ipEntity != null) {
            String ip = ipEntity.getIp();
            onIpSelected(ip);
            return "当前" + getOptionsName() + ":" + ip;
        } else {
            return "";
        }
    }
}
