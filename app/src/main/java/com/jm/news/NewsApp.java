package com.jm.news;

import android.app.Application;

import com.jm.news.common.Common;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.LogUtils;

public class NewsApp extends Application {
    private static final String TAG = "NewsApp";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "onCreate: ");
        LogUtils.setLogPrintFlag(LogUtils.LOG_TYPE_ALL);
        Common.getInstance().initialize(getApplicationContext());
        CommonUtils.getInstance().initialize(getApplicationContext());
    }


}
