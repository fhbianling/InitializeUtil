package com.bian.debugbox.box;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * author 边凌
 * date 2017/6/8 21:12
 * desc ${TODO}
 */

class ActivityLifeCycle implements Application.ActivityLifecycleCallbacks{
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        InitializeUtil.inflatedButtonProcess(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
