package com.jm.news.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.jm.news.bean.NewsChannelDataBean;
import com.jm.news.bean.NewsDataBean;
import com.jm.news.util.DataManager;

public class NewsDataViewModel extends AndroidViewModel {

    private static final String TAG = "NewsDataViewModel";
    public static final int DATA_STATUS_OK = 0;
    public static final int DATA_STATUS_FAILED = 1;
    public static final int DATA_STATUS_1 = 2;
    public static final int DATA_STATUS_2 = 3;

    private DataManager mDataManager;
    private MyDataResponsetListener mDataRequestListener = null;
    private NewsDataBean mNewsDataBean;
    private NewsChannelDataBean mNewsChannelDataBean;
    private MutableLiveData<Integer> mNewsDataChange = new MutableLiveData<>();
    private MutableLiveData<Integer> mChannelDataChange = new MutableLiveData<>();

    public NewsDataViewModel(@NonNull Application application) {
        super(application);
        mDataManager = new DataManager(application);
        mDataRequestListener = new MyDataResponsetListener();
        mDataManager.setDataRequestListener(mDataRequestListener);
        mDataManager.requestNewsNetworkData(DataManager.NEWS_TYPE_NEWS, "5572a108b3cdc86cf39001cd", 1, 20);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public MutableLiveData<Integer> getDataChange() {
        return mNewsDataChange;
    }

    public MutableLiveData<Integer> getChannelDataChange() {
        return mChannelDataChange;
    }

    private class MyDataResponsetListener extends DataManager.DataResponsetListener {

    }
}
