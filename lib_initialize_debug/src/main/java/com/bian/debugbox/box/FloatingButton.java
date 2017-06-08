package com.bian.debugbox.box;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
/**
 * author 边凌
 * date 2017/6/8 17:43
 * desc ${TODO}
 */

 class FloatingButton {


    static void inflateButton(final Context context){
        WindowManager wm= (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        layoutParams.gravity= Gravity.TOP|Gravity.END;
        layoutParams.width= WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
        final View inflate = View.inflate(context.getApplicationContext(), R.layout.float_window, null);
        wm.addView(inflate,layoutParams);
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitializeActivity.start(context);
            }
        });
    }
}
