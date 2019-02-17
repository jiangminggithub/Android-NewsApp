package com.jm.news.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jm.news.R;
import com.jm.news.activity.LoginActivity;
import com.jm.news.activity.SettingActivity;
import com.jm.news.activity.UserActivity;
import com.jm.news.common.Common;
import com.jm.news.customview.MCircleImageViewBase;
import com.jm.news.customview.MFragmentBase;
import com.jm.news.viewmodel.FragmentAppMenuViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class FragmentAppMenu extends MFragmentBase {

    private static final String TAG = "FragmentAppMenu";
    private LinearLayout mUser;
    private MCircleImageViewBase mIcon;
    private TextView mTvUserName;
    private TextView mTvAutograph;
    private LinearLayout mMessage;
    private LinearLayout mVip;
    private LinearLayout mTheme;
    private LinearLayout mCollected;
    private LinearLayout mFriends;
    private LinearLayout mLoacation;
    private LinearLayout mAbout;
    private LinearLayout mSetting;
    private LinearLayout mExit;

    private MyClickListener clickListener;

    private FragmentAppMenuViewModel mViewModel;

    public FragmentAppMenu() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getInflaterView(R.layout.layout_fragment_app_menu, inflater, container, savedInstanceState);
        if (!isRecreate()) {
            mUser = view.findViewById(R.id.app_menu_user);
            mIcon = view.findViewById(R.id.app_menu_user_icon);
            mTvUserName = view.findViewById(R.id.app_menu_user_name);
            mTvAutograph = view.findViewById(R.id.app_menu_user_autograph);
            mMessage = view.findViewById(R.id.app_menu_message);
            mVip = view.findViewById(R.id.app_menu_vip);
            mTheme = view.findViewById(R.id.app_menu_theme);
            mCollected = view.findViewById(R.id.app_menu_collected);
            mFriends = view.findViewById(R.id.app_menu_friends);
            mLoacation = view.findViewById(R.id.app_menu_location);
            mAbout = view.findViewById(R.id.app_menu_about);
            mSetting = view.findViewById(R.id.app_menu_setting);
            mExit = view.findViewById(R.id.app_menu_exit);

            mViewModel = ViewModelProviders.of(this).get(FragmentAppMenuViewModel.class);

            initview();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        clickListener = null;
        super.onDestroy();
    }

    private void initview() {
        clickListener = new MyClickListener();
        mUser.setOnClickListener(clickListener);
        mIcon.setOnClickListener(clickListener);
        mTvUserName.setOnClickListener(clickListener);
        mTvAutograph.setOnClickListener(clickListener);
        mMessage.setOnClickListener(clickListener);
        mVip.setOnClickListener(clickListener);
        mTheme.setOnClickListener(clickListener);
        mCollected.setOnClickListener(clickListener);
        mFriends.setOnClickListener(clickListener);
        mLoacation.setOnClickListener(clickListener);
        mAbout.setOnClickListener(clickListener);
        mSetting.setOnClickListener(clickListener);
        mExit.setOnClickListener(clickListener);

        new QBadgeView(getContext())
                .setBadgeNumber(2)
//                .setBadgeText("")
                .setBadgeGravity(Gravity.END | Gravity.CENTER)
                .setBadgeBackgroundColor(Color.RED)
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        Log.d(TAG, "onDragStateChanged: dragState=" + dragState);
                        if (dragState == Badge.OnDragStateChangedListener.STATE_SUCCEED) {
                            Toast.makeText(getContext(), "已取消查看", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setBadgeTextSize(14, true)
//                .setBadgePadding(3, true)
                .setBadgePadding(5, true)
                .setShowShadow(true)
                .bindTarget(mMessage);

        new QBadgeView(getContext())
                .setBadgeNumber(1)
//                .setBadgeText("")
                .setBadgeGravity(Gravity.END | Gravity.CENTER)
                .setBadgeBackgroundColor(Color.RED)
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        Log.d(TAG, "onDragStateChanged: dragState=" + dragState);
                        if (dragState == Badge.OnDragStateChangedListener.STATE_SUCCEED) {
                            Toast.makeText(getContext(), "已取消查看", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setBadgeTextSize(14, true)
//                .setBadgePadding(3, true)
                .setBadgePadding(5, true)
                .setShowShadow(true)
                .bindTarget(mTheme);
    }


    private void updateView() {
        if (null != mViewModel) {
            String userName = mViewModel.getAccountInfo(FragmentAppMenuViewModel.ACCOUNT_TYPE_NAME);
            String userAutograph = mViewModel.getAccountInfo(FragmentAppMenuViewModel.ACCOUNT_TYPE_AUTOGRAPH);
            Log.d(TAG, "updateView: name = " + userName + ", userAutograph = " + userAutograph);
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
        }
    }


    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.app_menu_user:
                case R.id.app_menu_user_icon:
                case R.id.app_menu_user_name:
                case R.id.app_menu_user_autograph:
                    if (Common.getInstance().hasUser()) {
                        intent = new Intent(getContext(), UserActivity.class);
                    } else {
                        intent = new Intent(getContext(), LoginActivity.class);
                    }
                    startActivity(intent);
                    break;
                case R.id.app_menu_message:
                    break;
                case R.id.app_menu_vip:
                    break;
                case R.id.app_menu_theme:
                    break;
                case R.id.app_menu_collected:
                    break;
                case R.id.app_menu_friends:
                    break;
                case R.id.app_menu_location:
                    break;
                case R.id.app_menu_about:
                    break;
                case R.id.app_menu_setting:
                    intent = new Intent(getContext(), SettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.app_menu_exit:
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(Common.getInstance().getResourcesString(R.string.app_menu_dialog_exit_title))
                            .setCancelText(Common.getInstance().getResourcesString(R.string.dialog_cancel))
                            .setConfirmText(Common.getInstance().getResourcesString(R.string.dialog_confirm))
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    getActivity().finish();
                                }
                            })
                            .show();
                    break;
            }
        }
    }


}
