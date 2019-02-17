package com.jm.news.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
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
import com.jm.news.define.BaseActivity;
import com.jm.news.util.CommonUtils;
import com.jm.news.viewmodel.LoginActivityViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final boolean BUTTON_NORMAL = true;
    private static final boolean BUTTON_LOCKED = false;
    private static final int INPUT_TEXT_MIN_SIZE = 6;

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

    private Handler mHandler;
    private EtTextChangeWatcher mEtTextChangeWatcher;
    private MyBtnOnClickListener mBtnOnClickListener;
    private MyBtnOnTouchListener mBtnOnTouchListener;

    private LoginActivityViewModel mViewModel;
    private LoginObserve mLoginObserve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setContentView(R.layout.activity_login);
        } else {
            setContentView(R.layout.layout_login_compat);
        }
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
        mBtnOnTouchListener = new MyBtnOnTouchListener();

        mTvNavigationTitle.setText(R.string.account_login);
        mIbNavigationBack.setOnClickListener(mBtnOnClickListener);
        mIvLoginUsernameDel.setOnClickListener(mBtnOnClickListener);
        mIvLoginPwdDel.setOnClickListener(mBtnOnClickListener);
        mBtnLoginSubmit.setOnClickListener(mBtnOnClickListener);
        mBtnLoginSubmit.setOnTouchListener(mBtnOnTouchListener);
        mBtnLoginRegister.setOnClickListener(mBtnOnClickListener);
        mBtnLoginRegister.setOnTouchListener(mBtnOnTouchListener);
        mTvForgetPwd.setOnClickListener(mBtnOnClickListener);
        mEtLoginUsername.addTextChangedListener(mEtTextChangeWatcher);
        mEtLoginPwd.addTextChangedListener(mEtTextChangeWatcher);

        mViewModel.getLoginStatus().observe(this, mLoginObserve);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        if (null != mViewModel) {
            mEtLoginUsername.setText(mViewModel.getAccountName());
            mEtLoginPwd.setText(null);
        }
        mEtTextChangeWatcher.afterTextChanged(null);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_navigation_back:
                this.finish();
                break;
            case R.id.bt_login_submit:
                mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mDialog.setContentText(Common.getInstance().getResourcesString(R.string.account_logging));
                mDialog.setCancelable(false);
                mDialog.show();
                mHandler.postDelayed(new LoginRunable(), 1000);
                break;
            case R.id.bt_login_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
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

    /************************************ observe function ***********************************/

    private class LoginObserve implements Observer<Integer> {

        @Override
        public void onChanged(@Nullable Integer integer) {
            Log.d(TAG, "onChanged: change = " + integer);
            if (null != mDialog) {
                if (null != integer) {
                    switch (integer) {
                        case LoginActivityViewModel.LOGIN_STATUS_NO_USER:
                            CommonUtils.getInstance().showToastView(R.string.account_user_no_exit);
                            break;
                        case LoginActivityViewModel.LOGIN_STATUS_SUCCESS:
                            finish();
                            break;
                        case LoginActivityViewModel.LOGIN_STATUS_FAILED:
                            CommonUtils.getInstance().showToastView(R.string.account_login_failed);
                            break;
                        default:
                            break;
                    }
                    mDialog.dismiss();
                }
            }
        }
    }


    /************************************ inner class *****************************************/

    private class LoginRunable implements Runnable {

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
                setBtnLoginSubmitStatus(BUTTON_NORMAL);
                mBtnLoginSubmit.setEnabled(true);
            } else {
                mBtnLoginSubmit.setEnabled(false);
                setBtnLoginSubmitStatus(BUTTON_LOCKED);
            }
        }
    }

    private void setBtnLoginSubmitStatus(boolean status) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (status == BUTTON_NORMAL) {
                mBtnLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            } else {
                mBtnLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            }
        } else {
            if (status == BUTTON_NORMAL) {
                mBtnLoginSubmit.setBackgroundColor(getResources().getColor(R.color.account_login_submit));
            } else {
                mBtnLoginSubmit.setBackgroundColor(getResources().getColor(R.color.account_login_submit_lock));
            }
        }
    }

    private void setBtnLoginRegisterStatus(boolean status) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (status == BUTTON_NORMAL) {
                mBtnLoginRegister.setBackgroundResource(R.drawable.bg_login_register);
            } else {
                mBtnLoginRegister.setBackgroundResource(R.drawable.bg_login_register);
            }
        } else {
            if (status == BUTTON_NORMAL) {
                mBtnLoginRegister.setBackgroundColor(getResources().getColor(R.color.account_login_register_normal));
            } else {
                mBtnLoginRegister.setBackgroundColor(getResources().getColor(R.color.account_login_register_pressed));
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
                    mHandler.postDelayed(new LoginRunable(), 1000);
                    break;
                case R.id.bt_login_register:
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
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

    private class MyBtnOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int id = v.getId();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (id == R.id.bt_login_submit) {
                    setBtnLoginSubmitStatus(BUTTON_LOCKED);
                } else if (id == R.id.bt_login_register) {
                    setBtnLoginRegisterStatus(BUTTON_LOCKED);
                } else {

                }
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (id == R.id.bt_login_submit) {
                    setBtnLoginSubmitStatus(BUTTON_NORMAL);
                } else if (id == R.id.bt_login_register) {
                    setBtnLoginRegisterStatus(BUTTON_NORMAL);
                } else {

                }
            }
            return false;
        }
    }

}
