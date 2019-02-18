package com.jm.news.viewmodel;

import android.annotation.SuppressLint;
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

public class RegisterActivityViewModel extends AndroidViewModel {
    private static final String TAG = "RegisterActivityViewModel";
    public static final int REGISTER_STATUS_SUCCESS = 0;
    public static final int REGISTER_STATUS_FAILED = 1;

    private MutableLiveData<Integer> mRegisterStatus = new MutableLiveData<>();

    @SuppressLint("LongLogTag")
    public RegisterActivityViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "RegisterActivityViewModel: ");
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCleared() {
        LogUtils.d(TAG, "onCleared: ");
        mRegisterStatus = null;
        super.onCleared();
    }

    /*************************** livedate function ************************************/
    public MutableLiveData<Integer> getRegisterStatus() {
        return mRegisterStatus;
    }

    /*************************** operation function ***********************************/
    @SuppressLint("LongLogTag")
    public void registerClicked(String accountName, String accountPwd) {
        LogUtils.d(TAG, "registerClicked: accountName = " + accountName + ", accountPwd = " + accountPwd);
        if (!TextUtils.isEmpty(accountName) && !TextUtils.isEmpty(accountPwd)) {
            Common common = Common.getInstance();
            Resources resources = common.getResources();
            SharedPreferences preference = common.getPreference(resources.getString(R.string.app_account_prefences_filename));
            if (null != resources && null != preference) {
                SharedPreferences.Editor edit = preference.edit();
                edit.putString(resources.getString(R.string.pre_key_account_name), accountName);
                edit.putString(resources.getString(R.string.pre_key_account_pwd), DataManager.encode(accountPwd));
                boolean isRegister = edit.commit();
                if (isRegister) {
                    mRegisterStatus.postValue(REGISTER_STATUS_SUCCESS);
                } else {
                    mRegisterStatus.postValue(REGISTER_STATUS_FAILED);
                }
            }
        }
    }
}


