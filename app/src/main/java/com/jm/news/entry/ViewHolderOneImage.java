package com.jm.news.entry;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.customview.MImageView;

public class ViewHolderOneImage extends RecyclerView.ViewHolder {
    private TextView mTvTitle;
    private TextView mTvSource;
    private TextView mTvPubData;
    private MImageView mIvImage;
    private LinearLayout mLlImageLatout;

    public ViewHolderOneImage(@NonNull View itemView) {
        super(itemView);
        mTvTitle = itemView.findViewById(R.id.tv_news_item_one_title);
        mTvSource = itemView.findViewById(R.id.tv_news_item_one_source);
        mIvImage = itemView.findViewById(R.id.miv_news_item_one_img);
        mTvPubData = itemView.findViewById(R.id.tv_news_item_one_date);
        mLlImageLatout = itemView.findViewById(R.id.ll_news_item_one_img);
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

    public MImageView getIvImage() {
        return mIvImage;
    }

    public LinearLayout getLlImageLatout() {
        return mLlImageLatout;
    }
}
