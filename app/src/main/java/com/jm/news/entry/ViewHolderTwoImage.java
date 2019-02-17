package com.jm.news.entry;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.customview.MImageViewBase;

public class ViewHolderTwoImage extends RecyclerView.ViewHolder {
    private TextView mTitle;
    private TextView mSource;
    private TextView mPubData;
    private MImageViewBase mImageLeft;
    private MImageViewBase mImageRight;

    public ViewHolderTwoImage(@NonNull View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.news_item_two_title);
        mSource = itemView.findViewById(R.id.news_item_two_source);
        mPubData = itemView.findViewById(R.id.news_item_two_data);
        mImageLeft = itemView.findViewById(R.id.news_item_two_img_left);
        mImageRight = itemView.findViewById(R.id.news_item_two_img_rignt);
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

    public MImageViewBase getmImageLeft() {
        return mImageLeft;
    }

    public MImageViewBase getmImageRight() {
        return mImageRight;
    }
}
