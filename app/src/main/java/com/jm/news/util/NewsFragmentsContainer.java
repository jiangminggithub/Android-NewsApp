package com.jm.news.util;

import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
 * Fragments manager with News Fragment
 * created by jiangming on 19-01-14
 */
public class NewsFragmentsContainer {
    private static final String TAG = "FragmentContainer";
    private static NewsFragmentsContainer mInstance = null;
    private HashMap<Integer, Fragment> fragments = new HashMap<>();

    private NewsFragmentsContainer() {
    }

    public static synchronized NewsFragmentsContainer Instance() {
        if (null == mInstance) {
            mInstance = new NewsFragmentsContainer();
            LogUtils.d(TAG, "Instance: create");
        }
        return mInstance;
    }

    public HashMap<Integer, Fragment> getFragmentHashMap() {
        LogUtils.d(TAG, "getFragmentHashMap:");
        return fragments;
    }

    public void putFragmentHashMap(Integer index, Fragment fragment) {
        LogUtils.d(TAG, "putFragmentHashMap: index = " + index);
        this.fragments.put(index, fragment);
    }

    public void clearAll() {
        fragments.clear();
        mInstance = null;
        LogUtils.d(TAG, "clearAll: ");
    }

    public void clearFragments() {
        fragments.clear();
        LogUtils.d(TAG, "clearFragments: ");
    }
}
