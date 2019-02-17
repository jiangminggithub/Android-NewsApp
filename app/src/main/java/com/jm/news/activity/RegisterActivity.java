package com.jm.news.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.define.BaseActivity;
import com.jm.news.util.CommonUtils;
import com.jm.news.viewmodel.RegisterActivityViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    private static final boolean BUTTON_NORMAL = true;
    private static final boolean BUTTON_LOCKED = false;
    private static final int INPUT_TEXT_MIN_SIZE = 6;

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

    private Handler mHandler;
    private EtTextChangeWatcher mEtTextChangeWatcher;
    private CbCheckedListener mCbCheckedListener;
    private MyBtnOnTouchListener mBtnOnTouchListener;


    private RegisterActivityViewModel mViewModel;
    private RegisterObserve mRegisterObserve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setContentView(R.layout.activity_register);
        } else {
            setContentView(R.layout.layout_register_compat);
        }
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
        mBtnOnTouchListener = new MyBtnOnTouchListener();

        mTvNavigationTitle.setText(R.string.login_register);
        mIbNavigationBack.setOnClickListener(this);
        mIvRegisterUsernameDel.setOnClickListener(this);
        mIvRegisterPwdDel.setOnClickListener(this);
        mBtnRegisterSubmit.setOnClickListener(this);
        mBtnRegisterSubmit.setOnTouchListener(mBtnOnTouchListener);
        mCbProtocol.setOnCheckedChangeListener(mCbCheckedListener);
        mEtRegisterUsername.addTextChangedListener(mEtTextChangeWatcher);
        mEtRegisterPwd.addTextChangedListener(mEtTextChangeWatcher);

        mViewModel.getRegisterStatus().observe(this, mRegisterObserve);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEtTextChangeWatcher.afterTextChanged(null);
    }

    @Override
    protected void onDestroy() {
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
            case R.id.bt_register_submit:
                mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mDialog.setContentText(Common.getInstance().getResourcesString(R.string.account_registering));
                mDialog.setCancelable(false);
                mDialog.show();
                mHandler.postDelayed(new RegisterRunable(), 1000);
                break;
            case R.id.iv_register_username_del:
                mEtRegisterUsername.setText(null);
                break;
            case R.id.iv_register_pwd_del:
                mEtRegisterPwd.setText(null);
                break;
            default:
                break;
        }
    }

    /************************************ observe function ***********************************/

    private class RegisterObserve implements Observer<Integer> {

        @Override
        public void onChanged(@Nullable Integer integer) {
            Log.d(TAG, "onChanged: change = " + integer);
            if (null != integer) {
                switch (integer) {
                    case RegisterActivityViewModel.REGISTER_STATUS_SUCCESS:
                        CommonUtils.getInstance().showToastView(R.string.account_register_success);
                        finish();
                        break;
                    case RegisterActivityViewModel.REGISTER_STATUS_FAILED:
                        CommonUtils.getInstance().showToastView(R.string.account_register_failed);
                        break;
                    default:
                        break;
                }
                mDialog.dismiss();
            }
        }
    }



    /************************************ private function ************************************/
    private void setBtnRegisterSubmitStatus(boolean status) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (status == BUTTON_NORMAL) {
                mBtnRegisterSubmit.setBackgroundResource(R.drawable.bg_register_submit);
            } else {
                mBtnRegisterSubmit.setBackgroundResource(R.drawable.bg_register_submit_lock);
            }
        } else {
            if (status == BUTTON_NORMAL) {
                mBtnRegisterSubmit.setBackgroundColor(getResources().getColor(R.color.account_register_submit));
            } else {
                mBtnRegisterSubmit.setBackgroundColor(getResources().getColor(R.color.account_register_submit_lock));
            }
        }
    }



    /************************************ inner class *****************************************/
    private class RegisterRunable implements Runnable {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String accountName = mEtRegisterUsername.getText().toString().trim();
                    String accountPwd = mEtRegisterPwd.getText().toString().trim();
                    boolean isAutoLogin = mCbProtocol.isChecked();
                    if (!TextUtils.isEmpty(accountName) && !TextUtils.isEmpty(accountPwd) && accountPwd.length() >= 6) {
                        mViewModel.registerClicked(accountName, accountPwd);
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
            if (!TextUtils.isEmpty(pwd) && pwd.length() >= 6 && !TextUtils.isEmpty(username) && isAgreeProtocol) {
                setBtnRegisterSubmitStatus(BUTTON_NORMAL);
                mBtnRegisterSubmit.setEnabled(true);
            } else {
                setBtnRegisterSubmitStatus(BUTTON_LOCKED);
                mBtnRegisterSubmit.setEnabled(false);
            }
        }
    }

    private class CbCheckedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(TAG, "onCheckedChanged: isChecked = " + isChecked);
            mEtTextChangeWatcher.afterTextChanged(null);
        }
    }

    private class MyBtnOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int id = v.getId();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (id == R.id.bt_register_submit) {
                    setBtnRegisterSubmitStatus(BUTTON_LOCKED);
                }
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (id == R.id.bt_register_submit) {
                    setBtnRegisterSubmitStatus(BUTTON_NORMAL);
                }
            }
            return false;
        }
    }

}
