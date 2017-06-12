package com.bian.debugbox.sample;

import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import com.bian.debugbox.box.InitializeUtil;
import com.bian.debugbox.box.client.BooleanClient;
import com.bian.debugbox.box.client.IpSettingClient;

/**
 * author 边凌
 * date 2017/4/11 15:30
 * desc ${TODO}
 */

public class TestApplication extends Application {
    static String sIp="";
    @Override
    public void onCreate() {
        super.onCreate();
        InitializeUtil.init(this);
        InitializeUtil.addBooleanClient(new BooleanClient() {
            @Override
            public void onBooleanResult(boolean result) {
                Toast.makeText(TestApplication.this, result + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public String getOptionsName() {
                return "简单测试";
            }
        });
        InitializeUtil.addIpSettingClient(new IpSettingClient() {
            @Override
            public void onIpSelected(String ip) {
                if (TextUtils.isEmpty(ip)) {
                    Toast.makeText(TestApplication.this, "Ip为空的回调", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TestApplication.this, ip, Toast.LENGTH_LONG).show();
                }
                TestApplication.sIp =ip;
            }
        });
        InitializeUtil.setDefaultIp("http://192.168.1.44:8080");
    }
}
