package com.bian.debugbox.box;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bian.debugbox.box.client.BooleanClient;
import com.bian.debugbox.box.client.FloatClient;
import com.bian.debugbox.box.client.NumberClient;
import com.bian.debugbox.box.client.OptionsClient;
import com.bian.debugbox.box.client.StringClient;

/**
 * author 边凌
 * date 2017/3/28 15:49
 * desc ${TODO}
 */

public class TextClientActivity extends Activity implements View.OnClickListener {
    private final static String KEY_NAME = "TextClientActivity";
    private TextView tv;
    private EditText et;
    private Button btn;
    private CheckBox cb;
    private OptionsClient optionsClient;

    public static void start(int requestCode, Activity activity, OptionsClient optionsClientWrap) {
        Intent starter = new Intent(activity, TextClientActivity.class);
        starter.putExtra(KEY_NAME, optionsClientWrap.getOptionsName());
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowSetting();
        setContentView(R.layout.activity_string);
        initView();
        initUiByClientValue();
    }

    private void initUiByClientValue() {
        optionsClient = parseClient();
        if (optionsClient == null) {
            return;
        }
        String savedValue = SharedPrefUtil.getInstance(this).
                getString(optionsClient);

        tv.setText(optionsClient.getOptionsName());

        if (optionsClient instanceof BooleanClient) {
            cb.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
            et.setVisibility(View.GONE);
            cb.setText(optionsClient.getOptionsName());
            cb.setChecked(Boolean.parseBoolean(savedValue));
        }

        if (optionsClient instanceof NumberClient) {
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if (optionsClient instanceof FloatClient) {
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        et.setText(savedValue);
    }

    private OptionsClient parseClient() {
        return OptionsClientManager.getOptionsClient(getIntent().getStringExtra(KEY_NAME));
    }

    private void initWindowSetting() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.text_tv);
        et = (EditText) findViewById(R.id.text_et);
        btn = (Button) findViewById(R.id.text_complete);
        cb = (CheckBox) findViewById(R.id.text_cb);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (optionsClient instanceof BooleanClient){
            ((BooleanClient) optionsClient).onResult(cb.isChecked());
            SharedPrefUtil.getInstance(this).putString(optionsClient.getOptionsName(), String.valueOf(cb.isChecked()));
            finish();
        }else {
            String text = et.getText().toString();
            if (TextUtils.isEmpty(text)){
                showToast("请输入内容");
                return;
            }
            Object result = null;

            if (optionsClient instanceof StringClient){
                result=text;
            }else if(optionsClient instanceof NumberClient){
                result=parseLong(text);
            }else if (optionsClient instanceof FloatClient){
                result=parseFloat(text);
            }

            if (result != null) {
                SharedPrefUtil.getInstance(this).putString(optionsClient.getOptionsName(), text);
                optionsClient.onResult(result);
                finish();
            }
        }

//        String text = et.getText().toString();
//        SharedPrefUtil.getInstance(this).putString(optionsClient.getOptionsName(), text);
//
//        if (optionsClient instanceof StringClient) {
//            ((StringClient) optionsClient).onResult(text);
//        } else if (optionsClient instanceof NumberClient) {
//            try {
//                long l = Long.parseLong(text);
//                ((NumberClient) optionsClient).onResult(l);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (optionsClient instanceof FloatClient) {
//            try {
//                float v1 = Float.parseFloat(text);
//                ((FloatClient) optionsClient).onResult(v1);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (optionsClient instanceof BooleanClient) {
//            ((BooleanClient) optionsClient).onResult(cb.isChecked());
//        }
//
//        finish();
    }

    private Object parseFloat(String text) {
        try {
            return Float.parseFloat(text);
        }catch (NumberFormatException e){
            e.printStackTrace();
            showToast("请输入正确的内容");
        }
        return null;
    }

    private Object parseLong(String text) {
        try {
            return Long.parseLong(text);
        }catch (NumberFormatException e){
            e.printStackTrace();
            showToast("请输入正确的内容");
        }
        return null;
    }

    private void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}
