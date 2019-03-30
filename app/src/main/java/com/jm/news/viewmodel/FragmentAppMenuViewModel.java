package com.jm.news.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.util.LogUtils;

public class FragmentAppMenuViewModel extends AndroidViewModel {

    // static field
    private static final String TAG = "FragmentAppMenuViewModel";
    public static final int ACCOUNT_TYPE_NAME = 0;
    public static final int ACCOUNT_TYPE_AUTOGRAPH = 1;
    public static final int ACCOUNT_TYPE_ICON = 2;
    private static final String DEFAULT_SHOW_TEXT = "-";
    private Common mCommon = Common.getInstance();
    private Resources mResources = mCommon.getResources();

    public FragmentAppMenuViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        LogUtils.d(TAG, "onCleared: ");
        mCommon = null;
        mResources = null;
        super.onCleared();
    }

    /***************************** public function   *****************************************/
    public String getAccountInfo(int type) {
        if (Common.getInstance().hasUser()) {
            if (type == ACCOUNT_TYPE_NAME) {
                LogUtils.d(TAG, "getAccountInfo: type = name");
                SharedPreferences preference = mCommon.getPreference(mResources.getString(R.string.app_account_preferences_filename));
                if (null != mResources && null != preference) {
                    String accountName = preference.getString(mResources.getString(R.string.pre_key_account_name), null);
                    LogUtils.d(TAG, "getAccountInfo: type = name , accountName = " + accountName);
                    if (!TextUtils.isEmpty(accountName)) {
                        return accountName;
                    }
                }
            } else if (type == ACCOUNT_TYPE_AUTOGRAPH) {
                LogUtils.d(TAG, "getAccountInfo: type = autograph");
                SharedPreferences preference = mCommon.getPreference(mResources.getString(R.string.app_user_detail_preferences_filename));
                if (null != mResources && null != preference) {
                    String userID = mCommon.getUser();
                    String userAutograph = preference.getString(userID + UserActivityViewModel.UserInfo.USER_AUTOGRAPH, DEFAULT_SHOW_TEXT);
                    LogUtils.d(TAG, "getAccountInfo: type = autograph , userAutograph = " + userAutograph);
                    return TextUtils.isEmpty(userAutograph) ? DEFAULT_SHOW_TEXT : userAutograph;
                }
            } else if (type == ACCOUNT_TYPE_ICON) {
                SharedPreferences preference = mCommon.getPreference(mCommon.getResourcesString(R.string.app_user_detail_preferences_filename));
                String userID = mCommon.getUser();
                String string = preference.getString(userID + UserActivityViewModel.UserInfo.USER_ICON, DEFAULT_SHOW_TEXT);
                return TextUtils.isEmpty(string) ? DEFAULT_SHOW_TEXT : string;
            } else {
                // noting to do
            }
        }
        return DEFAULT_SHOW_TEXT;
    }
}
