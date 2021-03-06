package com.jm.news.view;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jm.news.R;
import com.jm.news.activity.AboutActivity;
import com.jm.news.activity.LoginActivity;
import com.jm.news.activity.SettingActivity;
import com.jm.news.activity.UserActivity;
import com.jm.news.common.Common;
import com.jm.news.customview.MCircleImageView;
import com.jm.news.customview.MFragmentBase;
import com.jm.news.util.JumpUtils;
import com.jm.news.util.LogUtils;
import com.jm.news.viewmodel.FragmentAppMenuViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class FragmentAppMenu extends MFragmentBase {

    // static field
    private static final String TAG = "FragmentAppMenu";
    // control field
    private LinearLayout mLlUser;
    private MCircleImageView mIvIcon;
    private TextView mTvUserName;
    private TextView mTvAutograph;
    private LinearLayout mLlMessage;
    private LinearLayout mLlVip;
    private LinearLayout mLlTheme;
    private LinearLayout mLlCollected;
    private LinearLayout mLlFriends;
    private LinearLayout mLlLocation;
    private LinearLayout mLlAbout;
    private LinearLayout mLlSetting;
    private LinearLayout mLlExit;
    // function related field
    private MyClickListener mClickListener;
    private RequestOptions mGlideOptions = new RequestOptions()
            .placeholder(R.mipmap.loading_static)   // 图片加载出来前，显示的图片
            .fallback(R.mipmap.icon_user)          // url为空的时候,显示的图片
            .error(R.mipmap.icon_user);
    // viewmodel related field
    private FragmentAppMenuViewModel mViewModel;


    public FragmentAppMenu() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView: ");
        View view = getInflaterView(R.layout.layout_fragment_app_menu, inflater, container, savedInstanceState);
        if (!isRecreate()) {
            mLlUser = view.findViewById(R.id.ll_app_menu_user);
            mIvIcon = view.findViewById(R.id.miv_app_menu_user_icon);
            mTvUserName = view.findViewById(R.id.tv_app_menu_user_name);
            mTvAutograph = view.findViewById(R.id.tv_app_menu_user_autograph);
            mLlMessage = view.findViewById(R.id.ll_app_menu_message);
            mLlVip = view.findViewById(R.id.ll_app_menu_vip);
            mLlTheme = view.findViewById(R.id.ll_app_menu_theme);
            mLlCollected = view.findViewById(R.id.ll_app_menu_collected);
            mLlFriends = view.findViewById(R.id.ll_app_menu_friends);
            mLlLocation = view.findViewById(R.id.ll_app_menu_location);
            mLlAbout = view.findViewById(R.id.ll_app_menu_about);
            mLlSetting = view.findViewById(R.id.ll_app_menu_setting);
            mLlExit = view.findViewById(R.id.ll_app_menu_exit);

            mViewModel = ViewModelProviders.of(this).get(FragmentAppMenuViewModel.class);
            initview();
        }
        return view;
    }

    @Override
    public void onStart() {
        LogUtils.d(TAG, "onStart: ");
        super.onStart();
        updateView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy: ");
        mTvUserName = null;
        mTvAutograph = null;
        mIvIcon = null;
        mLlAbout = null;
        mLlCollected = null;
        mLlExit = null;
        mLlFriends = null;
        mLlLocation = null;
        mLlMessage = null;
        mLlSetting = null;
        mLlTheme = null;
        mLlUser = null;
        mLlVip = null;
        mClickListener = null;
        mViewModel = null;
        mGlideOptions = null;
        super.onDestroy();
    }

    private void initview() {
        LogUtils.d(TAG, "initview: ");
        mClickListener = new MyClickListener();
        mLlUser.setOnClickListener(mClickListener);
        mIvIcon.setOnClickListener(mClickListener);
        mTvUserName.setOnClickListener(mClickListener);
        mTvAutograph.setOnClickListener(mClickListener);
        mLlMessage.setOnClickListener(mClickListener);
        mLlVip.setOnClickListener(mClickListener);
        mLlTheme.setOnClickListener(mClickListener);
        mLlCollected.setOnClickListener(mClickListener);
        mLlFriends.setOnClickListener(mClickListener);
        mLlLocation.setOnClickListener(mClickListener);
        mLlAbout.setOnClickListener(mClickListener);
        mLlSetting.setOnClickListener(mClickListener);
        mLlExit.setOnClickListener(mClickListener);

        new QBadgeView(getContext())
                .setBadgeNumber(2)
//                .setBadgeText("")
                .setBadgeGravity(Gravity.END | Gravity.CENTER)
                .setBadgeBackgroundColor(Color.RED)
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        LogUtils.d(TAG, "onDragStateChanged: dragState=" + dragState);
                        if (dragState == Badge.OnDragStateChangedListener.STATE_SUCCEED) {
                            // TODO number icon is remove
                        }
                    }
                })
                .setBadgeTextSize(14, true)
//                .setBadgePadding(3, true)
                .setBadgePadding(5, true)
                .setShowShadow(true)
                .bindTarget(mLlMessage);

        new QBadgeView(getContext())
                .setBadgeNumber(1)
//                .setBadgeText("")
                .setBadgeGravity(Gravity.END | Gravity.CENTER)
                .setBadgeBackgroundColor(Color.RED)
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        LogUtils.d(TAG, "onDragStateChanged: dragState=" + dragState);
                        if (dragState == Badge.OnDragStateChangedListener.STATE_SUCCEED) {
                            // TODO number icon is remove
                        }
                    }
                })
                .setBadgeTextSize(14, true)
//                .setBadgePadding(3, true)
                .setBadgePadding(5, true)
                .setShowShadow(true)
                .bindTarget(mLlTheme);
    }

    /***************************** private function *********************************/
    private void updateView() {
        LogUtils.d(TAG, "updateView: ");
        if (null != mViewModel) {
            String userName = mViewModel.getAccountInfo(FragmentAppMenuViewModel.ACCOUNT_TYPE_NAME);
            String userAutograph = mViewModel.getAccountInfo(FragmentAppMenuViewModel.ACCOUNT_TYPE_AUTOGRAPH);
            String userIconUrl = mViewModel.getAccountInfo(FragmentAppMenuViewModel.ACCOUNT_TYPE_ICON);
            LogUtils.d(TAG, "updateView: name = " + userName
                    + ", userAutograph = " + userAutograph
                    + ", userIconUrl = " + userIconUrl);

            if (!TextUtils.isEmpty(userName)) {
                if ("-".equals(userName)) {
                    mTvUserName.setText(R.string.user_no_login);
                    mTvAutograph.setText(R.string.account_user_empty);
                    return;
                } else {
                    mTvUserName.setText(userName);
                }
            }

            if (!TextUtils.isEmpty(userAutograph)) {
                if ("-".equals(userAutograph)) {
                    mTvAutograph.setText(R.string.user_empty_autograph);
                } else {
                    mTvAutograph.setText(userAutograph);
                }
            }

            Glide.with(this).load(userIconUrl).apply(mGlideOptions).into(mIvIcon);
        }
    }

    /***************************** listener function *********************************/
    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_app_menu_user:
                case R.id.miv_app_menu_user_icon:
                case R.id.tv_app_menu_user_name:
                case R.id.tv_app_menu_user_autograph:
                    if (Common.getInstance().hasUser()) {
                        JumpUtils.jumpActivity(getContext(), UserActivity.class);
                    } else {
                        JumpUtils.jumpActivity(getContext(), LoginActivity.class);
                    }
                    break;
                case R.id.ll_app_menu_message:
                    break;
                case R.id.ll_app_menu_vip:
                    break;
                case R.id.ll_app_menu_theme:
                    break;
                case R.id.ll_app_menu_collected:
                    break;
                case R.id.ll_app_menu_friends:
                    break;
                case R.id.ll_app_menu_location:
                    break;
                case R.id.ll_app_menu_about:
                    JumpUtils.jumpActivity(getContext(), AboutActivity.class);
                    break;
                case R.id.ll_app_menu_setting:
                    JumpUtils.jumpActivity(getContext(), SettingActivity.class);
                    break;
                case R.id.ll_app_menu_exit:
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getString(R.string.dialog_app_menu_exit_title))
                            .setCancelText(getString(R.string.dialog_cancel))
                            .setConfirmText(getString(R.string.dialog_confirm))
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    getActivity().finish();
                                }
                            })
                            .show();
                    break;
            }
        }
    }


}
