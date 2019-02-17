package com.jm.news.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jm.news.bean.NewsDataItemBean;
import com.jm.news.common.Common;
import com.jm.news.define.DataDef;
import com.jm.news.util.DataManager;

import java.util.ArrayList;
import java.util.List;

public class FragmentNewsItemViewModel extends AndroidViewModel {

    // static filed
    private static final String TAG = "FragmentNewsItemViewModel";
    private static final int DEFAULT_PAGE_INDEX = 1;
    private static final int DEFAULT_REQUEST_COUNT = 30;
    private static final boolean FLAG_REQUEST_TYPE_REFRESH = false;

    // function related filed
    private boolean isRefresh = true;
    private String channelID = null;
    private int currentPageIndex = DEFAULT_PAGE_INDEX;
    private int allPagesCount = 0;
    private int requestCount = DEFAULT_REQUEST_COUNT;
    private DataManager mDataManager = null;
    private MyDataResponsetListener mDataRequestListener = null;
    private List<NewsDataItemBean> mNewsDataItemList = new ArrayList<>();

    // livedate filed
    private MutableLiveData<Integer> mNewsDataStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> mNewsDataCountStatus = new MutableLiveData<>();


    public FragmentNewsItemViewModel(@NonNull Application application) {
        super(application);
        mDataManager = new DataManager(application);
        mDataRequestListener = new MyDataResponsetListener();
        mDataManager.setDataRequestListener(mDataRequestListener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    /***************************** livedata function *****************************************/
    public MutableLiveData<Integer> getNewsDataStatus() {
        return mNewsDataStatus;
    }

    public MutableLiveData<Boolean> getNewsDataCountStatus() {
        return mNewsDataCountStatus;
    }

    /***************************** public function   *****************************************/
    public int getListCount() {
        return mNewsDataItemList.size();
    }

    public String getNewsTitle(int index) {
        if (null == mNewsDataItemList.get(index)) {
            return "";
        }

        return mNewsDataItemList.get(index).getTitle();
    }

    public String getNewsSource(int index) {
        if (null == mNewsDataItemList.get(index)) {
            return "";
        }

        return mNewsDataItemList.get(index).getSource();
    }

    public String getImgUrls(int index, int imgIndex) {
        if (null == mNewsDataItemList.get(index)) {
            return "";
        }

        return mNewsDataItemList.get(index).getImageurls().get(imgIndex).getUrl();
    }

    public String getPubDate(int index) {
//        if (null == mNewsDataItemList.get(index)) {
//            return "";
//        }

        return "";
    }

    @SuppressLint("LongLogTag")
    public int getItemImgCount(int index) {
        if (null == mNewsDataItemList.get(index)) {
            return 0;
        }

        return mNewsDataItemList.get(index).getPicCount();
    }

    public String getNewsLink(int index) {
        if (null == mNewsDataItemList.get(index)) {
            return null;
        }
        return mNewsDataItemList.get(index).getLink();
    }

    /***************************** operation function   **************************************/
    @SuppressLint("LongLogTag")
    public void updatetChannelID(int Index) {
        channelID = Common.getInstance().getChannelID(Index);
        Log.d(TAG, "updatetChannelID: channelID=" + channelID);
    }

    @SuppressLint("LongLogTag")
    public void requestRefreshData() {
        if (null != channelID) {
            currentPageIndex = DEFAULT_PAGE_INDEX;
            isRefresh = true;
            mDataManager.requestNewsNetworkData(DataManager.NEWS_TYPE_NEWS, channelID, currentPageIndex, requestCount);
            Log.d(TAG, "requestRefreshData: OK");
        } else {
            mNewsDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED);
            Log.d(TAG, "requestRefreshData: Failed");
        }

    }

    @SuppressLint("LongLogTag")
    public void requestLoadMoreData() {
        if (null != channelID) {
            currentPageIndex += currentPageIndex;
            isRefresh = false;
            if (currentPageIndex <= allPagesCount) {
                mDataManager.requestNewsNetworkData(DataManager.NEWS_TYPE_NEWS, channelID, currentPageIndex, requestCount);
            } else {
                mNewsDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_NO_MOREDATA);
            }
            Log.d(TAG, "requestLoadMoreData: OK ");

        } else {
            mNewsDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED);
            Log.d(TAG, "requestLoadMoreData: Failed ");
        }
    }

    @SuppressLint("LongLogTag")
    public void removeNewsItem(int index) {
        Log.d(TAG, "removeNewsItem: index = " + index);
        if (index >= 0) {
            mNewsDataItemList.remove(index);
            mNewsDataCountStatus.postValue(true);
        }
    }

    /***************************** listener function *****************************************/

    private class MyDataResponsetListener extends DataManager.DataResponsetListener {
        @SuppressLint("LongLogTag")
        @Override
        public void newsDataBeanChange(int requestStatus, int allPages, List<NewsDataItemBean> newsDataItemList) {
            Log.d(TAG, "newsDataBeanChange: requestStatus=" + requestStatus + " , allPages=" + allPages);
            switch (requestStatus) {
                case DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK:
                    if (null != newsDataItemList) {
                        allPagesCount = allPages;
                        if (isRefresh) {
                            mNewsDataItemList = newsDataItemList;
                            mNewsDataCountStatus.postValue(Boolean.TRUE);
                        } else {
                            mNewsDataItemList.addAll(newsDataItemList);
                            mNewsDataCountStatus.postValue(Boolean.FALSE);
                        }
                    }
                    break;
                case DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED:
                    mNewsDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED);
                    break;
                case DataDef.RequestStatusType.DATA_STATUS_NETWORK_DISCONNECTED:
                    mNewsDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_NETWORK_DISCONNECTED);
                    break;
                default:
                    break;
            }
        }
    }
}
