package com.jm.news;

import android.app.Application;
import android.util.Log;

import com.jm.news.common.Common;
import com.jm.news.util.CommonUtils;

public class NewsApp extends Application {
    private static final String TAG = "NewsApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        Common.getInstance().initialize(getApplicationContext());
        CommonUtils.getInstance().initialize(getApplicationContext());
    }


}
