package com.jm.news.viewmodel;

import android.annotation.SuppressLint;
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

    private static final String TAG = "FragmentAppMenuViewModel";
    public static final int ACCOUNT_TYPE_NAME = 0;
    public static final int ACCOUNT_TYPE_AUTOGRAPH = 1;
    private static final String DEFAULT_SHOW_TEXT = Common.getInstance().getResourcesString(R.string.user_empty_content);

    public FragmentAppMenuViewModel(@NonNull Application application) {
        super(application);
    }

    @SuppressLint("LongLogTag")
    public String getAccountInfo(int type) {
        if (Common.getInstance().hasUser()) {
            if (type == ACCOUNT_TYPE_NAME) {
                LogUtils.d(TAG, "getAccountInfo: type = name");
                Common common = Common.getInstance();
                Resources resources = common.getResources();
                SharedPreferences preference = common.getPreference(resources.getString(R.string.app_account_prefences_filename));
                if (null != resources && null != preference) {
                    String accountName = preference.getString(resources.getString(R.string.pre_key_account_name), null);
                    LogUtils.d(TAG, "getAccountInfo: type = name , accountName = " + accountName);
                    if (!TextUtils.isEmpty(accountName)) {
                        return accountName;
                    }
                }
            } else if (type == ACCOUNT_TYPE_AUTOGRAPH) {
                LogUtils.d(TAG, "getAccountInfo: type = autograph");
                Common common = Common.getInstance();
                Resources resources = common.getResources();
                SharedPreferences preference = common.getPreference(resources.getString(R.string.app_user_detail_prefences_filename));
                if (null != resources && null != preference) {
                    String userID = Common.getInstance().getUser();
                    String userAutograph = preference.getString(userID + UserActivityViewModel.UserInfo.USER_AUTOGRAPH, DEFAULT_SHOW_TEXT);
                    LogUtils.d(TAG, "getAccountInfo: type = autograph , userAutograph = " + userAutograph);
                    return TextUtils.isEmpty(userAutograph) ? DEFAULT_SHOW_TEXT : userAutograph;
                }

            } else {

            }
        }
        return DEFAULT_SHOW_TEXT;
    }
}
