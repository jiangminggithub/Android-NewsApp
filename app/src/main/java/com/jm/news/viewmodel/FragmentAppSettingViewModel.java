package com.jm.news.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jm.news.R;
import com.jm.news.bean.AppVersionBean;
import com.jm.news.common.Common;
import com.jm.news.define.DataDef;
import com.jm.news.util.CacheManager;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class FragmentAppSettingViewModel extends AndroidViewModel {

    // static filed
    private static final String TAG = "FragmentAppSettingViewModel";
    private static final String DEFAULT_LOCALE_TEXT = "-";
    private static final String TEMP_FILE_SUFFIX = ".tmp";
    private static final int DEFAULT_LOCALE_INDEX = 0;
    private static final int DEFAULT_CLEAR_CACHE_STATE = -1;
    private static final int DEFAULT_TOTAL_CACHE_SIZE = 0;
    private static final int DOWNLOAD_BUFFER_SIZE = 1024 * 8;
    private static final int DOWNLOAD_TIMEOUT = 1000 * 30;
    private static final int DOWNLOAD_READ_TIMEOUT = 1000 * 15;
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
    public static final int DOWNLOAD_PROGRESS_NO = -1;
    public static final int DOWNLOAD_NO_READ = -1;
    public static final int DOWNLOAD_PROGRESS_VALUE = 101;
    public static final int DOWNLOAD_PROGRESS_FAILED = -100;
    public static final int DOWNLOAD_PROGRESS_SUCCESS = 100;
    public static final int CHECK_UPDATE_FAILED = -101;
    public static final int IS_LATEST = 1000;
    // function related filed
    private String[] mLocaleItems;
    private int mLocaleChoiceIndex = 0;
    private Context mContext;
    private Resources mResources;
    private SharedPreferences mSettingPreferences;
    private SharedPreferences.Editor mPreEditor;
    private boolean isDownloadCancel = false;
    private boolean isOperationLock = false;


    public FragmentAppSettingViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "FragmentAppSettingViewModel: ");
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

    public void setCancelDownload(boolean is) {
        this.isDownloadCancel = is;
    }

    public void checkUpdate(final Handler handler) {
        LogUtils.d(TAG, "checkUpdate: handler = " + handler);
        if (null == handler) {
            Message msg = handler.obtainMessage(CHECK_UPDATE_FAILED);
            handler.sendMessage(msg);
            LogUtils.d(TAG, "checkUpdate: handler is null!");
            return;
        }
        // 防抖动操作
        if (!isOperationLock) {
            isOperationLock = true;
            LogUtils.d(TAG, "checkUpdate: operationLock is Lock");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = null;
                    HttpURLConnection connection = null;
                    try {
                        final URL url = new URL(DataDef.AppInfo.APP_CHECK_UPDATE_LINK);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(DOWNLOAD_TIMEOUT);
                        connection.setReadTimeout(DOWNLOAD_READ_TIMEOUT);
                        connection.connect();

                        int responseCode = connection.getResponseCode();
                        LogUtils.d(TAG, "checkUpdate run: ResponseCode() = " + responseCode);
                        // 网络响应码检查
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            inputStream = connection.getInputStream();
                            StringBuffer sb = new StringBuffer();
                            int readLength = 0;
                            byte[] buff = new byte[DOWNLOAD_BUFFER_SIZE];
                            while ((readLength = inputStream.read(buff)) != DOWNLOAD_NO_READ) {
                                sb.append(new String(buff, 0, readLength, Charset.forName("UTF-8")));
                            }

                            String result = sb.toString();
                            LogUtils.d(TAG, "checkUpdate: result = " + result);
                            if (TextUtils.isEmpty(result)) {
                                Message msg = handler.obtainMessage(CHECK_UPDATE_FAILED);
                                handler.sendMessage(msg);
                                LogUtils.d(TAG, "checkUpdate run: latest info is null");
                                return;
                            } else {
                                Gson gson = new Gson();
                                AppVersionBean appVersionBean = gson.fromJson(result, AppVersionBean.class);
                                long latestVersionCode = appVersionBean.getApkInfo().getVersionCode();
                                long versionCode = CommonUtils.getInstance().getVersionCode();
                                LogUtils.d(TAG, "checkUpdate: latestVersionCode = " + latestVersionCode + ", versionCode = " + versionCode);
                                Message msg = handler.obtainMessage(IS_LATEST);
                                if (versionCode >= latestVersionCode) {
                                    msg.obj = true;
                                } else {
                                    msg.obj = false;
                                }
                                handler.sendMessage(msg);
                            }
                        } else {
                            Message msg = handler.obtainMessage(CHECK_UPDATE_FAILED);
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Message msg = handler.obtainMessage(CHECK_UPDATE_FAILED);
                        handler.sendMessage(msg);
                        LogUtils.d(TAG, "checkUpdate run: operation is error, message = " + e.getMessage());
                    } finally {
                        try {
                            if (null != inputStream) {
                                inputStream.close();
                            }
                            if (null != connection) {
                                connection.disconnect();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            isOperationLock = false;
                            LogUtils.d(TAG, "checkUpdate: operationLock is UnLock");
                        }
                    }
                }
            }).start();
        }
    }

    public void downloadApp(final Handler handler) {
        LogUtils.d(TAG, "downloadApp: handler = " + handler);
        if (!CommonUtils.getInstance().isNetworkAvailable() || null == handler) {
            Message msg = handler.obtainMessage(DOWNLOAD_PROGRESS_FAILED);
            handler.sendMessage(msg);
            LogUtils.d(TAG, "downloadApp: NetworkAvailable is disconnect or handler is null!");
            return;
        }
        // 防抖动操作
        if (!isOperationLock) {
            isOperationLock = true;
            LogUtils.d(TAG, "downloadApp: operationLock is Lock");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        URL url = new URL(DataDef.AppInfo.APP_DOWNLOAD_LINK);
                        String fileName = url.toString().substring(url.toString().lastIndexOf(File.separatorChar));
                        File downloadFile = null;
                        File tempFile = null;
                        LogUtils.d(TAG, "downloadApp run: ExternalStorageState() = " + Environment.getExternalStorageState());

                        // 检查存储设备的状态
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            // 文件合法性检查
                            if (TextUtils.isEmpty(fileName)) {
                                Message msg = handler.obtainMessage(DOWNLOAD_PROGRESS_FAILED);
                                handler.sendMessage(msg);
                                LogUtils.d(TAG, "downloadApp run: download fileName is null ");
                                return;
                            } else {
                                tempFile = new File(mContext.getExternalCacheDir(), fileName + TEMP_FILE_SUFFIX);
                                downloadFile = new File(mContext.getExternalCacheDir(), fileName);
                            }
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                            if (downloadFile.exists()) {
                                downloadFile.delete();
                            }
                            LogUtils.d(TAG, "downloadApp run: downloadFile = " + downloadFile);
                        } else {
                            Message msg = handler.obtainMessage(DOWNLOAD_PROGRESS_FAILED);
                            handler.sendMessage(msg);
                            LogUtils.d(TAG, "downloadApp run: StorageState is not mounted !");
                            return;
                        }

                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Accept-Encoding", "identity");
                        connection.setConnectTimeout(DOWNLOAD_TIMEOUT);
                        connection.setReadTimeout(DOWNLOAD_READ_TIMEOUT);
                        connection.connect();

                        int responseCode = connection.getResponseCode();
                        LogUtils.d(TAG, "downloadApp run: ResponseCode() = " + responseCode);
                        // 网络响应码检查
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            inputStream = connection.getInputStream();
                            outputStream = new FileOutputStream(tempFile);
                            int contentLength = connection.getContentLength();
                            LogUtils.d(TAG, "downloadApp run: contentLength = " + contentLength);
                            long downTotal = 0;
                            int readLength = 0;
                            int progress = 0;
                            Message msg;

                            byte[] buff = new byte[DOWNLOAD_BUFFER_SIZE];
                            while ((readLength = inputStream.read(buff)) != DOWNLOAD_NO_READ) {
                                if (isDownloadCancel) {
                                    LogUtils.d(TAG, "downloadApp run: --- operation is cancel! ---");
                                    return;
                                }
                                outputStream.write(buff, 0, readLength);
                                downTotal += readLength;
                                progress = (int) ((downTotal * 100) / contentLength);
                                if (contentLength != DOWNLOAD_PROGRESS_NO) {
                                    msg = handler.obtainMessage(DOWNLOAD_PROGRESS_VALUE);
                                    msg.obj = progress + "%";
                                    LogUtils.d(TAG, "downloadApp run: progress = " + progress);
                                } else {
                                    msg = handler.obtainMessage(DOWNLOAD_PROGRESS_NO);
                                    msg.obj = CacheManager.getFormatSizeString(downTotal);
                                    LogUtils.d(TAG, "downloadApp run: downTotal = " + downTotal);
                                }
                                handler.sendMessage(msg);
                            }

                            tempFile.renameTo(downloadFile);
                            msg = handler.obtainMessage(DOWNLOAD_PROGRESS_SUCCESS);
                            msg.obj = downloadFile.getAbsolutePath();
                            handler.sendMessage(msg);
                            inputStream.close();
                            outputStream.close();
                        } else {
                            Message msg = handler.obtainMessage(DOWNLOAD_PROGRESS_FAILED);
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        LogUtils.e(TAG, "downloadApp run: APP download failed : " + e.getMessage());
                        Message msg = handler.obtainMessage(DOWNLOAD_PROGRESS_FAILED);
                        handler.sendMessage(msg);
                        e.printStackTrace();
                    } finally {
                        try {
                            if (null != inputStream) {
                                inputStream.close();
                            }
                            if (null != outputStream) {
                                outputStream.close();
                            }
                            if (null != connection) {
                                connection.disconnect();
                            }
                        } catch (Exception e) {
                            LogUtils.e(TAG, "downloadApp run: close resource failed : " + e.getMessage());
                            e.printStackTrace();
                        } finally {
                            isOperationLock = false;
                            LogUtils.d(TAG, "downloadApp: operationLock is UnLock");
                        }
                    }
                }
            }).start();
        }
    }
}
