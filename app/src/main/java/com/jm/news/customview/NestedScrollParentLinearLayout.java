package com.jm.news.customview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class NestedScrollParentLinearLayout extends LinearLayout implements NestedScrollingParent {

    private static final String TAG = NestedScrollParentLinearLayout.class.getSimpleName();
    private final NestedScrollingParentHelper mParentHelper;

    public NestedScrollParentLinearLayout(Context context) {
        this(context, null);
    }

    public NestedScrollParentLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollParentLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);

    }

    @Override
    public void onStopNestedScroll(View target) {
        mParentHelper.onStopNestedScroll(target);

    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
                               int dyUnconsumed) {
        int scrollY = getScrollY();
        int step;
        if (scrollY + dyUnconsumed < 0) {
            step = -scrollY;
        } else {
            int height = getChildAt(getChildCount() - 1).getBottom();
            Rect rect = new Rect();
            getLocalVisibleRect(rect);
            int visibleHeight = rect.bottom;
            if (visibleHeight < height && dyUnconsumed > 0) {
                step = Math.min(dyUnconsumed, height - visibleHeight);
            } else if (rect.top > 0 && dyUnconsumed < 0) {
                step = Math.max(dyUnconsumed, -rect.top);
            } else {
                step = 0;
            }
        }
        scrollBy(0, step);
        Log.d(TAG, "onNestedScroll: Y scrollBy : " + step);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        int targetTop = target.getTop();
        int targetBottom = target.getBottom();
        int scrollY = getScrollY();
        int currentTargetBottom = targetBottom - scrollY;
        if (scrollY < targetTop && dy > 0 && scrollY >= 0) {
            consumed[0] = 0;
            consumed[1] = Math.min(dy, targetTop - scrollY);
            Log.d(TAG, "onNestedPreScroll: Y scrollBy : " + consumed[1]);
        } else if (currentTargetBottom < getBottom() && dy < 0) {
            consumed[0] = 0;
            consumed[1] = Math.max(dy, currentTargetBottom - getBottom());
        } else {
            consumed[0] = 0;
            consumed[1] = 0;
        }
        scrollBy(0, consumed[1]);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (!consumed) {

            return true;
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }
}