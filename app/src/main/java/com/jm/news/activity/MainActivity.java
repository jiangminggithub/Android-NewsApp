package com.jm.news.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.customview.MSlidingPaneLayout;
import com.jm.news.customview.MViewPagerIndicator;
import com.jm.news.define.BaseActivity;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.NewsFragmentsContainer;
import com.jm.news.view.FragmentAppMenu;
import com.jm.news.view.FragmentErrorItem;
import com.jm.news.view.FragmentNewsItem;
import com.jm.news.view.FragmentNewsMain;
import com.jm.news.viewmodel.MainActivityViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private MSlidingPaneLayout mSplSlidingPane;
    private FrameLayout mFlLeftMenu;
    private LinearLayout mLlRightContent;
    private MViewPagerIndicator mPagerIndicator;
    private ViewPager mViewPager;
    private ImageButton mIbMainMenu;
    private ImageButton mIbMainSearch;
    private SlidingPaneLayoutListener mSlidingPaneListener;
    private ViewPagerListener mViewPagerListener;
    private ViewPagerAdapter mViewPagerAdapter;

    private MainActivityViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        // init layout
        mSplSlidingPane = findViewById(R.id.app_sliding_pane_layout);
        mFlLeftMenu = findViewById(R.id.fl_left_menu);
        mLlRightContent = findViewById(R.id.ll_right_content);
        mPagerIndicator = findViewById(R.id.mvpi_pager_indicator);
        mViewPager = findViewById(R.id.vp_view_pager);
        mIbMainMenu = findViewById(R.id.ib_main_menu);
        mIbMainSearch = findViewById(R.id.ib_main_search);

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        CommonUtils.getInstance().setNeedsMenuKey(this);

        // related  component bind
        mSlidingPaneListener = new SlidingPaneLayoutListener();
        mSplSlidingPane.setPanelSlideListener(mSlidingPaneListener);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPagerListener = new ViewPagerListener();
        mPagerIndicator
//                .setExpand(true)//设置tab宽度为包裹内容还是平分父控件剩余空间，默认值：false,包裹内容
//                .setIndicatorWrapText(false)//设置indicator是与文字等宽还是与整个tab等宽，默认值：true,与文字等宽
                .setIndicatorColor(Color.parseColor("#ff3300"))//indicator颜色
                .setTabTextColor(Color.parseColor("#ff999999"))//文字颜色
                .setSelectedTabTextColor(Color.parseColor("#ff3300"))//被选中的文字颜色
                .setIndicatorHeight(2)//indicator高度
//                .setShowUnderline(true, Color.parseColor("#dddddd"), 2)//设置是否展示underline，默认不展示
//                .setShowDivider(true, Color.parseColor("#dddddd"), 10, 1)//设置是否展示分隔线，默认不展示
                .setTabTextSize(16)//文字大小
                .setTabTypefaceStyle(Typeface.NORMAL)//字体样式：粗体、斜体等
//                .setSelectedTabTextSize(18)//被选中的文字大小
                .setTabPadding(10, 5, 10, 8)
                .setSelectedTabTypefaceStyle(Typeface.BOLD);
        mPagerIndicator.setViewPager(mViewPager);
        mPagerIndicator.setOnPageChangeListener(mViewPagerListener);

        mIbMainSearch.setOnClickListener(this);
        mIbMainMenu.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_left_menu, new FragmentAppMenu()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        mSplSlidingPane = null;
        mFlLeftMenu = null;
        mLlRightContent = null;
        mPagerIndicator = null;
        mViewPager = null;
        mViewPagerAdapter = null;
        mViewPagerListener = null;
        mSlidingPaneListener = null;
        NewsFragmentsContainer.Instance().clearAll();
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSplSlidingPane.isOpen()) {
                mSplSlidingPane.closePane();
            } else {
                moveTaskToBack(false);
            }
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_MENU)) {
            if (mSplSlidingPane.isOpen()) {
                mSplSlidingPane.closePane();
            } else {
                mSplSlidingPane.openPane();
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_main_menu:
                mSplSlidingPane.openPane();
                break;
            case R.id.ib_main_search:
                Common common = Common.getInstance();
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(common.getResourcesString(R.string.dialog_waring_tips))
                        .setContentText(common.getResourcesString(R.string.dialog_waring_content))
                        .setConfirmText(common.getResourcesString(R.string.dialog_confirm))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            default:
                break;
        }
    }

    /************************************** private function ****************************************************/

    /**************************************inner class****************************************************/
    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Log.d(TAG, "getItem: i=" + i);
            Fragment fragment = null;
            NewsFragmentsContainer container = NewsFragmentsContainer.Instance();
            int size = container.getFragmentHashMap().size();
            if ((i + 1) > size || null == container.getFragmentHashMap().get(i)) {
                if (i == 0) {
                    fragment = new FragmentNewsMain(i);
                } else {
                    fragment = new FragmentNewsItem(i);
                }
                container.putFragmentHashMap(i, fragment);
            }
            fragment = container.getFragmentHashMap().get(i);
            return null == fragment ? new FragmentErrorItem() : fragment;
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount: count=" + mViewModel.getChannelCount());
            return mViewModel.getChannelCount();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(TAG, "getPageTitle: position=" + position);
            return mViewModel.getChannelName(position);
        }
    }

    /*************************************** listener function *******************************************/
    private class SlidingPaneLayoutListener implements SlidingPaneLayout.PanelSlideListener {

        @Override
        public void onPanelSlide(@NonNull View view, float v) {
            Log.d(TAG, "onPanelSlide: v=" + v);
        }

        @Override
        public void onPanelOpened(@NonNull View view) {
            Log.d(TAG, "onPanelOpened");
        }

        @Override
        public void onPanelClosed(@NonNull View view) {
            Log.d(TAG, "onPanelClosed");
        }
    }

    private class ViewPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            Log.d(TAG, "onPageSelected: i=" + i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            Log.d(TAG, "onPageScrollStateChanged: i = " + i);
        }
    }


}
