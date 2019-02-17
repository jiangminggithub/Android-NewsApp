package com.jm.news.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.jm.news.R;

import java.util.HashMap;
import java.util.Locale;

public class Common {
    private static final String TAG = "Common";
    public static final int LOCALE_TYPE_DEFAULT = 0;
    public static final int LOCALE_TYPE_SIMPLIFIED_CHINESE = 1;
    public static final int LOCALE_TYPE_ENGLISH = 2;
    private static final int DEFAULT_LOCALE_INDEX = 0;

    private static Common mInstance = null;
    private Context mContext = null;
    private Resources mResources = null;
    private HashMap<Integer, String> mChannelIDMap = null;
    private String mUser = null;
    private Locale mLocale = null;
    private boolean exitClearCache = false;

    public static final synchronized Common getInstance() {
        if (null == mInstance) {
            mInstance = new Common();
            mInstance.mChannelIDMap = new HashMap<>();
        }
        return mInstance;
    }

    private Common() {
    }

    public void initialize(Context context) {
        this.mContext = context;
        mResources = context.getResources();
        initializedAppInfo();
    }

    /****************************** operation function *****************************/
    private void initializedAppInfo() {
        SharedPreferences settingPreference = getPreference(mResources.getString(R.string.app_setting_prefences));
        SharedPreferences accountPreference = getPreference(mResources.getString(R.string.app_account_prefences));
        if (null != mResources) {
            // locale initialized
            String localeKey = mResources.getString(R.string.pre_key_setting_local);
            String exitClearCachekey = mResources.getString(R.string.pre_key_setting_app_out_clear);
            if (null != settingPreference && !TextUtils.isEmpty(localeKey)) {
                int localeIndex = settingPreference.getInt(localeKey, DEFAULT_LOCALE_INDEX);
                if (localeIndex != Common.LOCALE_TYPE_DEFAULT) {
                    setAppLocale(localeIndex);
                }
                boolean isClearCache = settingPreference.getBoolean(exitClearCachekey, false);
                if (isClearCache) {
                    exitClearCache = isClearCache;
                }
            }

            // account initialized
            if (null != accountPreference) {
                boolean isAutoLogin = accountPreference.getBoolean(mResources.getString(R.string.app_account_auto_login), false);
                String accountName = accountPreference.getString(mResources.getString(R.string.app_account_name), null);
                if (isAutoLogin && !TextUtils.isEmpty(accountName)) {
                    setUser(accountName);
                }
            }
        }
    }

    public void setAppLocale(int localType) {
        if (null != mContext) {
            Resources resources = mContext.getResources();
            Configuration config = resources.getConfiguration();
            DisplayMetrics dm = resources.getDisplayMetrics();

            if (localType == LOCALE_TYPE_SIMPLIFIED_CHINESE) {
                mLocale = Locale.SIMPLIFIED_CHINESE;
            } else if (localType == LOCALE_TYPE_ENGLISH) {
                mLocale = Locale.ENGLISH;
            } else {
                mLocale = Locale.getDefault();
                Log.d(TAG, "setAppLocale: default------------------------------------- = " + mLocale);
            }

            Log.d(TAG, "setAppLocale: Build.Version = " + Build.VERSION.SDK_INT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(mLocale);
                config.setLocales(new LocaleList(mLocale));
                mContext.createConfigurationContext(config);
                Log.d(TAG, "setAppLocale: Locale = " + mLocale);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(mLocale);
            } else {
                config.locale = mLocale;
            }
            resources.updateConfiguration(config, dm);
            Log.d(TAG, "setAppLocale: " + mLocale);
        }
    }

    public Locale getLocale() {
        Log.d(TAG, "getLocale: " + mLocale);
        if (null != mLocale) {
            return mLocale;
        }
        return Locale.getDefault();
    }

    public boolean logoutUser() {
        if (null != mResources) {
            SharedPreferences preference = getPreference(mResources.getString(R.string.app_account_prefences));
            if (null != preference) {
                SharedPreferences.Editor edit = preference.edit();
                boolean isCommit = edit.putBoolean(mResources.getString(R.string.app_account_auto_login), false).commit();
                mUser = null;
                return isCommit;
            }
        }
        return false;
    }

    public void changeAccount() {
        if (null != mResources) {
            SharedPreferences accountPreference = getPreference(mResources.getString(R.string.app_account_prefences));
            SharedPreferences userDetailPreference = getPreference(mResources.getString(R.string.app_user_detail_prefences));
            if (null != accountPreference) {
                accountPreference.edit().clear().commit();
            }
            if (null != userDetailPreference) {
                userDetailPreference.edit().clear().commit();
            }
            mUser = null;
        }
    }

    public Context attachBaseContext(Context context) {
        if (null != mLocale && null != context && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Configuration config = context.getResources().getConfiguration();
            config.setLocale(mLocale);
            config.setLocales(new LocaleList(mLocale));
            Context newContext = mContext.createConfigurationContext(config);
            Log.d(TAG, "attachBaseContext: newContext = " + newContext);
            return newContext;
        }
        return null;
    }

    /***********************************getter and setter *********************************/
    public String getUser() {
        return mUser;
    }

    public void setUser(String mUser) {
        this.mUser = mUser;
    }

    public boolean hasUser() {
        if (TextUtils.isEmpty(mUser)) {
            return false;
        }

        return true;
    }

    public void addChannelID(int key, @NonNull String channelID) {
        Log.d(TAG, "addChannelID: key=" + key + ", channelID=" + channelID);

        if (null != channelID && !"".equals(channelID)) {
            mChannelIDMap.put(key, channelID);
        }
    }

    public String getChannelID(int key) {
        Log.d(TAG, "getChannelID: key=" + key);
        return mChannelIDMap.get(key);
    }

    public Context getContext() {
        return mContext;
    }

    public Resources getResources() {
        return mResources;
    }

    public String getResourcesString(int resId) {
        if (null != mResources) {
            return mResources.getString(resId);
        }
        return "";
    }

    public String[] getResourcesStringArray(int resId) {
        if (null != mResources) {
            return mResources.getStringArray(resId);
        }
        return null;
    }

    public SharedPreferences getPreference(String preNeme) {
        if (null != mContext) {
            return mContext.getSharedPreferences(preNeme, Context.MODE_PRIVATE);
        }
        return null;
    }

    public boolean isExitClearCache() {
        return exitClearCache;
    }
}
