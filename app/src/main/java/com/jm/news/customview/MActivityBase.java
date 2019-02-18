package com.jm.news.customview;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.jm.news.common.Common;
import com.jm.news.util.LogUtils;

public class MActivityBase extends AppCompatActivity {
    private static final String TAG = "MActivityBase";

    @Override
    protected void attachBaseContext(Context newBase) {
        LogUtils.d(TAG, "attachBaseContext: BaseLocale = " + newBase.getResources().getConfiguration().locale);
        Context context = Common.getInstance().attachBaseContext(newBase);
        if (null != context) {
            super.attachBaseContext(context);
            LogUtils.d(TAG, "attachBaseContext: NewLocale = " + context.getResources().getConfiguration().locale);
            return;
        }
        super.attachBaseContext(newBase);
    }
}
