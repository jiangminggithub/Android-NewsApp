package com.jm.news.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jm.news.R;
import com.jm.news.common.Common;

public class UserActivityViewModel extends AndroidViewModel {

    private static final String TAG = "UserActivityViewModel";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor edit;
    private Context mContext;

    // Radio box options
    private String[] mSexItems;
    // Initial options for radio boxes
    private int mSexCheckedItemIndex = 0;
    // Checkbox options
    private String[] mHobbyItems;
    // Check box initial selection
    private boolean[] mHobbyCheckedItemsFlag = {false, false, false, false, false, false};
    // Default show text
    private String defaultShowText;


    public UserActivityViewModel(@NonNull Application application) {
        super(application);
        String preName = Common.getInstance().getResourcesString(R.string.app_user_detail_prefences);
        defaultShowText = Common.getInstance().getResourcesString(R.string.user_empty_content);
        mSexItems = Common.getInstance().getResourcesStringArray(R.array.user_sex_items);
        mHobbyItems = Common.getInstance().getResourcesStringArray(R.array.user_hobby_items);

        if (!TextUtils.isEmpty(preName)) {
            mPreferences = Common.getInstance().getPreference(preName);
            edit = mPreferences.edit();
        }
    }

    /**************************************public function****************************************************/
    public String getPreferenceString(int key) {
        if (null != mPreferences && Common.getInstance().hasUser()) {
            String userID = Common.getInstance().getUser();
            String string = mPreferences.getString(userID + key, defaultShowText);
            return TextUtils.isEmpty(string) ? defaultShowText : string;
        }
        return defaultShowText;
    }

    public String getAccountName() {
        Common common = Common.getInstance();
        Resources resources = common.getResources();
        SharedPreferences preference = common.getPreference(resources.getString(R.string.app_account_prefences));
        if (null != resources && null != preference) {
            String accountName = preference.getString(resources.getString(R.string.app_account_name), null);
            if (!TextUtils.isEmpty(accountName)) {
                return accountName;
            }
        }
        return defaultShowText;
    }

    public String getSexPreferenceString(int key) {
        if (null != mPreferences && Common.getInstance().hasUser()) {
            String userID = Common.getInstance().getUser();
            String string = mPreferences.getString(userID + key, defaultShowText);
            if (!defaultShowText.equals(string) && !"".equals(string)) {
                Integer index = Integer.valueOf(string);
                mSexCheckedItemIndex = index;
                if (index >= 0) {
                    return mSexItems[index];
                }

            }
        }
        return defaultShowText;
    }

    public String getHobbyPreferenceString(int key) {
        if (null != mPreferences && Common.getInstance().hasUser()) {
            String userID = Common.getInstance().getUser();
            String string = mPreferences.getString(userID + key, defaultShowText);
            if (!TextUtils.isEmpty(string) && !defaultShowText.equals(string)) {
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
        return defaultShowText;
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
        mSexCheckedItemIndex = index;

    }

    public void setHobbyChoiceItemsFlag(int which, boolean isChecked) {
        if (which >= 0) {
            mHobbyCheckedItemsFlag[which] = isChecked;
        }
    }

    public void putPreferenceString(int key, String value) {
        String userID = Common.getInstance().getUser();
        if (null != edit && null != value) {
            edit.putString(userID + key, value);
            edit.apply();
        }
    }

    public void putPreferenceString(int key) {
        String userID = Common.getInstance().getUser();
        if (null != edit) {
            if (key == UserInfo.USER_SEX) {
                edit.putString(String.valueOf(userID + key), String.valueOf(mSexCheckedItemIndex));
            } else if (key == UserInfo.USER_HOBBY) {
                String result = "";
                for (int i = 0; i < mHobbyCheckedItemsFlag.length; i++) {
                    result += mHobbyCheckedItemsFlag[i] + "|";
                }
                if (!TextUtils.isEmpty(result)) {
                    result = result.substring(0, result.length() - 1);
                }
                edit.putString(userID + key, result);
            } else {
                // nothing to do
            }
            edit.apply();
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
