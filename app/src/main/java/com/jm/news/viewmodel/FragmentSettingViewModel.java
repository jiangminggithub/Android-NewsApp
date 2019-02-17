package com.jm.news.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.util.CacheManager;

public class FragmentSettingViewModel extends AndroidViewModel {
    private static final String TAG = "FragmentSettingViewModel";
    private static final int DEFAULT_LOCALE_INDEX = 0;
    public static final int KEY_CLEAR_CACHE = 0;
    public static final int KEY_OUT_CLEAR_CACHE = 1;
    public static final int KEY_OPEN_NOTIFY = 2;
    public static final int KEY_AUTO_RUN = 3;
    public static final int KEY_LOCALE = 4;
    public static final int KEY_HIDE_BENEFITS = 5;
    public static final int KEY_CHECK_UPDATE = 6;
    public static final int KEY_VERSION = 7;

    private String[] mLocaleItems;
    private int mLocaleChoiceIndex = 0;
    private Context mContext;
    private Resources mResources;
    private SharedPreferences mSettingPrefences;
    private SharedPreferences.Editor mPreEditor;

    @SuppressLint("LongLogTag")
    public FragmentSettingViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "FragmentSettingViewModel: ");
        mContext = application.getApplicationContext();
        mResources = Common.getInstance().getResources();
        String preSettingName = mResources.getString(R.string.app_setting_prefences);
        if (!TextUtils.isEmpty(preSettingName)) {
            mSettingPrefences = application.getSharedPreferences(preSettingName, Context.MODE_PRIVATE);
            mPreEditor = mSettingPrefences.edit();
        }
        mLocaleItems = Common.getInstance().getResourcesStringArray(R.array.setting_locale);
        mLocaleChoiceIndex = getLocaleIndex();

    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
        mContext = null;
        mResources = null;
        mSettingPrefences = null;
        mPreEditor = null;
        super.onCleared();
    }

    /********************************** public function *************************************/
    @SuppressLint("LongLogTag")
    public String getPreferenceKey(int type) {
        Log.d(TAG, "getPreferenceKey: type = " + type);
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
                case KEY_CHECK_UPDATE:
                    key = mResources.getString(R.string.pre_key_setting_auto_check_update);
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

    @SuppressLint("LongLogTag")
    public double getTotalCacheSize() {
        double totalCacheSize = 0.0;
        if (null != mContext) {
            try {
                totalCacheSize = CacheManager.getTotalCacheSize(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getTotalCacheSize: operation is failed!");
            }
        }
        return totalCacheSize;
    }

    @SuppressLint("LongLogTag")
    public String getTotalCacheFormatString() {
        if (null != mContext) {
            try {
                String totalCacheSizeString = CacheManager.getTotalCacheSizeString(mContext);
                return totalCacheSizeString;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getTotalCacheFormatString: operation is failed!");
            }
        }

        return null;
    }

    public String getFerenceString(String key, String value) {
        if (null != mSettingPrefences && !TextUtils.isEmpty(key)) {
            String string = mSettingPrefences.getString(key, value);
            return string;
        }
        return null;
    }

    public boolean getBoolean(String key, boolean value) {
        if (null != mSettingPrefences && !TextUtils.isEmpty(key)) {
            boolean isChecked = mSettingPrefences.getBoolean(key, value);
            return isChecked;
        }
        return false;
    }

    public String[] getLocaleItems() {
        return mLocaleItems;
    }

    public int getLocaleIndex() {
        String key = mResources.getString(R.string.pre_key_setting_local);
        if (null != mSettingPrefences && !TextUtils.isEmpty(key)) {
            mLocaleChoiceIndex = mSettingPrefences.getInt(key, DEFAULT_LOCALE_INDEX);
        }
        return mLocaleChoiceIndex;
    }

    public String getLocaleString() {
        if (null != mLocaleItems) {
            int localeIndex = getLocaleIndex();
            return mLocaleItems[localeIndex];
        }
        return "-";
    }

    public void setLocaleIndex(int which) {
        mLocaleChoiceIndex = which;
    }


    /********************************** operation function *************************************/

    public void putBoolean(String key, boolean value) {
        if (null != mPreEditor && !TextUtils.isEmpty(key)) {
            mPreEditor.putBoolean(key, value);
            mPreEditor.apply();
        }
    }

    public boolean putLocalSelected(String key) {
        if (null != mPreEditor && !TextUtils.isEmpty(key)) {
            mPreEditor.putInt(key, mLocaleChoiceIndex);
            return mPreEditor.commit();
        }
        return false;
    }

    public int clearAllCache() {
        if (null != mContext) {
            return CacheManager.clearAllCache(mContext);
        }
        return -1;
    }
}
