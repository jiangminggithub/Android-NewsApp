package com.jm.news.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.customview.MActivityBase;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.JumpUtils;
import com.jm.news.util.LogUtils;
import com.jm.news.viewmodel.LoginActivityViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends MActivityBase {

    // static field
    private static final String TAG = "LoginActivity";
    private static final int INPUT_TEXT_MIN_SIZE = 6;
    // control field
    private ImageButton mIbNavigationBack;
    private TextView mTvNavigationTitle;
    private EditText mEtLoginUsername;
    private ImageView mIvLoginUsernameDel;
    private ImageView mIvLoginPwdDel;
    private EditText mEtLoginPwd;
    private Button mBtnLoginSubmit;
    private Button mBtnLoginRegister;
    private CheckBox mCbRememberLogin;
    private TextView mTvForgetPwd;
    private SweetAlertDialog mDialog;
    // function related field
    private Handler mHandler;
    private EtTextChangeWatcher mEtTextChangeWatcher;
    private MyBtnOnClickListener mBtnOnClickListener;
    // viewmodel related field
    private LoginActivityViewModel mViewModel;
    private LoginObserve mLoginObserve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate: ");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login);

        mIbNavigationBack = findViewById(R.id.ib_navigation_back);
        mTvNavigationTitle = findViewById(R.id.tv_navigation_label);
        mEtLoginUsername = findViewById(R.id.et_login_username);
        mIvLoginUsernameDel = findViewById(R.id.iv_login_username_del);
        mEtLoginPwd = findViewById(R.id.et_login_pwd);
        mIvLoginPwdDel = findViewById(R.id.iv_login_pwd_del);
        mBtnLoginSubmit = findViewById(R.id.bt_login_submit);
        mBtnLoginRegister = findViewById(R.id.bt_login_register);
        mCbRememberLogin = findViewById(R.id.cb_remember_login);
        mTvForgetPwd = findViewById(R.id.tv_login_forget_pwd);

        mViewModel = ViewModelProviders.of(this).get(LoginActivityViewModel.class);
        mHandler = new Handler();
        mEtTextChangeWatcher = new EtTextChangeWatcher();
        mLoginObserve = new LoginObserve();
        mBtnOnClickListener = new MyBtnOnClickListener();

        mTvNavigationTitle.setText(R.string.account_login);
        mIbNavigationBack.setOnClickListener(mBtnOnClickListener);
        mIvLoginUsernameDel.setOnClickListener(mBtnOnClickListener);
        mIvLoginPwdDel.setOnClickListener(mBtnOnClickListener);
        mBtnLoginSubmit.setOnClickListener(mBtnOnClickListener);
        mBtnLoginRegister.setOnClickListener(mBtnOnClickListener);
        mTvForgetPwd.setOnClickListener(mBtnOnClickListener);
        mEtLoginUsername.addTextChangedListener(mEtTextChangeWatcher);
        mEtLoginPwd.addTextChangedListener(mEtTextChangeWatcher);

        mViewModel.getLoginStatus().observe(this, mLoginObserve);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart: ");
        if (null != mViewModel) {
            mEtLoginUsername.setText(mViewModel.getAccountName());
            mEtLoginPwd.setText(null);
        }
        mEtTextChangeWatcher.afterTextChanged(null);
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy: ");
        mIbNavigationBack = null;
        mTvNavigationTitle = null;
        mEtLoginUsername = null;
        mIvLoginUsernameDel = null;
        mEtLoginPwd = null;
        mIvLoginPwdDel = null;
        mBtnLoginSubmit = null;
        mBtnLoginRegister = null;
        mCbRememberLogin = null;
        mTvForgetPwd = null;
        mEtTextChangeWatcher = null;
        mHandler = null;
        mDialog = null;
        mViewModel = null;
        mLoginObserve = null;
        super.onDestroy();
    }

    /************************************ observe function ***********************************/
    private class LoginObserve implements Observer<Integer> {

        @Override
        public void onChanged(@Nullable Integer integer) {
            LogUtils.d(TAG, "onChanged: change = " + integer);
            if (null != mDialog) {
                if (null != integer) {
                    switch (integer) {
                        case LoginActivityViewModel.LOGIN_STATUS_NO_USER:
                            CommonUtils.getInstance().showToastView(R.string.toast_account_user_no_exit);
                            break;
                        case LoginActivityViewModel.LOGIN_STATUS_SUCCESS:
                            finish();
                            break;
                        case LoginActivityViewModel.LOGIN_STATUS_FAILED:
                            CommonUtils.getInstance().showToastView(R.string.toast_account_login_failed);
                            break;
                        default:
                            break;
                    }
                    mDialog.dismiss();
                }
            }
        }
    }


    /************************************ listener function *****************************************/
    private class EtTextChangeWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String username = mEtLoginUsername.getText().toString().trim();
            String pwd = mEtLoginPwd.getText().toString().trim();

            // 是否显示清除按钮
            if (username.length() > 0) {
                mIvLoginUsernameDel.setVisibility(View.VISIBLE);
            } else {
                mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
            }
            if (pwd.length() > 0) {
                mIvLoginPwdDel.setVisibility(View.VISIBLE);
            } else {
                mIvLoginPwdDel.setVisibility(View.INVISIBLE);
            }

            if (!TextUtils.isEmpty(pwd) && pwd.length() >= INPUT_TEXT_MIN_SIZE && !TextUtils.isEmpty(username)) {
                mBtnLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit);
                mBtnLoginSubmit.setEnabled(true);
            } else {
                mBtnLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
                mBtnLoginSubmit.setEnabled(false);
            }
        }
    }

    private class MyBtnOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_navigation_back:
                    finish();
                    break;
                case R.id.bt_login_submit:
                    mDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    mDialog.setContentText(Common.getInstance().getResourcesString(R.string.account_logging));
                    mDialog.setCancelable(false);
                    mDialog.show();
                    mHandler.postDelayed(new LoginRunnable(), 1000);
                    break;
                case R.id.bt_login_register:
                    JumpUtils.jumpActivity(LoginActivity.this, RegisterActivity.class);
                    break;
                case R.id.iv_login_username_del:
                    mEtLoginUsername.setText(null);
                    break;
                case R.id.iv_login_pwd_del:
                    mEtLoginPwd.setText(null);
                    break;
                case R.id.tv_login_forget_pwd:
                    // TODO forget function
                    break;
                default:
                    break;
            }
        }
    }

    /************************************ inner class *****************************************/
    private class LoginRunnable implements Runnable {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String userName = mEtLoginUsername.getText().toString().trim();
                    String userPwd = mEtLoginPwd.getText().toString().trim();
                    boolean isAutoLogin = mCbRememberLogin.isChecked();
                    if (null != mViewModel && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPwd)) {
                        mViewModel.loginClicked(userName, userPwd, isAutoLogin);
                    }
                }
            });

        }
    }

}
