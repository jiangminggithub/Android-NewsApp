package com.jm.news.entry;


import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RecyclerViewBehavior extends CoordinatorLayout.Behavior<RecyclerView> {
    private static final String TAG = "RecyclerViewBehavior";

    public RecyclerViewBehavior() {
    }

    public RecyclerViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RecyclerView child, View dependency) {
        Log.d(TAG, "layoutDependsOn: " + (dependency instanceof View));
        return dependency instanceof View;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RecyclerView child, View dependency) {
        //计算列表y坐标，最小为0
        float y = dependency.getHeight() + dependency.getTranslationY();
        Log.d(TAG, "onDependentViewChanged: y=" + y);
        if (y < 0) {
            y = 0;
        }
        child.setY(y);
        return true;
    }
}
