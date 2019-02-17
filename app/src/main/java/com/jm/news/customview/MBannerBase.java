package com.jm.news.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.youth.banner.Banner;


public class MBannerBase extends Banner {
    private static final String TAG = "MBannerBase";
    private float lastY;

    public MBannerBase(Context context) {
        super(context);
    }

    public MBannerBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MBannerBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float lastY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getY() - lastY < 0) {
                Log.d(TAG, "onTouchEvent: ----------------------------------------------");
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
