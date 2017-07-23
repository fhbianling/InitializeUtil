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
            public String getOptionsName() {
                return "布尔测试";
            }

            @Override
            public void onResult(Boolean result) {
                Toast.makeText(TestApplication.this, result + "", Toast.LENGTH_SHORT).show();
            }
        });
        InitializeUtil.addIpSettingClient(new IpSettingClient() {
            @Override
            public String getOptionsName() {
                return "IP设置";
            }

            @Override
            public void onResult(String result) {
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(TestApplication.this, "Ip为空的回调", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TestApplication.this, result, Toast.LENGTH_LONG).show();
                }
                TestApplication.sIp =result;
            }

        });
        InitializeUtil.setDefaultIp("IP设置","http://192.168.1.44:8080");
    }
}
