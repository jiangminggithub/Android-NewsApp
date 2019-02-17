package com.jm.news.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.define.BaseActivity;
import com.jm.news.view.FragmentSetting;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvBack;
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mTvBack = findViewById(R.id.tv_head_back);
        mTvTitle = findViewById(R.id.tv_head_title);
        mTvBack.setOnClickListener(this);
        mTvTitle.setText(Common.getInstance().getResourcesString(R.string.app_toobar_title_setting));
        getFragmentManager().beginTransaction().replace(R.id.fl_app_setting_content, new FragmentSetting()).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_head_back:
                this.finish();
                break;
            default:
                break;
        }
    }
}
