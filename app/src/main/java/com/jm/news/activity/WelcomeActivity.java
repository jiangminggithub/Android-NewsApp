package com.jm.news.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
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
    private static final int PERMISSION_REQUEST_CODE = 1;
    // control field
    private MProgressView mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_welcome);
        mProgressView = findViewById(R.id.mpv_progress);

        CommonUtils.getInstance().checkPermissions(WelcomeActivity.this);

        mProgressView.setProgressListener(new MyProgressListener());
        mProgressView.setOnClickListener(new MyOnClickListener());
        mProgressView.setTimeMillis(PROGRESS_TIME);
        mProgressView.start();
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

    /**************************** private function *****************************/
//    private void checkPresion() {
//        int readStorageState = ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
//        int writeStorageState = ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int networkState = ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
//        int internetState = ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.INTERNET);
//
//        Log.d(TAG, "checkPresion: readStorageState = " + readStorageState + " , writeStorageState = " + writeStorageState + ", networkState = " + networkState + ", internetState = " + internetState);
//
////        if (readStorageState != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
////        }
//        if (writeStorageState != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        }
////        if (networkState != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);
////        }
////        if (internetState != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
////        }
//
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: requestCode = " + requestCode + ", permissions = " + permissions.toString() + ", grantResults = " + grantResults.toString());
        if (requestCode == PERMISSION_REQUEST_CODE) {

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
