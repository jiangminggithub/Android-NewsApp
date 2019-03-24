package com.jm.news.util;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.jm.news.bean.NewsBannerBean;
import com.jm.news.bean.NewsBannerDataBean;
import com.jm.news.bean.NewsChannelDataBean;
import com.jm.news.bean.NewsDataBean;
import com.jm.news.bean.NewsDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean;
import com.jm.news.bean.NewsDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean.ImageurlsBean;
import com.jm.news.bean.NewsItemBean;
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
    private final static String REQUEST_DEFAULT_PAGE_INDEX = "0";
    private final static String REQUEST_DEFAULT_MAX_RESULT = "30";
    public final static int NEWS_TYPE_CHANNEL = 0;
    public final static int NEWS_TYPE_NEWS = 1;
    public final static int NEWS_TYPE_NEWS_BANNER = 3;
    // function related field
    private Context mContext;
    private DataResponseListener mDataResponsetListener;


    public DataManager(Context context) {
        this.mContext = context;
    }

    public void requestNewsNetworkData(@Nullable final int requestType, final String channelId, int pageIndex, int maxResult) {
        LogUtils.d(TAG, "requestNewsNetworkData: requestType = " + requestType
                + ", channelId = " + channelId
                + ", pageIndex = " + pageIndex
                + ", maxResult = " + maxResult);

        if (!requestNetDataPreChecked()) {
            return;
        }
        if (null == channelId) {
            if (null != mDataResponsetListener) {
                mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
                mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, 0, null);
                mDataResponsetListener.newsChanelDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
            }
            return;
        }

        final String type = requestType == NEWS_TYPE_CHANNEL ? DataDef.ApiInfo.NEWS_DATA_CHANNEL : DataDef.ApiInfo.NEWS_DATA_NEWS;
        final String page = pageIndex > 0 ? String.valueOf(pageIndex) : REQUEST_DEFAULT_PAGE_INDEX;
        final String maxCount = maxResult > 0 ? String.valueOf(maxResult) : REQUEST_DEFAULT_MAX_RESULT;

        new Thread(new Runnable() {
            @Override
            public void run() {

                String result;
                try {
                    result = new ShowApiRequest(type, decode(DataDef.ApiInfo.API_ID), decode(DataDef.ApiInfo.API_SECRET))
                            .addTextPara("channelId", channelId)
                            .addTextPara("page", page)
                            .addTextPara("maxResult", maxCount)
                            .post();

                    LogUtils.d(TAG, "run: result=" + result);

                    Gson gson = new Gson();
                    switch (requestType) {
                        case NEWS_TYPE_CHANNEL:
                            NewsChannelDataBean chanelDataBean = gson.fromJson(result, NewsChannelDataBean.class);
                            if (chanelDataBean.getShowapi_res_code() == DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK) {
                                mDataResponsetListener.newsChanelDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK, chanelDataBean);
                            } else {
                                mDataResponsetListener.newsChanelDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, chanelDataBean);
                            }
                            break;
                        case NEWS_TYPE_NEWS:
                            NewsDataBean dataBean = gson.fromJson(result, NewsDataBean.class);
                            if (dataBean.getShowapi_res_code() == DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK) {
                                newsDataConversion(dataBean);
                            } else {
                                mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, 0, null);
                            }
                            break;
                        case NEWS_TYPE_NEWS_BANNER:
                            NewsBannerDataBean bannerBean = gson.fromJson(result, NewsBannerDataBean.class);
                            if (bannerBean.getShowapi_res_code() == DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK) {
                                newsBannerDataConversion(bannerBean);
                            } else {
                                mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, "run: failed request ");
                    requestFailedResponse(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED);
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void newsDataConversion(NewsDataBean dataBean) {
        if (null == dataBean) {
            LogUtils.d(TAG, "newsDataConversion: NewsDataBean is NULL");
            mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, 0, null);
            return;
        }

        List<NewsItemBean> DataList = new ArrayList<>();
        NewsItemBean newDataItem;
        List<ImageurlsBean> imageUrlsBeanList;
        try {
            for (ContentlistBean bean : dataBean.getShowapi_res_body().getPagebean().getContentlist()) {
                int imgCount = 0;
                newDataItem = new NewsItemBean();
                newDataItem.setId(bean.getId());
                newDataItem.setTitle(bean.getTitle());
                newDataItem.setSource(bean.getSource());
                newDataItem.setLink(bean.getLink());
                newDataItem.setPubDate(bean.getPubDate());
                imageUrlsBeanList = new ArrayList<>();

                for (ImageurlsBean imgBean : bean.getImageurls()) {
                    if (!imgBean.getUrl().endsWith(DataDef.ApiInfo.IMG_IGNORE_SUFFIX)) {
                        imageUrlsBeanList.add(imgBean);
                        ++imgCount;
                    }
                }

                newDataItem.setImageurls(imageUrlsBeanList);
                newDataItem.setPicCount(imgCount);
                DataList.add(newDataItem);
            }
            int allPages = dataBean.getShowapi_res_body().getPagebean().getAllPages();
            mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK, allPages, DataList);
            LogUtils.d(TAG, "newsDataConversion: Conversion is Ok");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "newsDataConversion: Conversion is Failed");
            mDataResponsetListener.newsDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, 0, null);
        }
    }

    private void newsBannerDataConversion(NewsBannerDataBean bannerBean) {
        if (null == bannerBean) {
            LogUtils.d(TAG, "newsBannerDataConversion: NewsBannerDataBean is NULL");
            mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
            return;
        }

        List<String> bannerTitles = new ArrayList<>();
        List<String> bannerImages = new ArrayList<>();
        List<String> bannerUrls = new ArrayList<>();
        try {
            List<NewsBannerDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentList = bannerBean.getShowapi_res_body().getPagebean().getContentlist();
            for (int i = 0; i < contentList.size(); i++) {
                if (bannerTitles.size() >= 10) {
                    break;
                }

                NewsBannerDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean item = contentList.get(i);
                if (null == item) {
                    LogUtils.d(TAG, "newsBannerDataConversion: nullItem");
                    continue;
                }

                List<NewsBannerDataBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean.ImageurlsBean> imageUrls = item.getImageurls();
                if (null == imageUrls || imageUrls.size() < 1) {
                    LogUtils.d(TAG, "newsBannerDataConversion: null imageUrls");
                    continue;
                }

                String title = item.getTitle();
                String imgUrl = imageUrls.get(0).getUrl();
                String url = item.getLink();

                if (imgUrl.endsWith(DataDef.ApiInfo.IMG_IGNORE_SUFFIX)
                        || "".equals(title)
                        || "".equals(imgUrl)
                        || "".equals(url)) {
                    continue;
                }

                LogUtils.d(TAG, "newsBannerDataConversion: imgUrl=" + imgUrl);
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
                LogUtils.d(TAG, "newsBannerDataConversion: Conversion is OK");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "newsBannerDataConversion: Conversion is Failed");
            mDataResponsetListener.newsBannerDataBeanChange(DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED, null);
        }
    }

    public void RequestNativeData() {

    }

    /**
     * 网络请求检查
     *
     * @return Boolean 是否可以访问
     */
    private boolean requestNetDataPreChecked() {
        if (null != mDataResponsetListener) {
            // 判断网络是否可用
            LogUtils.d(TAG, "requestDataChecked: isNetworkAvailable " + CommonUtils.getInstance().isNetworkAvailable());
            if (!CommonUtils.getInstance().isNetworkAvailable()) {
                requestFailedResponse(DataDef.RequestStatusType.DATA_STATUS_NETWORK_DISCONNECTED);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 数据请求失败响应
     *
     * @param requestStatus 需要响应的状态
     */
    private void requestFailedResponse(int requestStatus) {
        if (null != mDataResponsetListener) {
            mDataResponsetListener.newsBannerDataBeanChange(requestStatus, null);
            mDataResponsetListener.newsDataBeanChange(requestStatus, 0, null);
            mDataResponsetListener.newsChanelDataBeanChange(requestStatus, null);
            LogUtils.d(TAG, "requestFailedResponse: ");
        }
    }


    public void setDataRequestListener(DataResponseListener dataResponseListener) {
        this.mDataResponsetListener = dataResponseListener;
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
        for (int i = 0, size = e.length; i < size; i++) {
            for (byte keyByte : keyBytes) {
                e[i] = (byte) (e[i] ^ keyByte);
            }
        }
        return new String(e);
    }


    /************************************** inner class ******************************************/
    /**
     * Data Response Listener Define
     */
    public static abstract class DataResponseListener {
        public void newsDataBeanChange(int requestStatus, int allPages, List<NewsItemBean> newsDataItemList) {
        }

        public void newsChanelDataBeanChange(int requestStatus, NewsChannelDataBean newsChannelDataBean) {
        }

        public void newsBannerDataBeanChange(int requestStatus, NewsBannerBean newsBannerBean) {
        }
    }
}
