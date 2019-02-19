package com.jm.news.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.customview.MActivityBase;
import com.jm.news.customview.MSlidingPaneLayout;
import com.jm.news.customview.MViewPagerIndicator;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.LogUtils;
import com.jm.news.util.NewsFragmentsContainer;
import com.jm.news.view.FragmentAppMenu;
import com.jm.news.view.FragmentErrorItem;
import com.jm.news.view.FragmentNewsItem;
import com.jm.news.view.FragmentNewsMain;
import com.jm.news.viewmodel.MainActivityViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends MActivityBase implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int MAIN_FRAGMENT_INDEX = 0;
    private static final int INDICATOR_HEIGHT = 2;
    private static final int INDICATOR_TEXT_SIZE = 16;
    private static final int INDICATOR_PADDING_LEFT = 10;
    private static final int INDICATOR_PADDING_TOP = 5;
    private static final int INDICATOR_PADDING_RIGHT = 10;
    private static final int INDICATOR_PADDING_BOTTOM = 8;

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

    private NetworkConnectReceiver mReceiver;
    private MainActivityViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
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
//                .setExpand(true)  // 设置tab宽度为包裹内容还是平分父控件剩余空间，默认值：false,包裹内容
//                .setIndicatorWrapText(false)  // 设置indicator是与文字等宽还是与整个tab等宽，默认值：true,与文字等宽
                .setIndicatorColor(Color.parseColor("#ff3300")) //   indicator颜色
                .setTabTextColor(Color.parseColor("#ff999999")) //   文字颜色
                .setSelectedTabTextColor(Color.parseColor("#ff3300"))  // 被选中的文字颜色
                .setIndicatorHeight(INDICATOR_HEIGHT)   // indicator高度
//                .setShowUnderline(true, Color.parseColor("#dddddd"), 2)   // 设置是否展示underline，默认不展示
//                .setShowDivider(true, Color.parseColor("#dddddd"), 10, 1) //   设置是否展示分隔线，默认不展示
                .setTabTextSize(INDICATOR_TEXT_SIZE)    //  文字大小
                .setTabTypefaceStyle(Typeface.NORMAL)   // 字体样式：粗体、斜体等
//                .setSelectedTabTextSize(18)   // 被选中的文字大小
                .setTabPadding(INDICATOR_PADDING_LEFT, INDICATOR_PADDING_TOP, INDICATOR_PADDING_RIGHT, INDICATOR_PADDING_BOTTOM)
                .setSelectedTabTypefaceStyle(Typeface.BOLD);
        mPagerIndicator.setViewPager(mViewPager);
        mPagerIndicator.setOnPageChangeListener(mViewPagerListener);

        mIbMainSearch.setOnClickListener(this);
        mIbMainMenu.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_left_menu, new FragmentAppMenu()).commit();

        // 监听网络变化
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkConnectReceiver();
        this.registerReceiver(mReceiver, filter);
        LogUtils.d(TAG, "onCreate: AppReceiver is register");

    }

    @Override
    protected void onStart() {
        LogUtils.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onStop() {
        LogUtils.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy: ");
        NewsFragmentsContainer.Instance().clearAll();
        this.unregisterReceiver(mReceiver);
        mSplSlidingPane = null;
        mFlLeftMenu = null;
        mLlRightContent = null;
        mPagerIndicator = null;
        mViewPager = null;
        mViewPagerAdapter = null;
        mViewPagerListener = null;
        mSlidingPaneListener = null;
        mReceiver = null;
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

    /*************************************** listener function *******************************************/
    private class SlidingPaneLayoutListener implements SlidingPaneLayout.PanelSlideListener {

        @Override
        public void onPanelSlide(@NonNull View view, float v) {
            LogUtils.d(TAG, "onPanelSlide: v=" + v);
        }

        @Override
        public void onPanelOpened(@NonNull View view) {
            LogUtils.d(TAG, "onPanelOpened");
            FragmentNewsMain fragment = (FragmentNewsMain) NewsFragmentsContainer.Instance().getFragmentHashMap().get(MAIN_FRAGMENT_INDEX);
            if (null != fragment) {
                fragment.setBannerAutoPaly(false);
            }
        }

        @Override
        public void onPanelClosed(@NonNull View view) {
            LogUtils.d(TAG, "onPanelClosed");
            FragmentNewsMain fragment = (FragmentNewsMain) NewsFragmentsContainer.Instance().getFragmentHashMap().get(MAIN_FRAGMENT_INDEX);
            if (null != fragment) {
                fragment.setBannerAutoPaly(true);
            }
        }
    }

    private class ViewPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            LogUtils.d(TAG, "onPageSelected: i=" + i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            LogUtils.d(TAG, "onPageScrollStateChanged: i = " + i);
        }
    }

    /**************************************inner class****************************************************/
    private class ViewPagerAdapter extends FragmentPagerAdapter {


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            LogUtils.d(TAG, "getItem: i=" + i);
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
            LogUtils.d(TAG, "getCount: count=" + mViewModel.getChannelCount());
            return mViewModel.getChannelCount();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            LogUtils.d(TAG, "getPageTitle: position=" + position);
            return mViewModel.getChannelName(position);
        }

    }

    /**
     * 网络连接状态监听
     */
    private class NetworkConnectReceiver extends BroadcastReceiver {
        private static final String TAG = "NetworkConnectReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            LogUtils.d(TAG, "NetworkConnectReceiver: intent.getAction() = " + intent.getAction());
            // 监听网络连接
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                // 获取联网状态的NetworkInfo对象
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (null != info) {
                    // 网络未连接或者网络不可用
                    if (info.getState() != NetworkInfo.State.CONNECTED || !info.isAvailable()) {
                        LogUtils.d(TAG, "NetworkConnectReceiver: NetWork is invisible!");
                        CommonUtils.getInstance().showNetInvisibleDialog(MainActivity.this);
                    } else {    // 如果当前的网络连接成功并且网络连接可用
                        LogUtils.d(TAG, "NetworkConnectReceiver: NetWork is visible!");
                        CommonUtils.getInstance().dismissNetInvisibleDialog();
                    }

                }
            }
        }


    }
}
