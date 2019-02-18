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

public class UserActivityViewModel extends AndroidViewModel {

    private static final String TAG = "UserActivityViewModel";
    private static final String DEFAULT_SHOW_TEXT = Common.getInstance().getResourcesString(R.string.user_empty_content);
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEdit;

    private String[] mSexItems;
    private int mSexCheckedItemIndex = 0;
    private String[] mHobbyItems;
    private boolean[] mHobbyCheckedItemsFlag = {false, false, false, false, false, false};


    public UserActivityViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "UserActivityViewModel: ");
        String preName = Common.getInstance().getResourcesString(R.string.app_user_detail_prefences_filename);
        mSexItems = Common.getInstance().getResourcesStringArray(R.array.user_sex_items);
        mHobbyItems = Common.getInstance().getResourcesStringArray(R.array.user_hobby_items);

        if (!TextUtils.isEmpty(preName)) {
            mPreferences = Common.getInstance().getPreference(preName);
            mEdit = mPreferences.edit();
        }
    }

    /**************************************public function****************************************************/
    public String getPreferenceString(int key) {
        LogUtils.d(TAG, "getPreferenceString: key = " + key);
        if (null != mPreferences && Common.getInstance().hasUser()) {
            String userID = Common.getInstance().getUser();
            String string = mPreferences.getString(userID + key, DEFAULT_SHOW_TEXT);
            return TextUtils.isEmpty(string) ? DEFAULT_SHOW_TEXT : string;
        }
        return DEFAULT_SHOW_TEXT;
    }

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
        return DEFAULT_SHOW_TEXT;
    }

    public String getSexPreferenceString(int key) {
        LogUtils.d(TAG, "getSexPreferenceString: key = " + key);
        if (null != mPreferences && Common.getInstance().hasUser()) {
            String userID = Common.getInstance().getUser();
            String string = mPreferences.getString(userID + key, DEFAULT_SHOW_TEXT);
            if (!DEFAULT_SHOW_TEXT.equals(string) && !"".equals(string)) {
                Integer index = Integer.valueOf(string);
                mSexCheckedItemIndex = index;
                if (index >= 0) {
                    return mSexItems[index];
                }
            }
        }
        return DEFAULT_SHOW_TEXT;
    }

    public String getHobbyPreferenceString(int key) {
        LogUtils.d(TAG, "getHobbyPreferenceString: key = " + key);
        if (null != mPreferences && Common.getInstance().hasUser()) {
            String userID = Common.getInstance().getUser();
            String string = mPreferences.getString(userID + key, DEFAULT_SHOW_TEXT);
            if (!TextUtils.isEmpty(string) && !DEFAULT_SHOW_TEXT.equals(string)) {
                String[] split = string.split("\\|");
                for (int i = 0; i < split.length; i++) {
                    mHobbyCheckedItemsFlag[i] = Boolean.valueOf(split[i]);
                }

                String result = "";
                for (int j = 0; j < mHobbyItems.length; j++) {
                    if (mHobbyCheckedItemsFlag[j]) {
                        result += mHobbyItems[j] + "、";
                    }
                }
                if (!TextUtils.isEmpty(result)) {
                    result = result.substring(0, result.length() - 1);
                    return result;
                }
            }
        }
        return DEFAULT_SHOW_TEXT;
    }

    public String[] getSexitems() {
        return mSexItems;
    }

    public int getSexCheckedItemIndex() {
        return mSexCheckedItemIndex;
    }

    public String[] getHobbyItems() {
        return mHobbyItems;
    }

    public boolean[] getHobbyCheckedItemsFlag() {
        return mHobbyCheckedItemsFlag;
    }


    /**************************************operation function*********************************************/
    public void setSexChoiceItemIndex(int index) {
        LogUtils.d(TAG, "setSexChoiceItemIndex: index = " + index);
        mSexCheckedItemIndex = index;

    }

    public void setHobbyChoiceItemsFlag(int which, boolean isChecked) {
        LogUtils.d(TAG, "setHobbyChoiceItemsFlag: which = " + which + ", isChecked = " + isChecked);
        if (which >= 0) {
            mHobbyCheckedItemsFlag[which] = isChecked;
        }
    }

    public void putPreferenceString(int key, String value) {
        LogUtils.d(TAG, "putPreferenceString: key = " + key + ", value = " + value);
        if (null != mEdit && null != value) {
            String userID = Common.getInstance().getUser();
            mEdit.putString(userID + key, value);
            mEdit.apply();
        }
    }

    public void putPreferenceString(int key) {
        LogUtils.d(TAG, "putPreferenceString: key = " + key);
        if (null != mEdit) {
            String userID = Common.getInstance().getUser();
            if (key == UserInfo.USER_SEX) {
                mEdit.putString(String.valueOf(userID + key), String.valueOf(mSexCheckedItemIndex));
            } else if (key == UserInfo.USER_HOBBY) {
                String result = "";
                for (int i = 0; i < mHobbyCheckedItemsFlag.length; i++) {
                    result += mHobbyCheckedItemsFlag[i] + "|";
                }
                if (!TextUtils.isEmpty(result)) {
                    result = result.substring(0, result.length() - 1);
                }
                mEdit.putString(userID + key, result);
            } else {
                // nothing to do
            }
            mEdit.apply();
        }
    }


    /**************************************inner class****************************************************/
    public class UserInfo {
        public static final int USER_HEADER = 0;
        public static final int USER_ICON = 1;
        public static final int USER_NAME = 2;
        public static final int USER_AUTOGRAPH = 3;
        public static final int USER_NICKNAME = 4;
        public static final int USER_SEX = 5;
        public static final int USER_ADDRESS = 6;
        public static final int USER_PHONE = 7;
        public static final int USER_HOBBY = 8;
        public static final int USER_PROFILE = 9;
        public static final int USER_MORE = 10;
        public static final int USER_ACCOUNT = 11;
    }

}
