package com.jm.news.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jm.news.activity.WebViewActivity;
import com.jm.news.define.DataDef;

public class JumpUtils {

    // static field
    private static final String TAG = "JumpUtils";

    /**
     * Activity的跳转
     *
     * @param context 当前Context
     * @param clazz   跳转的Activity
     */
    public static final void jumpActivity(@NonNull Context context, @NonNull Class clazz) {
        if (null != context && null != clazz) {
            Intent intent = new Intent(context, clazz);
            context.startActivity(intent);
        }
    }

    /**
     * 跳转到WebViewActivity
     *
     * @param activity     当前Context
     * @param newsLink     加载的URl
     * @param isJavaScript 是否启用JavaScript
     */
    public static final void jumpWebView(@NonNull Activity activity, @NonNull String newsLink, boolean isJavaScript) {
        LogUtils.d(TAG, "jumpWebView: newsLink = " + newsLink);
        if (null != activity) {
            if (CommonUtils.getInstance().isNetworkAvailable()) {
                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra(DataDef.WebViewKey.KEY_URL, newsLink);
                intent.putExtra(DataDef.WebViewKey.KEY_OPEN_JAVASCRIPT, isJavaScript);
                activity.startActivity(intent);
            } else {
                CommonUtils.getInstance().showNetInvisibleDialog(activity);
                LogUtils.d(TAG, "jumpWebView: context = " + activity.toString());
            }
        }
    }

    /**
     * 通过其他应用打开网络链接
     *
     * @param context 当前Context
     * @param url     URL
     */
    public static final void jumpOtherApp(@NonNull Context context, @NonNull String url) {
        LogUtils.d(TAG, "jumpOtherApp: url = " + url);
        if (null != context && !TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }
    }
}
