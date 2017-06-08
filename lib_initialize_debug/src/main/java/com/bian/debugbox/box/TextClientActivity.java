package com.bian.debugbox.box;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

/**
 * author 边凌
 * date 2017/3/28 15:49
 * desc ${TODO}
 */

public class TextClientActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private final static String KEY_NAME = "TextClientActivity";
    private TextView tv;
    private EditText et;
    private Button btn;
    private CheckBox cb;
    private OptionsClientWrap optionsClientWrap;

    public static void start(int requestCode, Activity activity, OptionsClientWrap optionsClientWrap) {
        Intent starter = new Intent(activity, TextClientActivity.class);
        starter.putExtra(KEY_NAME, optionsClientWrap.getOptionsName());
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string);
        findView();
        setListener();
        String stringExtra = getIntent().getStringExtra(KEY_NAME);
        optionsClientWrap = OptionsClientManager.getOptionsClientWrap(stringExtra);
        if (optionsClientWrap != null) {
            if (optionsClientWrap instanceof BooleanClientWrap) {
                cb.setVisibility(View.VISIBLE);
                tv.setVisibility(View.GONE);
                et.setVisibility(View.GONE);
                cb.setText(optionsClientWrap.getOptionsName());
                cb.setOnCheckedChangeListener(this);
                cb.setChecked((Boolean) optionsClientWrap.getCurrentValue());
            }

            tv.setText(optionsClientWrap.getOptionsName());
            if (optionsClientWrap instanceof NumberClientWrap) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if (optionsClientWrap instanceof FloatClientWrap) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            et.setText(String.valueOf(optionsClientWrap.getCurrentValue()));

        }
    }

    private void setListener() {
        btn.setOnClickListener(this);
    }

    private void findView() {
        tv = (TextView) findViewById(R.id.text_tv);
        et = (EditText) findViewById(R.id.text_et);
        btn = (Button) findViewById(R.id.text_complete);
        cb = (CheckBox) findViewById(R.id.text_cb);
    }

    @Override
    public void onClick(View v) {
        String string = et.getText().toString();
        if (optionsClientWrap instanceof StringClientWrap) {
            ((StringClientWrap) optionsClientWrap).onStringSetResult(string);
        }else if (optionsClientWrap instanceof NumberClientWrap){
            try {
                long l = Long.parseLong(string);
                ((NumberClientWrap) optionsClientWrap).onNumberResult(l);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(optionsClientWrap instanceof FloatClientWrap){
            try {
                float v1 = Float.parseFloat(string);
                ((FloatClientWrap) optionsClientWrap).onFloatResult(v1);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if (optionsClientWrap instanceof BooleanClientWrap){
            ((BooleanClientWrap) optionsClientWrap).onBooleanResult(cb.isChecked());
        }

        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        BooleanClientWrap optionsClientWrap = (BooleanClientWrap) this.optionsClientWrap;
        optionsClientWrap.onBooleanResult(isChecked);
    }
}
