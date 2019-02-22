package com.jm.news.customview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jm.news.util.LogUtils;

public class MFragmentBase extends Fragment {

    // static field
    private static final String TAG = "MFragmentBase";
    // function related field
    private View mView = null;
    private boolean recreate = false;


    @Override
    public void onDestroy() {
        mView = null;
        super.onDestroy();
    }

    /**
     * 加载可重复使用的View
     *
     * @param resource           加载的resourceID
     * @param inflater           inflater 对象
     * @param container          container 对象
     * @param savedInstanceState savedInstanceState 对象
     * @return
     */
    public View getInflaterView(@NonNull int resource, @NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "getInflaterView: ");
        if (null == mView) {
            mView = inflater.inflate(resource, container, false);
            recreate = false;
            LogUtils.d(TAG, "getInflaterView: view create");
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (null != parent) {
                parent.removeView(mView);
            }
            recreate = true;
            LogUtils.d(TAG, "getInflaterView: view recycler");
        }
        return mView;
    }

    /**
     * 是否是重新创建的View
     *
     * @return
     */
    public boolean isRecreate() {
        return recreate;
    }
}
