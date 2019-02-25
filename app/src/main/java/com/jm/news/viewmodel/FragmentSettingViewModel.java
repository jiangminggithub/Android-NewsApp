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
import com.jm.news.util.CacheManager;
import com.jm.news.util.LogUtils;

public class FragmentSettingViewModel extends AndroidViewModel {

    // static filed
    private static final String TAG = "FragmentSettingViewModel";
    private static final String DEFAULT_LOCALE_TEXT = "-";
    private static final int DEFAULT_LOCALE_INDEX = 0;
    private static final int DEFAULT_CLEAR_CACHE_STATE = -1;
    private static final int DEFAULT_TOTAL_CACHE_SIZE = 0;
    public static final int KEY_CLEAR_CACHE = 0;
    public static final int KEY_OUT_CLEAR_CACHE = 1;
    public static final int KEY_OPEN_NOTIFY = 2;
    public static final int KEY_AUTO_RUN = 3;
    public static final int KEY_LOCALE = 4;
    public static final int KEY_HIDE_BENEFITS = 5;
    public static final int KEY_AUTO_CHECK_UPDATE = 6;
    public static final int KEY_APP_WALL = 7;
    public static final int KEY_REPUTATION = 8;
    public static final int KEY_SHARE = 9;
    public static final int KEY_FEEDBACK = 10;
    public static final int KEY_VERSION = 11;
    // function related filed
    private String[] mLocaleItems;
    private int mLocaleChoiceIndex = 0;
    private Context mContext;
    private Resources mResources;
    private SharedPreferences mSettingPreferences;
    private SharedPreferences.Editor mPreEditor;


    public FragmentSettingViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "FragmentSettingViewModel: ");
        mContext = application.getApplicationContext();
        mResources = Common.getInstance().getResources();
        String preSettingName = mResources.getString(R.string.app_setting_preferences_filename);
        if (!TextUtils.isEmpty(preSettingName)) {
            mSettingPreferences = application.getSharedPreferences(preSettingName, Context.MODE_PRIVATE);
            mPreEditor = mSettingPreferences.edit();
        }
    }


    @Override
    protected void onCleared() {
        LogUtils.d(TAG, "onCleared: ");
        mLocaleItems = null;
        mContext = null;
        mResources = null;
        mSettingPreferences = null;
        mPreEditor = null;
        super.onCleared();
    }

    /********************************** public function *************************************/
    public void initialized() {
        mLocaleItems = Common.getInstance().getResourcesStringArray(R.array.setting_locale);
        mLocaleChoiceIndex = getLocaleIndex();
    }

    public String getPreferenceKey(int type) {
        LogUtils.d(TAG, "getPreferenceKey: type = " + type);
        String key = null;
        if (null != mResources) {
            switch (type) {
                case KEY_CLEAR_CACHE:
                    key = mResources.getString(R.string.pre_key_setting_clear_cache);
                    break;
                case KEY_OUT_CLEAR_CACHE:
                    key = mResources.getString(R.string.pre_key_setting_app_out_clear);
                    break;
                case KEY_OPEN_NOTIFY:
                    key = mResources.getString(R.string.pre_key_setting_open_notify);
                    break;
                case KEY_AUTO_RUN:
                    key = mResources.getString(R.string.pre_key_setting_auto_run);
                    break;
                case KEY_LOCALE:
                    key = mResources.getString(R.string.pre_key_setting_local);
                    break;
                case KEY_HIDE_BENEFITS:
                    key = mResources.getString(R.string.pre_key_setting_hide_benefits);
                    break;
                case KEY_AUTO_CHECK_UPDATE:
                    key = mResources.getString(R.string.pre_key_setting_auto_check_update);
                    break;
                case KEY_APP_WALL:
                    key = mResources.getString(R.string.pre_key_setting_app_wall);
                    break;
                case KEY_REPUTATION:
                    key = mResources.getString(R.string.pre_key_setting_reputation);
                    break;
                case KEY_SHARE:
                    key = mResources.getString(R.string.pre_key_setting_share);
                    break;
                case KEY_FEEDBACK:
                    key = mResources.getString(R.string.pre_key_setting_feedback);
                    break;
                case KEY_VERSION:
                    key = mResources.getString(R.string.pre_key_setting_version);
                    break;
                default:
                    break;
            }
        }
        return key;
    }

    public double getTotalCacheSize() {
        double totalCacheSize = DEFAULT_TOTAL_CACHE_SIZE;
        if (null != mContext) {
            try {
                totalCacheSize = CacheManager.getTotalCacheSize(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(TAG, "getTotalCacheSize: operation is failed!");
            }
        }
        return totalCacheSize;
    }

    public String getTotalCacheFormatString() {
        if (null != mContext) {
            try {
                String totalCacheSizeString = CacheManager.getTotalCacheSizeString(mContext);
                return totalCacheSizeString;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(TAG, "getTotalCacheFormatString: operation is failed!");
            }
        }

        return null;
    }

    public boolean getBoolean(String key, boolean value) {
        if (null != mSettingPreferences && !TextUtils.isEmpty(key)) {
            boolean isChecked = mSettingPreferences.getBoolean(key, value);
            return isChecked;
        }
        return false;
    }

    public String[] getLocaleItems() {
        if (null == mLocaleItems) {
            initialized();
        }
        return mLocaleItems;
    }

    public int getLocaleIndex() {
        String key = mResources.getString(R.string.pre_key_setting_local);
        if (null != mSettingPreferences && !TextUtils.isEmpty(key)) {
            mLocaleChoiceIndex = mSettingPreferences.getInt(key, DEFAULT_LOCALE_INDEX);
        }
        return mLocaleChoiceIndex;
    }

    public String getLocaleString() {
        if (null == mLocaleItems) {
            initialized();
        }
        if (null != mLocaleItems) {
            int localeIndex = getLocaleIndex();
            return mLocaleItems[localeIndex];
        }
        return DEFAULT_LOCALE_TEXT;
    }

    public void setLocaleIndex(int which) {
        mLocaleChoiceIndex = which;
    }

    /********************************** operation function *************************************/
    public void putBoolean(String key, boolean value) {
        LogUtils.d(TAG, "putBoolean: key = " + key + ", value = " + value);
        if (null != mPreEditor && !TextUtils.isEmpty(key)) {
            mPreEditor.putBoolean(key, value);
            mPreEditor.apply();
        }
    }

    public boolean putLocalSelected(String key) {
        LogUtils.d(TAG, "putLocalSelected: key = " + key);
        if (null != mPreEditor && !TextUtils.isEmpty(key)) {
            mPreEditor.putInt(key, mLocaleChoiceIndex);
            return mPreEditor.commit();
        }
        return false;
    }

    public int clearAllCache() {
        LogUtils.d(TAG, "clearAllCache: ");
        if (null != mContext) {
            return CacheManager.clearAllCache(mContext);
        }
        return DEFAULT_CLEAR_CACHE_STATE;
    }

}
