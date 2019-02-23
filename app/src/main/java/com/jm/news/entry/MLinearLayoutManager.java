package com.jm.news.entry;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jm.news.util.LogUtils;

/**
 * RecyclerView和ScrollView嵌套使用
 * <p>
 * 参考：https://www.cnblogs.com/whycxb/p/9329217.html
 */
public class MLinearLayoutManager extends LinearLayoutManager {

    // static field
    private static final String TAG = "MLinearLayoutManager";
    private static final int RECYCLE_VIEW_FIRST_POSITION = 0;
    // function related field
    private LinearLayout mRecyclerViewLayout; // 固定recyclerview的父布局
    private int[] mMeasuredDimension = new int[2];


    public MLinearLayoutManager(Context context) {
        super(context);
    }

    public MLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int layoutItemCount = state.getItemCount();
        if (layoutItemCount > 0 && layoutItemCount >= getItemCount()) {
            final int widthMode = View.MeasureSpec.getMode(widthSpec);
            final int heightMode = View.MeasureSpec.getMode(heightSpec);
            final int widthSize = View.MeasureSpec.getSize(widthSpec);
            final int heightSize = View.MeasureSpec.getSize(heightSpec);
            int width = 0;
            int height = 0;
            for (int i = 0; i < getItemCount(); i++) {
                measureScrapChild(recycler, i,
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        mMeasuredDimension);
                if (getOrientation() == HORIZONTAL) {
                    width = width + mMeasuredDimension[0];
                    if (i == RECYCLE_VIEW_FIRST_POSITION) {
                        height = mMeasuredDimension[1];
                    }
                } else {
                    height = height + mMeasuredDimension[1];
                    if (i == RECYCLE_VIEW_FIRST_POSITION) {
                        width = mMeasuredDimension[0];
                    }
                }
            }
            if (widthMode == View.MeasureSpec.EXACTLY) {
                width = widthSize;
            }
            if (heightMode == View.MeasureSpec.EXACTLY) {
                height = heightSize;
            }
            LogUtils.d(TAG, "onMeasure: width = " + width + ", height = " + height);
            setMeasuredDimension(width, height);
            // 设置recyclerview的父布局的高度值
            LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            mRecyclerViewLayout.setLayoutParams(parmas);

        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        LogUtils.d(TAG, "measureScrapChild: position = " + position);
        try {
            View view = recycler.getViewForPosition(position);  // 可能有溢出的异常存在
            if (view != null) {
                RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), p.width);
                int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), p.height);
                view.measure(childWidthSpec, childHeightSpec);
                measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                recycler.recycleView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "measureScrapChild: recycler.getViewForPosition is null");
        }
    }

    public LinearLayout getRecyclerViewLayout() {
        return mRecyclerViewLayout;
    }

    public void setRecyclerViewLayout(LinearLayout recyclerViewLayout) {
        mRecyclerViewLayout = recyclerViewLayout;
    }

    // 禁止recyclerview滑动
    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return false && super.canScrollVertically();
    }
}
