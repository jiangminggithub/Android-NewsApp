package com.jm.news.customview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jm.news.common.Common;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.LogUtils;

public class MActivityBase extends AppCompatActivity {

    // static field
    private static final String TAG = "MActivityBase";
    // function field
    private boolean mHasPermission = false; // 是否拥有相关操作权限

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHasPermission = CommonUtils.checkPermissions(MActivityBase.this);
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtils.d(TAG, "onRequestPermissionsResult: requestCode = " + requestCode);
        if (requestCode == CommonUtils.PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                CommonUtils.showNoPermission(MActivityBase.this);
            }
        }
    }

    public boolean isHasPermission() {
        return mHasPermission;
    }
}
