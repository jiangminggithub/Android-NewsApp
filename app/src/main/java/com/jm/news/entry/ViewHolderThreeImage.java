package com.jm.news.entry;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.customview.MImageViewBase;

public class ViewHolderThreeImage extends RecyclerView.ViewHolder {
    private TextView mTvTitle;
    private TextView mTvSource;
    private TextView mTvPubData;
    private MImageViewBase mIvImageLeft;
    private MImageViewBase mIvImageMiddle;
    private MImageViewBase mIvImageRight;

    public ViewHolderThreeImage(@NonNull View itemView) {
        super(itemView);
        mTvTitle = itemView.findViewById(R.id.tv_news_item_three_title);
        mTvSource = itemView.findViewById(R.id.tv_news_item_three_source);
        mTvPubData = itemView.findViewById(R.id.tv_news_item_three_data);
        mIvImageLeft = itemView.findViewById(R.id.miv_news_item_three_img_left);
        mIvImageMiddle = itemView.findViewById(R.id.miv_news_item_three_img_middle);
        mIvImageRight = itemView.findViewById(R.id.miv_news_item_three_img_rignt);
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }

    public TextView getTvSource() {
        return mTvSource;
    }

    public TextView getTvPubData() {
        return mTvPubData;
    }

    public MImageViewBase getIvImageLeft() {
        return mIvImageLeft;
    }

    public MImageViewBase getIvImageMiddle() {
        return mIvImageMiddle;
    }

    public MImageViewBase getIvImageRight() {
        return mIvImageRight;
    }
}
