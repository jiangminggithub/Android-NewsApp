package com.jm.news.customview;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.jm.news.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public class MRefreshLayout extends SmartRefreshLayout implements NestedScrollingParent {
    private int mTopViewHeight;
    private View mTop;

    public MRefreshLayout(Context context) {
        super(context);
    }

    public MRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes)
    {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed)
    {
        boolean hiddenTop = dy > 0 && getScrollY() < mTopViewHeight;
        boolean showTop = dy < 0 && getScrollY() > 0 && !ViewCompat.canScrollVertically(target, -1);

        if (hiddenTop || showTop)
        {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY)
    {
        if (getScrollY() >= mTopViewHeight) return false;
        fling((int) velocityY);
        return true;
    }

    public void fling(int velocityY)
    {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
    }

    @Override
    public void scrollTo(int x, int y)
    {
        if (y < 0)
        {
            y = 0;
        }
        if (y > mTopViewHeight)
        {
            y = mTopViewHeight;
        }
        if (y != getScrollY())
        {
            super.scrollTo(x, y);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTop=findViewById(R.id.fragment_main_news_banner);
    }
}
