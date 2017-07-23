package com.bian.debugbox.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bian.debugbox.box.InitializeUtil;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView= (TextView) findViewById(R.id.text);
    }

    public void start(View view) {
        Main2Activity.start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InitializeUtil.onPermissionResult(requestCode,resultCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView.setText(TestApplication.sIp);
    }
}
