package com.jm.news.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.jm.news.R;
import com.jm.news.customview.MActivityBase;
import com.jm.news.customview.MProgressView;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.JumpUtils;
import com.jm.news.util.LogUtils;

public class WelcomeActivity extends MActivityBase {

    // static field
    private static final String TAG = "WelcomeActivity";
    private static final int PROGRESS_SUCCESS = 0;
    private static final long PROGRESS_TIME = 1800;
    // control field
    private MProgressView mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate: ");
        // Android P 异型屏全屏显示
        CommonUtils.setFullScreenWindowLayout(getWindow());
        setContentView(R.layout.activity_welcome);
        mProgressView = findViewById(R.id.mpv_progress);

        mProgressView.setProgressListener(new MyProgressListener());
        mProgressView.setOnClickListener(new MyOnClickListener());
        mProgressView.setTimeMillis(PROGRESS_TIME);

        if (isHasPermission()) {
            mProgressView.start();
        }
    }

    @Override
    protected void onStop() {
        LogUtils.d(TAG, "onStop: ");
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy: ");
        mProgressView = null;
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtils.d(TAG, "onRequestPermissionsResult: requestCode = " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CommonUtils.PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mProgressView.start();
            }
        }
    }

    /**************************** listener function *****************************/
    private class MyProgressListener implements MProgressView.OnProgressListener {
        @Override
        public void onProgress(int progress) {
            if (progress == PROGRESS_SUCCESS) {
                JumpUtils.jumpActivity(WelcomeActivity.this, MainActivity.class);
            }
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (null != mProgressView) {
                mProgressView.stop();
                JumpUtils.jumpActivity(WelcomeActivity.this, MainActivity.class);
            }
        }
    }
}
