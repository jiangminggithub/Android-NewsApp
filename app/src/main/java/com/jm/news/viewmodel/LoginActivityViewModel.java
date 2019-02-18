package com.jm.news.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.util.DataManager;
import com.jm.news.util.LogUtils;

public class LoginActivityViewModel extends AndroidViewModel {
    private static final String TAG = "LoginActivityViewModel";
    public static final int LOGIN_STATUS_NO_USER = 0;
    public static final int LOGIN_STATUS_SUCCESS = 1;
    public static final int LOGIN_STATUS_FAILED = 2;

    private MutableLiveData<Integer> mLoginStatus = new MutableLiveData<>();

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "LoginActivityViewModel: ");
    }

    @Override
    protected void onCleared() {
        LogUtils.d(TAG, "onCleared: ");
        mLoginStatus = null;
        super.onCleared();
    }

    /*************************** livedate function ************************************/
    public MutableLiveData<Integer> getLoginStatus() {
        return mLoginStatus;
    }

    /*************************** public function ***********************************/
    public String getAccountName() {
        Common common = Common.getInstance();
        Resources resources = common.getResources();
        SharedPreferences preference = common.getPreference(resources.getString(R.string.app_account_prefences_filename));
        if (null != resources && null != preference) {
            String accountName = preference.getString(resources.getString(R.string.pre_key_account_name), null);
            if (!TextUtils.isEmpty(accountName)) {
                return accountName;
            }
        }
        return null;
    }

    /*************************** operation function ***********************************/
    public void loginClicked(String username, String pwd, boolean isAutoLogin) {
        LogUtils.d(TAG, "loginClicked: username = " + username + ", pwd = " + pwd + ", isAutoLogin = " + isAutoLogin);
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            Common common = Common.getInstance();
            Resources resources = common.getResources();
            SharedPreferences preference = common.getPreference(resources.getString(R.string.app_account_prefences_filename));
            if (null != resources && null != preference) {
                String accountName = preference.getString(resources.getString(R.string.pre_key_account_name), null);
                String accountPwd = preference.getString(resources.getString(R.string.pre_key_account_pwd), null);
                if (TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountPwd)) {
                    mLoginStatus.postValue(LOGIN_STATUS_NO_USER);
                } else {
                    if (username.equals(accountName) && DataManager.encode(pwd).equals(accountPwd)) {
                        preference.edit().putBoolean(resources.getString(R.string.pre_key_account_auto_login), isAutoLogin).apply();
                        common.setUser(username);
                        mLoginStatus.postValue(LOGIN_STATUS_SUCCESS);
                    } else {
                        mLoginStatus.postValue(LOGIN_STATUS_FAILED);
                    }
                }
            }
        }
    }

}
