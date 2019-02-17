package com.jm.news.define;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jm.news.common.Common;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void attachBaseContext(Context newBase) {
        Log.d(TAG, "attachBaseContext: BaseLocale = " + newBase.getResources().getConfiguration().locale);
        Context context = Common.getInstance().attachBaseContext(newBase);
        if (null != context) {
            super.attachBaseContext(context);
            Log.d(TAG, "attachBaseContext: NewLocale = " + context.getResources().getConfiguration().locale);
            return;
        }
        super.attachBaseContext(newBase);
    }
}
