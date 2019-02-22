package com.jm.news;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.jm.news.common.Common;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.LogUtils;

public class NewsApp extends Application {

    // static field
    private static final String TAG = "NewsApp";

    static {
        // Log输出等级配置
        LogUtils.setLogPrintFlag(LogUtils.LOG_TYPE_ALL);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "onCreate: ");
        Common.getInstance().initialize(getApplicationContext());
        CommonUtils.getInstance().initialize(getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: newConfig = " + newConfig.toString());
        Common.getInstance().updateAppInfo();
    }
}
