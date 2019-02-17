package com.jm.news.customview;


import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import com.jm.news.R;
import com.youth.banner.Banner;

public class StickyNavLayout extends LinearLayout implements NestedScrollingParent {

    private int topHeight = 50;
    OverScroller mScroller;
    private Banner topview;
    private RecyclerView mRecyclerView;
     
    private Context context;

    public StickyNavLayout(Context context) {
        super(context);
        this.context = context;
        mScroller = new OverScroller(context);
    }

    public StickyNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mScroller = new OverScroller(context);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public void onStopNestedScroll(View child) {
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        boolean hiddenTop = dy > 0 && getScrollY() < topHeight;
        boolean showTop = dy < 0 && getScrollY() >= 0 && !ViewCompat.canScrollVertically(target, -1);
        if (hiddenTop || showTop) {

            if (getScrollY() < topHeight) {

            } else {
                consumed[1] = dy;
            }
            scrollBy(0, dy);
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (getScrollY() >= topHeight) return false;
        fling((int) velocityY);
        return true;
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, topHeight);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getChildAt(0).measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
        params.height = getMeasuredHeight();
        setMeasuredDimension(getMeasuredWidth(), topview.getMeasuredHeight() + mRecyclerView.getMeasuredHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        topview = findViewById(R.id.fragment_main_news_banner);
        mRecyclerView = findViewById(R.id.viewblock_recyclerView);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        topHeight = topview.getMeasuredHeight();
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > topHeight) {
            y = topHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }
}