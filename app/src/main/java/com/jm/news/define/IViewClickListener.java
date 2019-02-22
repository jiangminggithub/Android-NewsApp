package com.jm.news.define;

import android.view.View;

/**
 * IViewClickListener: Click or longClick
 */
public interface IViewClickListener {

    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
