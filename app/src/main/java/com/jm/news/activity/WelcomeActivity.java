package com.jm.news.activity;

import android.os.Bundle;

import com.jm.news.R;
import com.jm.news.customview.MActivityBase;

public class WelcomeActivity extends MActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
}
