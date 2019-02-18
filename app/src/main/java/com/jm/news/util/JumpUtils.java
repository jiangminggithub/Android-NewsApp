package com.jm.news.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jm.news.activity.WebViewActivity;
import com.jm.news.define.DataDef;

public class JumpUtils {
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
     * @param context      当前Context
     * @param newsLink     加载的URl
     * @param isJavaScript 是否启用JavaScript
     */
    public static final void jumpWebView(@NonNull Context context, @NonNull String newsLink, boolean isJavaScript) {
        LogUtils.d(TAG, "jumpWebView: newsLink = " + newsLink);
        if (null != context) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(DataDef.WebViewKey.KEY_URL, newsLink);
            intent.putExtra(DataDef.WebViewKey.KEY_OPEN_JAVASCRIPT, isJavaScript);
            context.startActivity(intent);
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
