package com.jm.news.entry;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.customview.MImageViewBase;

public class ViewHolderOneImage extends RecyclerView.ViewHolder {
    private TextView mTitle;
    private TextView mSource;
    private TextView mPubData;
    private MImageViewBase mImage;
    private LinearLayout mImageLatout;

    public ViewHolderOneImage(@NonNull View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.news_item_one_title);
        mSource = itemView.findViewById(R.id.news_item_one_source);
        mImage = itemView.findViewById(R.id.news_item_one_img);
        mPubData = itemView.findViewById(R.id.news_item_one_date);
        mImageLatout = itemView.findViewById(R.id.news_item_one_img_ll);
    }

    public TextView getmTitle() {
        return mTitle;
    }

    public TextView getmSource() {
        return mSource;
    }

    public TextView getmPubData() {
        return mPubData;
    }

    public MImageViewBase getmImage() {
        return mImage;
    }

    public LinearLayout getmImageLatout() {
        return mImageLatout;
    }
}
