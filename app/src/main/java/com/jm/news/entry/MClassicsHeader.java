package com.jm.news.entry;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MClassicsHeader extends ClassicsHeader {
    private Common mCommon = Common.getInstance();
    public MClassicsHeader(Context context) {
        super(context);
    }

    public MClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MClassicsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View arrowView = mArrowView;
        final View updateView = mLastUpdateText;
        switch (newState) {
            case None:
                updateView.setVisibility(mEnableLastTime ? VISIBLE : GONE);
            case PullDownToRefresh:
                mTitleText.setText(mCommon.getResourcesString(R.string.srl_header_pulling));
                arrowView.setVisibility(VISIBLE);
                arrowView.animate().rotation(0);
                break;
            case Refreshing:
            case RefreshReleased:
                mTitleText.setText(mCommon.getResourcesString(R.string.srl_header_refreshing));
                arrowView.setVisibility(GONE);
                break;
            case ReleaseToRefresh:
                mTitleText.setText(mCommon.getResourcesString(R.string.srl_header_release));
                arrowView.animate().rotation(180);
                mLastUpdateFormat = new SimpleDateFormat(mCommon.getResourcesString(R.string.srl_header_update), Locale.getDefault());
                break;
            case ReleaseToTwoLevel:
                mTitleText.setText(mCommon.getResourcesString(R.string.srl_header_secondary));
                arrowView.animate().rotation(0);
                break;
            case Loading:
                arrowView.setVisibility(GONE);
                updateView.setVisibility(mEnableLastTime ? INVISIBLE : GONE);
                mTitleText.setText(mCommon.getResourcesString(R.string.srl_header_loading));
                break;
        }

    }
}
