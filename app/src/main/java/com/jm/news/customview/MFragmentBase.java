package com.jm.news.customview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MFragmentBase extends Fragment {
    private static final String TAG = "MFragmentBase";
    private View mView = null;
    private boolean recreate = false;

    @Override
    public void onDestroy() {
        mView = null;
        super.onDestroy();
    }

    public View getInflaterView(@NonNull int resource, @NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "getInflaterView: ");
        if (null == mView) {
            mView = inflater.inflate(resource, container, false);
            recreate = false;
            Log.d(TAG, "getInflaterView: view create");
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (null != parent) {
                parent.removeView(mView);
            }
            recreate = true;
            Log.d(TAG, "getInflaterView: view recycler");
        }
        return mView;
    }

    public boolean isRecreate() {
        return recreate;
    }
}
