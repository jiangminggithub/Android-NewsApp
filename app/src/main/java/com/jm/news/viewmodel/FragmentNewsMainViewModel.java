package com.jm.news.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.jm.news.bean.NewsBannerBean;
import com.jm.news.bean.NewsItemBean;
import com.jm.news.common.Common;
import com.jm.news.define.DataDef;
import com.jm.news.util.DataManager;
import com.jm.news.util.DataManager.DataResponseListener;
import com.jm.news.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class FragmentNewsMainViewModel extends AndroidViewModel {

    // static filed
    private static final String TAG = "FragmentNewsMainViewModel";
    private static final int DEFAULT_PAGE_INDEX = 1;
    private static final int DEFAULT_REQUEST_COUNT = 30;
    // function related filed
    private boolean isRefresh = true;
    private String mChannelID = null;
    private int mCurrentPageIndex = DEFAULT_PAGE_INDEX;
    private int mAllPagesCount = 0;
    private int mRequestCount = DEFAULT_REQUEST_COUNT;
    private DataManager mDataManager = null;
    private MyDataResponseListener mDataRequestListener = null;
    private List<NewsItemBean> mNewsDataItemList = new ArrayList<>();
    private NewsBannerBean mNewsBannerDataBean = null;
    // livedate filed
    private MutableLiveData<Integer> mNewsBannerDataStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> mNewsDataStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> mNewsDataCountStatus = new MutableLiveData<>();


    public FragmentNewsMainViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "FragmentNewsMainViewModel: ");
        mDataManager = new DataManager(application);
        mDataRequestListener = new MyDataResponseListener();
        mDataManager.setDataRequestListener(mDataRequestListener);
        mDataManager.requestNewsNetworkData(DataManager.NEWS_TYPE_NEWS_BANNER, DataDef.NewsChanelIDs.BANNER_ID, DEFAULT_PAGE_INDEX, DEFAULT_REQUEST_COUNT);
    }

    @Override
    protected void onCleared() {
        if (null != mDataManager) {
            mDataManager.removeDataRequestListener();
        }
        mDataManager = null;
        mDataRequestListener = null;
        mNewsDataItemList = null;
        mNewsBannerDataBean = null;
        mNewsBannerDataStatus = null;
        mNewsDataStatus = null;
        mNewsDataCountStatus = null;
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
    public void updateChannelID(int Index) {
        mChannelID = Common.getInstance().getChannelID(Index);
        LogUtils.d(TAG, "updateChannelID: mChannelID=" + mChannelID);
    }

    public void requestRefreshData() {
        if (null != mChannelID) {
            mCurrentPageIndex = DEFAULT_PAGE_INDEX;
            isRefresh = true;
            mDataManager.requestNewsNetworkData(DataManager.NEWS_TYPE_NEWS, mChannelID, mCurrentPageIndex, mRequestCount);
            LogUtils.d(TAG, "requestRefreshData: OK");
        } else {
            mNewsDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED);
            LogUtils.d(TAG, "requestRefreshData: Failed");
        }
        //        requestBannerDate();
    }

    public void requestBannerDate() {
        if (null != mDataManager) {
            mDataManager.requestNewsNetworkData(DataManager.NEWS_TYPE_NEWS_BANNER, DataDef.NewsChanelIDs.BANNER_ID, DEFAULT_PAGE_INDEX, DEFAULT_REQUEST_COUNT);
        }
    }


    public void requestLoadMoreData() {
        if (null != mChannelID) {
            mCurrentPageIndex += mCurrentPageIndex;
            isRefresh = false;
            if (mCurrentPageIndex <= mAllPagesCount) {
                mDataManager.requestNewsNetworkData(DataManager.NEWS_TYPE_NEWS, mChannelID, mCurrentPageIndex, mRequestCount);
            } else {
                mNewsDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_NO_MORE_DATA);
            }
            LogUtils.d(TAG, "requestLoadMoreData: OK ");
        } else {
            mNewsDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED);
            LogUtils.d(TAG, "requestLoadMoreData: Failed ");
        }
    }


    public void removeNewsItem(int index) {
        LogUtils.d(TAG, "removeNewsItem: index = " + index);
        if (index >= 0) {
            mNewsDataItemList.remove(index);
            mNewsDataCountStatus.postValue(true);
        }
    }

    /***************************** listener function *****************************************/
    private class MyDataResponseListener extends DataResponseListener {

        @Override
        public void newsDataBeanChange(int requestStatus, int allPages, List<NewsItemBean> newsDataItemList) {
            LogUtils.d(TAG, "newsDataBeanChange: requestStatus = " + requestStatus
                    + " , allPages = " + allPages);
            switch (requestStatus) {
                case DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK:
                    if (null != newsDataItemList) {
                        mAllPagesCount = allPages;
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

        @Override
        public void newsBannerDataBeanChange(int requestStatus, NewsBannerBean newsBannerBean) {
            LogUtils.d(TAG, "newsBannerDataBeanChange: requestStatus = " + requestStatus);
            switch (requestStatus) {
                case DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK:
                    if (null != newsBannerBean) {
                        mNewsBannerDataBean = newsBannerBean;
                        mNewsBannerDataStatus.postValue(DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK);
                        LogUtils.d(TAG, "newsBannerDataBeanChange: OK");
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
