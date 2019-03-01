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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.jm.news.viewmodel.RegisterActivityViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends MActivityBase implements View.OnClickListener {

    // static field
    private static final String TAG = "RegisterActivity";
    private static final int INPUT_TEXT_MIN_SIZE = 6;
    // control field
    private ImageButton mIbNavigationBack;
    private TextView mTvNavigationTitle;
    private EditText mEtRegisterUsername;
    private ImageView mIvRegisterUsernameDel;
    private EditText mEtRegisterPwd;
    private ImageView mIvRegisterPwdDel;
    private Button mBtnRegisterSubmit;
    private CheckBox mCbProtocol;
    private TextView mTvProtocol;
    private SweetAlertDialog mDialog;
    // function related field
    private Handler mHandler;
    private EtTextChangeWatcher mEtTextChangeWatcher;
    private CbCheckedListener mCbCheckedListener;
    // viewmodel related field
    private RegisterActivityViewModel mViewModel;
    private RegisterObserve mRegisterObserve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_register);

        mIbNavigationBack = findViewById(R.id.ib_navigation_back);
        mTvNavigationTitle = findViewById(R.id.tv_navigation_label);
        mEtRegisterUsername = findViewById(R.id.et_register_username);
        mIvRegisterUsernameDel = findViewById(R.id.iv_register_username_del);
        mEtRegisterPwd = findViewById(R.id.et_register_pwd);
        mIvRegisterPwdDel = findViewById(R.id.iv_register_pwd_del);
        mBtnRegisterSubmit = findViewById(R.id.bt_register_submit);
        mCbProtocol = findViewById(R.id.cb_protocol);
        mTvProtocol = findViewById(R.id.tv_protocol);

        mViewModel = ViewModelProviders.of(this).get(RegisterActivityViewModel.class);
        mHandler = new Handler();
        mEtTextChangeWatcher = new EtTextChangeWatcher();
        mCbCheckedListener = new CbCheckedListener();
        mRegisterObserve = new RegisterObserve();

        mTvNavigationTitle.setText(R.string.login_register);
        mIbNavigationBack.setOnClickListener(this);
        mIvRegisterUsernameDel.setOnClickListener(this);
        mIvRegisterPwdDel.setOnClickListener(this);
        mTvProtocol.setOnClickListener(this);
        mBtnRegisterSubmit.setOnClickListener(this);
        mCbProtocol.setOnCheckedChangeListener(mCbCheckedListener);
        mEtRegisterUsername.addTextChangedListener(mEtTextChangeWatcher);
        mEtRegisterPwd.addTextChangedListener(mEtTextChangeWatcher);

        mViewModel.getRegisterStatus().observe(this, mRegisterObserve);
    }

    @Override
    protected void onStart() {
        LogUtils.d(TAG, "onStart: ");
        super.onStart();
        mEtTextChangeWatcher.afterTextChanged(null);
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy: ");
        mIbNavigationBack = null;
        mTvNavigationTitle = null;
        mEtRegisterUsername = null;
        mIvRegisterUsernameDel = null;
        mEtRegisterPwd = null;
        mIvRegisterPwdDel = null;
        mBtnRegisterSubmit = null;
        mCbProtocol = null;
        mTvProtocol = null;
        mViewModel = null;
        mHandler = null;
        mEtTextChangeWatcher = null;
        mCbCheckedListener = null;
        mRegisterObserve = null;
        mDialog = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_navigation_back:
                this.finish();
                break;
            case R.id.iv_register_username_del:
                mEtRegisterUsername.setText(null);
                break;
            case R.id.iv_register_pwd_del:
                mEtRegisterPwd.setText(null);
                break;
            case R.id.tv_protocol:
                JumpUtils.jumpActivity(RegisterActivity.this, AboutActivity.class);
                break;
            case R.id.bt_register_submit:
                mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mDialog.setContentText(Common.getInstance().getResourcesString(R.string.account_registering));
                mDialog.setCancelable(false);
                mDialog.show();
                mHandler.postDelayed(new RegisterRunnable(), 1000);
                break;
            default:
                break;
        }
    }

    /************************************ observe function ***********************************/
    private class RegisterObserve implements Observer<Integer> {

        @Override
        public void onChanged(@Nullable Integer integer) {
            LogUtils.d(TAG, "onChanged: change = " + integer);
            if (null != integer) {
                switch (integer) {
                    case RegisterActivityViewModel.REGISTER_STATUS_SUCCESS:
                        CommonUtils.getInstance().showToastView(R.string.toast_account_register_success);
                        finish();
                        break;
                    case RegisterActivityViewModel.REGISTER_STATUS_FAILED:
                        CommonUtils.getInstance().showToastView(R.string.toast_account_register_failed);
                        break;
                    default:
                        break;
                }
                mDialog.dismiss();
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
            String username = mEtRegisterUsername.getText().toString().trim();
            String pwd = mEtRegisterPwd.getText().toString().trim();
            boolean isAgreeProtocol = mCbProtocol.isChecked();

            // 是否显示清除按钮
            if (username.length() > 0) {
                mIvRegisterUsernameDel.setVisibility(View.VISIBLE);
            } else {
                mIvRegisterUsernameDel.setVisibility(View.INVISIBLE);
            }
            if (pwd.length() > 0) {
                mIvRegisterPwdDel.setVisibility(View.VISIBLE);
            } else {
                mIvRegisterPwdDel.setVisibility(View.INVISIBLE);
            }

            // 登录按钮是否可用
            if (!TextUtils.isEmpty(pwd) && pwd.length() >= INPUT_TEXT_MIN_SIZE && !TextUtils.isEmpty(username) && isAgreeProtocol) {
                mBtnRegisterSubmit.setBackgroundResource(R.drawable.bg_register_submit);
                mBtnRegisterSubmit.setEnabled(true);
            } else {
                mBtnRegisterSubmit.setBackgroundResource(R.drawable.bg_register_submit_lock);
                mBtnRegisterSubmit.setEnabled(false);
            }
        }
    }

    private class CbCheckedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            LogUtils.d(TAG, "onCheckedChanged: isChecked = " + isChecked);
            mEtTextChangeWatcher.afterTextChanged(null);
        }
    }

    /************************************ inner class *****************************************/
    private class RegisterRunnable implements Runnable {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d(TAG, "run: ");
                    String accountName = mEtRegisterUsername.getText().toString().trim();
                    String accountPwd = mEtRegisterPwd.getText().toString().trim();
                    LogUtils.d(TAG, "run: register: accountName = " + accountName + ", accountPwd = " + accountPwd);
                    if (!TextUtils.isEmpty(accountName) && !TextUtils.isEmpty(accountPwd) && accountPwd.length() >= INPUT_TEXT_MIN_SIZE) {
                        mViewModel.registerClicked(accountName, accountPwd);
                    }
                }
            });

        }
    }

}
