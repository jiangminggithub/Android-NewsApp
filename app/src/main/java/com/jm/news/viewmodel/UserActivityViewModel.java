package com.jm.news.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserActivityViewModel extends AndroidViewModel {

    // static filed
    private static final String TAG = "UserActivityViewModel";
    private static final int BUFF_SIZE = 1024 * 8;
    private static final int NO_READ = -1;
    public static final String DEFAULT_SHOW_TEXT = "-";
    private static final String DIRECTORY_IMAGES = "HotNews";
    // function related filed
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEdit;
    private String[] mSexItems;
    private int mSexCheckedItemIndex = 0;
    private String[] mHobbyItems;
    private boolean[] mHobbyCheckedItemsFlag = {false, false, false, false, false, false};
    // livedate function
    private MutableLiveData<Boolean> mUserIconStatus = new MutableLiveData<>();


    public UserActivityViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "UserActivityViewModel: ");
        String preName = Common.getInstance().getResourcesString(R.string.app_user_detail_preferences_filename);
        if (!TextUtils.isEmpty(preName)) {
            mPreferences = Common.getInstance().getPreference(preName);
            mEdit = mPreferences.edit();
        }
    }

    @Override
    protected void onCleared() {
        LogUtils.d(TAG, "onCleared: ");
        mUserIconStatus = null;
        mPreferences = null;
        mEdit = null;
        super.onCleared();
    }

    /***************************** ******** livedata function *****************************************/
    public MutableLiveData<Boolean> getUserIconUpdateStatus() {
        return mUserIconStatus;
    }

    /************************************** public function *******************************************/
    public void initialized() {
        Common common = Common.getInstance();
        mSexItems = common.getResourcesStringArray(R.array.user_sex_items);
        mHobbyItems = common.getResourcesStringArray(R.array.user_hobby_items);
    }

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
        SharedPreferences preference = common.getPreference(resources.getString(R.string.app_account_preferences_filename));
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
        if (null == mSexItems) {
            initialized();
        }
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
        if (null == mHobbyItems) {
            initialized();
        }
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

    public String[] getSexItems() {
        if (null == mSexItems) {
            initialized();
        }
        return mSexItems;
    }

    public int getSexCheckedItemIndex() {
        return mSexCheckedItemIndex;
    }

    public String[] getHobbyItems() {
        if (null == mHobbyItems) {
            initialized();
        }
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

    public void setUserIconImage(final String sourceUrl) {
        LogUtils.d(TAG, "setUserIconImage: sourceUrl = " + sourceUrl);
        if (!TextUtils.isEmpty(sourceUrl)) {
            // 当外置存储设备可用时，文件设置到指定位置，否则默认文件位置
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String fileName = sourceUrl.substring(sourceUrl.lastIndexOf(File.separatorChar) + 1);
                File targetDirectory = new File(Environment.getExternalStorageDirectory().toString() + File.separatorChar + DIRECTORY_IMAGES);
                if (!targetDirectory.exists()) {
                    targetDirectory.mkdirs();
                }
                final File targetFile = new File(targetDirectory, fileName);
                LogUtils.d(TAG, "setUserIconImage: targetFile = " + targetFile);
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                new Thread() {
                    @Override
                    public void run() {
                        FileInputStream inputStream = null;
                        FileOutputStream outputStream = null;
                        try {
                            inputStream = new FileInputStream(sourceUrl);
                            outputStream = new FileOutputStream(targetFile);
                            byte buff[] = new byte[BUFF_SIZE];
                            int readLength = 0;
                            while ((readLength = inputStream.read(buff)) != NO_READ) {
                                outputStream.write(buff, 0, readLength);
                            }
                            putPreferenceString(UserActivityViewModel.UserInfo.USER_ICON, targetFile.toString());
                            mUserIconStatus.postValue(Boolean.TRUE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (null != inputStream) {
                                    inputStream.close();
                                }
                                if (null != outputStream) {
                                    outputStream.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                LogUtils.e(TAG, "run: failed = " + e.getMessage());
                                mUserIconStatus.postValue(Boolean.FALSE);
                            }
                        }
                    }
                }.start();
            } else {
                putPreferenceString(UserActivityViewModel.UserInfo.USER_ICON, sourceUrl);
                mUserIconStatus.postValue(Boolean.TRUE);
            }
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
