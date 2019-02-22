package com.jm.news.customview;

import android.content.Context;
import android.util.AttributeSet;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.util.LogUtils;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MClassicsHeaderView extends ClassicsHeader {

    // static field
    private static final String TAG = "MClassicsHeaderView";

    public MClassicsHeaderView(Context context) {
        this(context, null);
    }

    public MClassicsHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MClassicsHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogUtils.d(TAG, "MClassicsHeaderView: ");
        Common common = Common.getInstance();
        mTextPulling = common.getResourcesString(R.string.srl_header_pulling);
        mTextLoading = common.getResourcesString(R.string.srl_header_loading);
        mTextRelease = common.getResourcesString(R.string.srl_header_release);
        mTextFinish = common.getResourcesString(R.string.srl_header_finish);
        mTextFailed = common.getResourcesString(R.string.srl_header_failed);
        mTextUpdate = common.getResourcesString(R.string.srl_header_update);
        mTextSecondary = common.getResourcesString(R.string.srl_header_secondary);
        mTextRefreshing = common.getResourcesString(R.string.srl_header_refreshing);
        mLastUpdateFormat = new SimpleDateFormat(common.getResourcesString(R.string.srl_header_update), common.getLocale());
        KEY_LAST_UPDATE_TIME += context.getClass().getName();
        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE);
        setLastUpdateTime(new Date(mShared.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())));
    }
}
