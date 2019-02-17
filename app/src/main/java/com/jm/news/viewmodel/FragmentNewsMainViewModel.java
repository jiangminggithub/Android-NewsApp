package com.jm.news.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jm.news.R;
import com.jm.news.bean.NewsBannerBean;
import com.jm.news.bean.NewsDataItemBean;
import com.jm.news.common.Common;
import com.jm.news.define.DataDef;
import com.jm.news.util.DataManager;
import com.jm.news.util.DataManager.DataResponsetListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentNewsMainViewModel extends AndroidViewModel {
    // static filed
    private static final String TAG = "FragmentNewsMainViewModel";
    private static final int DEFAULT_PAGE_INDEX = 1;
    private static final int DEFAULT_REQUEST_COUNT = 30;

    // function related filed
    private boolean isRefresh = true;
    private String channelID = null;
    private int currentPageIndex = DEFAULT_PAGE_INDEX;
    private int allPagesCount = 0;
    private int requestCount = DEFAULT_REQUEST_COUNT;
    private DataManager mDataManager = null;
    private MyDataResponsetListener mDataRequestListener = null;
    private List<NewsDataItemBean> mNewsDataItemList = new ArrayList<>();
    private NewsBannerBean mNewsBannerDataBean = null;

    // livedate filed
    private MutableLiveData<Integer> mNewsBannerDataStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> mNewsDataStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> mNewsDataCountStatus = new MutableLiveData<>();

    @SuppressLint("LongLogTag")
    public FragmentNewsMainViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "FragmentNewsMainViewModel: ");
        mDataManager = new DataManager(application);
        mDataRequestListener = new MyDataResponsetListener();
        mDataManager.setDataRequestListener(mDataRequestListener);
        String bannerID = DataDef.NewsChanelIDs.BANNER_ID;
        if (null != bannerID && !"".equals(bannerID)) {
            mDataManager.requestNewsNetworkData(DataManager.NEWS_TYPE_NEWS_BANNER, bannerID, 1, 50);
        } else {
            mNewsBannerDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED);
        }
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

    public MutableLiveData<Integer> getNewsBannerDataStatus() {
        return mNewsBannerDataStatus;
    }

    /***************************** public function *****************************************/
    public List<String> getBannerTitles() {
        if (null != mNewsBannerDataBean) {
            return mNewsBannerDataBean.getBannerTitles();
        }
        return null;
    }

    public List<String> getBannerImgUrls() {
        if (null != mNewsBannerDataBean) {
            return mNewsBannerDataBean.getBannerImages();
        }
        return null;
    }

    public String getBannerLink(int index) {
        if (null == mNewsBannerDataBean || null == mNewsBannerDataBean.getBannerUrls() || null == mNewsBannerDataBean.getBannerUrls().get(index)) {
            return null;
        }
        return mNewsBannerDataBean.getBannerUrls().get(index);
    }

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

    public int getItemImgCount(int index) {
        if (null == mNewsDataItemList.get(index)) {
            return 0;
        }

        return mNewsDataItemList.get(index).getPicCount();
    }

    public String getNewsLink(int index) {
        if (null == mNewsDataItemList || null == mNewsDataItemList.get(index)) {
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
    private class MyDataResponsetListener extends DataResponsetListener {

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

        @SuppressLint("LongLogTag")
        @Override
        public void newsBannerDataBeanChange(int requestStatus, NewsBannerBean newsBannerBean) {
            Log.d(TAG, "newsBannerDataBeanChange: requestStatus = " + requestStatus);
            switch (requestStatus) {
                case DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK:
                    if (null != newsBannerBean) {
                        mNewsBannerDataBean = newsBannerBean;
                        mNewsBannerDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK);
                        Log.d(TAG, "newsBannerDataBeanChange: OK");
                    }
                    break;
                case DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED:
                    mNewsBannerDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED);
                    break;
                case DataDef.RequestStatusType.DATA_STATUS_NETWORK_DISCONNECTED:
                    mNewsBannerDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_NETWORK_DISCONNECTED);
                    break;
                default:
                    break;
            }
        }
    }
}
