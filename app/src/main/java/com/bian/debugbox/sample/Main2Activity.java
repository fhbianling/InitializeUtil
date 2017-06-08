package com.bian.debugbox.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
    public static void start(Context context) {
        Intent starter = new Intent(context, Main2Activity.class);
        context.startActivity(starter);
    }
}
