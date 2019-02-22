package com.jm.news.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.define.DataDef;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.LogUtils;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    // static field
    private static final String TAG = "AboutActivity";
    private static final int GITHUB_CN_SPAN_START = 11;
    private static final int GITHUB_CN_SPAN_END = 17;
    private static final int GITHUB_EN_SPAN_START = 48;
    private static final int GITHUB_EN_SPAN_END = 54;
    private static final int DOWNLOAD_CN_SPAN_START = 10;
    private static final int DOWNLOAD_CN_SPAN_END = 14;
    private static final int DOWNLOAD_EN_SPAN_START = 44;
    private static final int DOWNLOAD_EN_SPAN_END = 52;
    private static final int DEFAULT_SPAN_VALUE = 0;
    // control field
    private TextView mTvBack;
    private TextView mTvTitle;
    private TextView mTvVersion;
    private TextView mTvGitHub;
    private TextView mTvDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_about);

        mTvBack = findViewById(R.id.tv_head_back);
        mTvTitle = findViewById(R.id.tv_head_title);
        mTvVersion = findViewById(R.id.tv_about_version);
        mTvGitHub = findViewById(R.id.tv_about_github);
        mTvDownload = findViewById(R.id.tv_about_download);

        mTvTitle.setText(R.string.about_title);
        mTvBack.setOnClickListener(this);
        mTvVersion.setText(CommonUtils.getInstance().getVersionName());

        SpannableString spGitHub = new SpannableString(getString(R.string.about_support_content_one));
        SpannableString spDownload = new SpannableString(getString(R.string.about_support_content_two));

        int gitSpanStart = DEFAULT_SPAN_VALUE;
        int gitSpanEnd = DEFAULT_SPAN_VALUE;
        int downloadSpanStart = DEFAULT_SPAN_VALUE;
        int downloadSpanEnd = DEFAULT_SPAN_VALUE;

        Locale locale = Common.getInstance().getLocale();
        LogUtils.d(TAG, "onCreate: locale = " + locale);
        if (Locale.CHINA.getCountry().equals(locale.getCountry())) {
            gitSpanStart = GITHUB_CN_SPAN_START;
            gitSpanEnd = GITHUB_CN_SPAN_END;
            downloadSpanStart = DOWNLOAD_CN_SPAN_START;
            downloadSpanEnd = DOWNLOAD_CN_SPAN_END;
        } else {
            gitSpanStart = GITHUB_EN_SPAN_START;
            gitSpanEnd = GITHUB_EN_SPAN_END;
            downloadSpanStart = DOWNLOAD_EN_SPAN_START;
            downloadSpanEnd = DOWNLOAD_EN_SPAN_END;
        }

        spGitHub.setSpan(new URLSpan(DataDef.AppInfo.APP_GITHUB_LINK), gitSpanStart, gitSpanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spDownload.setSpan(new URLSpan(DataDef.AppInfo.APP_DOWNLOAD_LINK), downloadSpanStart, downloadSpanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvGitHub.setText(spGitHub);
        mTvGitHub.setLinkTextColor(getResources().getColor(R.color.web_link_color));
        mTvGitHub.setLinksClickable(true);
        mTvGitHub.setMovementMethod(LinkMovementMethod.getInstance());

        mTvDownload.setText(spDownload);
        mTvDownload.setLinkTextColor(getResources().getColor(R.color.web_link_color));
        mTvDownload.setLinksClickable(true);
        mTvDownload.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_head_back) {
            this.finish();
        }
    }
}
