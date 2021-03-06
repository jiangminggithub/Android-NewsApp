package com.jm.news.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.define.DataDef;
import com.jm.news.util.CacheManager;
import com.jm.news.util.LogUtils;

public class MainActivityViewModel extends AndroidViewModel {

    // static filed
    private static final String TAG = "MainActivityViewModel";
    // function related filed
    private Context mContext = null;
    private String[] mChannelNames = null;
    private String[] mChannelIds = null;
    // livedate filed
    private MutableLiveData<Integer> mChannelDataChange = new MutableLiveData<>();


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "MainActivityViewModel: ");
        mContext = application;
    }

    @Override
    protected void onCleared() {
        LogUtils.d(TAG, "onCleared: ");
        mChannelDataChange = null;
        if (null != mContext && Common.getInstance().isExitClearCache()) {
            int status = CacheManager.clearAllCache(mContext);
            LogUtils.d(TAG, "onCleared: clearAllCache status = " + status);
        }
        mContext = null;
        mChannelIds = null;
        mChannelNames = null;
        super.onCleared();
    }

    /***************************** public function *****************************/
    public void initialized() {
        LogUtils.d(TAG, "initialized: ");
        Common common = Common.getInstance();
        Resources resources = common.getResources();
        if (null != resources) {
            // news channels initialized
            mChannelNames = resources.getStringArray(R.array.news_channel_names);
            mChannelIds = DataDef.NewsChanelIDs.CHANEL_IDS;
            if (null != mChannelIds && null != mChannelNames) {
                for (int i = 0; i < mChannelIds.length; i++) {
                    LogUtils.d(TAG, "initialized: channelID:" + i + " = " + mChannelIds[i]);
                    common.addChannelID(i, mChannelIds[i]);
                }
            }
        }
    }

    /***************************** livedata function *****************************************/
    public MutableLiveData<Integer> getChannelDataChange() {
        return mChannelDataChange;
    }

    /***************************** public function *****************************************/
    public int getChannelCount() {
        if (null == mChannelNames) {
            initialized();
        }
        if (null != mChannelNames) {
            return mChannelNames.length;
        }
        return 0;
    }

    public String getChannelName(int index) {
        LogUtils.d(TAG, "getChannelName: index = " + index + ", mChannelNames[index] = " + mChannelNames[index]);
        if (null == mChannelNames) {
            initialized();
        }
        if (null != mChannelNames && index < mChannelNames.length) {
            return mChannelNames[index];
        }
        return "";
    }

}
