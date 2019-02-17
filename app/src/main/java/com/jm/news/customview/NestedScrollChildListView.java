package com.jm.news.customview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

public class NestedScrollChildListView extends ListView implements NestedScrollingChild {

    private static final String TAG = NestedScrollChildListView.class.getSimpleName();
    private final NestedScrollingChildHelper mChildHelper;
    private final int[] mScrollConsumed = new int[2];
    private final int[] mScrollOffset = new int[2];

    private int mActivePointerId = INVALID_POINTER;

    private int mNestedYOffset;

    private int mLastScrollerY;

    private int mLastMotionY;

    private int lastY = -1;

    private int oldTop = 0;


    public NestedScrollChildListView(Context context) {
        this(context, null);
    }

    public NestedScrollChildListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollChildListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MotionEvent vtev = MotionEvent.obtain(event);

        final int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }


        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = (int) event.getY();
                mActivePointerId = event.getPointerId(0);
                oldTop = getTop();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEvent.ACTION_MOVE:
                int currentTop = getTop();
                mNestedYOffset = currentTop - oldTop;
                final int activePointerIndex = event.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
                    break;
                }

                final int y = (int) event.getY(activePointerIndex);
                int deltaY = mLastMotionY - y;
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    Log.d(TAG, "onTouchEvent: deltaY : " + deltaY + " , mScrollConsumedY : " + mScrollConsumed[1] + " , mScrollOffset : " + mScrollOffset[1]);
                    vtev.offsetLocation(0, mScrollConsumed[1]);
                    deltaY -= mScrollConsumed[1];
                }
                mLastMotionY = y - mScrollOffset[1];
                Rect rect = new Rect();
                if (getLocalVisibleRect(rect)) {
                    Log.d(TAG, "onTouchEvent: rect : " + rect);
                } else {
                    Log.d(TAG, "onTouchEvent: visible rect got failed");
                }
                int consumeY = deltaY;
                if (getFirstVisiblePosition() == 0) {
                    int top = getChildAt(0).getTop();
                    if (rect.top + deltaY < top) {
                        consumeY = top - rect.top;
                    }
                } else if (getLastVisiblePosition() == getCount() - 1) {
                    int bottom = getChildAt(getChildCount() - 1).getBottom();
                    if (rect.bottom + deltaY > bottom) {
                        consumeY = bottom - rect.bottom;
                    }
                }
                if (Math.abs(consumeY) < Math.abs(deltaY)) {
                    deltaY -= consumeY;
                    Log.d(TAG, "onTouchEvent: consumeY :" + consumeY + " , deltaY : " + deltaY);
                    vtev.offsetLocation(0, consumeY);
                    if (dispatchNestedScroll(0, consumeY, 0, deltaY, mScrollOffset)) {

                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = event.getActionIndex();
                mLastMotionY = (int) event.getY(index);
                mActivePointerId = event.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                mLastMotionY = (int) event.getY(event.findPointerIndex(mActivePointerId));
                break;
            default:

                break;
        }
        return super.onTouchEvent(vtev);
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}