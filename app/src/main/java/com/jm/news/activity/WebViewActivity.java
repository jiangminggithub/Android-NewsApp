package com.jm.news.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.customview.MActivityBase;
import com.jm.news.define.DataDef;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.JumpUtils;
import com.jm.news.util.LogUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WebViewActivity extends MActivityBase implements View.OnClickListener {

    // static field
    private static final String TAG = "WebViewActivity";
    private static final int SUCCESS_PROGRESS_STATUS = 100;
    private static final int EXPAND_VIEW_TOUCH_DELEGATE = 50;
    // control field
    private TextView mTvBack;
    private TextView mTvTitle;
    private WebView mWebView;
    private ImageButton mIbMore;
    private SweetAlertDialog mDialog;
    // URL field
    private String mNewsUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_webview);

        mTvBack = findViewById(R.id.tv_head_back);
        mTvTitle = findViewById(R.id.tv_head_title);
        mWebView = findViewById(R.id.wv_webview);
        mIbMore = findViewById(R.id.ib_head_more);

        mTvBack.setOnClickListener(this);
        mTvTitle.setText(Common.getInstance().getResourcesString(R.string.app_toolbar_title_webview_detail));

        mIbMore.setVisibility(View.VISIBLE);
        mIbMore.setOnClickListener(this);
        CommonUtils.getInstance().expandViewTouchDelegate(mIbMore, EXPAND_VIEW_TOUCH_DELEGATE, EXPAND_VIEW_TOUCH_DELEGATE, EXPAND_VIEW_TOUCH_DELEGATE, EXPAND_VIEW_TOUCH_DELEGATE);

        Intent intent = getIntent();
        mNewsUrl = intent.getStringExtra(DataDef.WebViewKey.KEY_URL);
        boolean isOpenJavaScript = intent.getBooleanExtra(DataDef.WebViewKey.KEY_OPEN_JAVASCRIPT, false);
        LogUtils.d(TAG, "onCreate: mNewsUrl = " + mNewsUrl + ", isOpenJavaScript = " + isOpenJavaScript);

        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mDialog.setContentText(Common.getInstance().getResourcesString(R.string.dialog_webview_loading));
        mDialog.setCancelable(true);
        mDialog.show();

        if (!TextUtils.isEmpty(mNewsUrl)) {
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(isOpenJavaScript);
            settings.setJavaScriptCanOpenWindowsAutomatically(isOpenJavaScript);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebView.setWebViewClient(new MyWebViewClient());
            mWebView.setWebChromeClient(new MyWebChromeClient());
            mWebView.loadUrl(mNewsUrl);
        } else {
            mDialog.dismiss();
            CommonUtils.getInstance().showToastView(R.string.toast_webview_no_url);
        }

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if (null != mWebView) {
            mWebView.clearCache(true);
        }
        mTvBack = null;
        mTvTitle = null;
        mIbMore = null;
        mWebView = null;
        mDialog = null;
        mNewsUrl = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_head_back:
                this.finish();
                break;
            case R.id.ib_head_more: {
                PopupMenu popupMenu = CommonUtils.getInstance().getPopupMenu(this, v, R.menu.menu_popup_webview_more, false);
                popupMenu.setOnMenuItemClickListener(new MyPopMenuClickListener());
                popupMenu.show();
            }
            break;
            default:
                break;
        }
    }

    /********************************** listener function ********************************************/
    private class MyPopMenuClickListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_webview_refresh:
                    mWebView.loadUrl(mNewsUrl);
                    break;
                case R.id.menu_webview_share:
                    if (!TextUtils.isEmpty(mNewsUrl)) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText(null, mNewsUrl));
                        CommonUtils.getInstance().showToastView(R.string.toast_menu_webview_share_tips);
                    }
                    break;
                case R.id.menu_webview_open_other:
                    JumpUtils.jumpOtherApp(WebViewActivity.this, mNewsUrl);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    /********************************** inner class ********************************************/
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                // 拦截自定义协议的请求导致访问失败
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    // 让系统寻找可处理的程序处理
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
                    return true;
                }
            } catch (Exception e) {     //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return true;            //没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
            }

            view.loadUrl(url);
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.getSettings()
                        .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (null != mDialog) {
                mDialog.dismiss();
            }
            LogUtils.d(TAG, "onPageFinished: ");
        }

    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            LogUtils.d(TAG, "onProgressChanged: progress=" + newProgress);
            if (null != mDialog) {
                if (newProgress < SUCCESS_PROGRESS_STATUS) {
                    mDialog.setTitleText(newProgress + "%");
                } else {
                    mDialog.dismiss();
                }
            }
        }
    }

}
