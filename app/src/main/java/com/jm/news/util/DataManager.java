package com.jm.news.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.jm.news.bean.NewsBannerBean;
import com.jm.news.bean.NewsBannerDataBean;
import com.jm.news.bean.NewsChannelDataBean;
import com.jm.news.bean.NewsDataBean;
import com.jm.news.bean.NewsDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean;
import com.jm.news.bean.NewsDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean.ImageurlsBean;
import com.jm.news.bean.NewsDataItemBean;
import com.jm.news.define.DataDef;
import com.show.api.ShowApiRequest;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    // static field
    private static final String TAG = "DataManager";
    private static final String appKey = "Jiang&&Ming&&News";
    private static final Charset charset = Charset.forName("UTF-8");
    private static byte[] keyBytes = appKey.getBytes(charset);
    private final static String NEWS_DATA_CHANNEL = "http://route.showapi.com/109-34";
    private final static String NEWS_DATA_NEWS = "http://route.showapi.com/109-35";
    private final static String IMG_IGNORE_FLAG = "http://static.ws.126.net/cnews/css13/img/end_news.png";
    public final static int NEWS_TYPE_CHANNEL = 0;
    public final static int NEWS_TYPE_NEWS = 1;
    public final static int NEWS_TYPE_NEWS_BANNER = 3;

    // function related field
    private Context mContext;
    //    private Resources resources;
    private DataResponsetListener mDataResponsetListener;

    public void requestData() {
        if (null == mDataResponsetListener) {

        }

    }

    public DataManager(Context context) {
        this.mContext = context;
//        resources = mContext.getResources();
    }


    public void requestNewsNetworkData(@Nullable final int requestType, final String channelId, int pageIndex, int maxResult) {
        Log.d(TAG, "requestNewsNetworkData: ------------------------------------------------------------------------------------");
        Log.d(TAG, "requestNewsNetworkData: requestType = " + requestType + ", channelId = " + channelId + ", pageIndex = " + pageIndex + ", maxResult = " + maxResult);
        Log.d(TAG, "requestNewsNetworkData: ------------------------------------------------------------------------------------");
//        if (true) {
//            mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
//            mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, 0, null);
//            mDataResponsetListener.newsChanelDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
//            return;
//        }

        if (null == channelId) {
            if (null != mDataResponsetListener) {
                mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
                mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, 0, null);
                mDataResponsetListener.newsChanelDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
            }
            return;
        }

        final String type = requestType == NEWS_TYPE_CHANNEL ? NEWS_DATA_CHANNEL : NEWS_DATA_NEWS;
        final String page = pageIndex > 0 ? String.valueOf(pageIndex) : "1";
        final String maxCount = maxResult > 0 ? String.valueOf(maxResult) : "20";

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = new ShowApiRequest(type, decode(DataDef.ApiInfo.API_ID), decode(DataDef.ApiInfo.API_secret))
                        .addTextPara("channelId", channelId)
                        .addTextPara("page", page)
                        .addTextPara("maxResult", maxCount)
                        .post();
                Log.d(TAG, "run: result=" + result);

                Gson gson = new Gson();
                switch (requestType) {
                    case NEWS_TYPE_CHANNEL:
                        NewsChannelDataBean chanelDataBean = gson.fromJson(result, NewsChannelDataBean.class);
                        if (chanelDataBean.getShowapi_res_code() == 0) {
                            mDataResponsetListener.newsChanelDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK, chanelDataBean);
                        } else {
                            mDataResponsetListener.newsChanelDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, chanelDataBean);
                        }
                        break;
                    case NEWS_TYPE_NEWS:
                        NewsDataBean dataBean = gson.fromJson(result, NewsDataBean.class);
                        if (dataBean.getShowapi_res_code() == 0) {
                            newsDataConver(dataBean);
                        } else {
                            mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, 0, null);
                        }
                        break;
                    case NEWS_TYPE_NEWS_BANNER:
                        NewsBannerDataBean bannerBean = gson.fromJson(result, NewsBannerDataBean.class);
                        if (bannerBean.getShowapi_res_code() == 0) {
                            newsBannerDataConver(bannerBean);
                        } else {
                            mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
                        }
                        break;
                    default:
                        break;
                }
            }
        }).start();

    }

    private void newsDataConver(NewsDataBean dataBean) {
        if (null == dataBean) {
            Log.d(TAG, "newsDataConver: NewsDataBean is NULL");
            mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, 0, null);
            return;
        }

        List<NewsDataItemBean> DataList = new ArrayList<>();
        NewsDataItemBean newDataItem = null;
        List<ImageurlsBean> imageurlsBeanList = null;
        try {
            for (ContentlistBean bean : dataBean.getShowapi_res_body().getPagebean().getContentlist()) {
                int imgCount = 0;
                newDataItem = new NewsDataItemBean();
                newDataItem.setId(bean.getId());
                newDataItem.setTitle(bean.getTitle());
                newDataItem.setSource(bean.getSource());
                newDataItem.setLink(bean.getLink());
                newDataItem.setPubDate(bean.getPubDate());
                imageurlsBeanList = new ArrayList<>();

                for (ImageurlsBean imgBean : bean.getImageurls()) {
                    if (!IMG_IGNORE_FLAG.equals(imgBean.getUrl())) {
                        imageurlsBeanList.add(imgBean);
                        ++imgCount;
                    }
                }

                newDataItem.setImageurls(imageurlsBeanList);
                newDataItem.setPicCount(imgCount);
                DataList.add(newDataItem);
            }
            int allPages = dataBean.getShowapi_res_body().getPagebean().getAllPages();
            mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK, allPages, DataList);
            Log.d(TAG, "newsDataConver: Conver is Ok");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "newsDataConver: Conver is Failed");
            mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, 0, null);
        }
    }

    private void newsBannerDataConver(NewsBannerDataBean bannerBean) {
        if (null == bannerBean) {
            Log.d(TAG, "newsBannerDataConver: NewsBannerDataBean is NULL");
            mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
            return;
        }

        List<String> bannerTitles = new ArrayList<>();
        List<String> bannerImages = new ArrayList<>();
        List<String> bannerUrls = new ArrayList<>();
        try {
            List<NewsBannerDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentlist = bannerBean.getShowapi_res_body().getPagebean().getContentlist();
            for (int i = 0; i < contentlist.size(); i++) {
                if (bannerTitles.size() >= 10) {
                    break;
                }

                NewsBannerDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean item = contentlist.get(i);
                if (null == item) {
                    Log.d(TAG, "newsBannerDataConver: nullitem");
                    continue;
                }
                List<NewsBannerDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean.ImageurlsBean> imageurls = item.getImageurls();
                if (null == imageurls || imageurls.size() < 1) {
                    Log.d(TAG, "newsBannerDataConver: null imageurls");
                    continue;
                }
                String title = item.getTitle();
                String imgUrl = imageurls.get(0).getUrl();
                String url = item.getLink();

                if (IMG_IGNORE_FLAG.equals(imgUrl) || "".equals(title) || "".equals(imgUrl) || "".equals(url)) {
                    continue;
                }

                Log.d(TAG, "newsBannerDataConver: imgUrl=" + imgUrl);
                bannerTitles.add(title);
                bannerImages.add(imgUrl);
                bannerUrls.add(url);
            }

            NewsBannerBean bannerDataBean = new NewsBannerBean();
            bannerDataBean.setBannerTitles(bannerTitles);
            bannerDataBean.setBannerImages(bannerImages);
            bannerDataBean.setBannerUrls(bannerUrls);

            int titleSize = bannerDataBean.getBannerTitles().size();
            int imgUrlsSize = bannerDataBean.getBannerImages().size();
            int urlSize = bannerDataBean.getBannerUrls().size();

            if (titleSize > 0 && imgUrlsSize > 0 && urlSize > 0) {
                mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK, bannerDataBean);
                Log.d(TAG, "newsBannerDataConver: Conver is OK");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "newsBannerDataConver: Conver is Failed");
            mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
        }


    }

    public void RequestNativeData() {

    }

    private boolean isRequestVisible() {
        if (null != mDataResponsetListener) {

        }
        return false;
    }


    public void setDataRequestListener(DataResponsetListener dataResponsetListener) {
        this.mDataResponsetListener = dataResponsetListener;
    }

    public void removeDataRequestListener() {
        this.mDataResponsetListener = null;
    }


    /**
     * Character data encryption operations
     *
     * @param enc The target character to be encrypted
     * @return Encrypted string
     */
    public static String encode(String enc) {
        byte[] b = enc.getBytes(charset);
        for (int i = 0, size = b.length; i < size; i++) {
            for (byte keyBytes0 : keyBytes) {
                b[i] = (byte) (b[i] ^ keyBytes0);
            }
        }
        return new String(b);
    }

    /**
     * Character data decryption operation
     *
     * @param dec The target character to decrypt
     * @return Decrypted string
     */
    private static String decode(String dec) {
        byte[] e = dec.getBytes(charset);
        byte[] dee = e;
        for (int i = 0, size = e.length; i < size; i++) {
            for (byte keyBytes0 : keyBytes) {
                e[i] = (byte) (dee[i] ^ keyBytes0);
            }
        }
        return new String(e);
    }


    /************************************** inner class ******************************************/
    /**
     * Data Response Listener Define
     */
    public static abstract class DataResponsetListener {
        public void newsDataBeanChange(int requestStatus, int allPages, List<NewsDataItemBean> newsDataItemList) {
        }

        public void newsChanelDataBeanChange(int requestStatus, NewsChannelDataBean newsChannelDataBean) {
        }

        public void newsBannerDataBeanChange(int requestStatus, NewsBannerBean newsBannerBean) {

        }
    }
}
