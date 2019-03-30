package com.jm.news.activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.customview.MActivityBase;
import com.jm.news.customview.MCircleImageView;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.JumpUtils;
import com.jm.news.util.LogUtils;
import com.jm.news.util.RealPathFromUriUtils;
import com.jm.news.viewmodel.UserActivityViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserActivity extends MActivityBase implements View.OnClickListener {

    // static field
    private static final String TAG = "UserActivity";
    private static final int REQUEST_IMAGE_CODE = 0;
    private static final int DIALOG_TITLE_SIZE = 20;
    private static final int DIALOG_TITLE_PADDING_LEFT = 10;
    private static final int DIALOG_TITLE_PADDING_TOP = 20;
    private static final int DIALOG_TITLE_PADDING_RIGHT = 10;
    private static final int DIALOG_TITLE_PADDING_BOTTOM = 10;
    private static final int DIALOG_EDIT_MAX_LINE = 5;
    private static final int USER_NAME_LENGTH = 20;
    private static final int USER_AUTOGRAPH_LENGTH = 80;
    private static final int USER_ADDRESS_LENGTH = 100;
    private static final int USER_PHONE_LENGTH = 25;
    private static final int USER_PROFILE_LENGTH = 120;
    // control field
    private TextView mTvBack;
    private TextView mTvTitle;
    private MCircleImageView mCivUserIcon;
    private LinearLayout mLlUserHead;
    private LinearLayout mLlAutograph;
    private LinearLayout mLlUserNickName;
    private LinearLayout mLlUserSex;
    private LinearLayout mLlUserAddress;
    private LinearLayout mLlUserPhone;
    private LinearLayout mLlUserHobby;
    private LinearLayout mLlUserProfile;
    private LinearLayout mLlUserMore;
    private LinearLayout mLlUserAccount;
    private TextView mTvNameInfo;
    private TextView mTvAutographInfo;
    private TextView mTvNickNameInfo;
    private TextView mTvSexInfo;
    private TextView mTvAddressInfo;
    private TextView mTvPhoneInfo;
    private TextView mTvHobbyInfo;
    private TextView mTvProfileInfo;
    // function related filed
    private RequestOptions mGlideOptions = new RequestOptions()
            .placeholder(R.mipmap.loading_static)   // 图片加载出来前，显示的图片
            .fallback(R.mipmap.icon_user)          // url为空的时候,显示的图片
            .error(R.mipmap.icon_user);
    // viewmodel related field
    private UserActivityViewModel mViewModel;
    private UserIconStatusObserver mUserIconStatusObserver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_user);

        mTvBack = findViewById(R.id.tv_head_back);
        mTvTitle = findViewById(R.id.tv_head_title);

        mCivUserIcon=findViewById(R.id.miv_user_icon);
        mLlUserHead = findViewById(R.id.ll_user_header);
        mLlAutograph = findViewById(R.id.ll_user_autograph);
        mLlUserNickName = findViewById(R.id.ll_user_nickname);
        mLlUserSex = findViewById(R.id.ll_user_sex);
        mLlUserAddress = findViewById(R.id.ll_user_address);
        mLlUserPhone = findViewById(R.id.ll_user_phone);
        mLlUserHobby = findViewById(R.id.ll_user_hobby);
        mLlUserProfile = findViewById(R.id.ll_user_profile);
        mLlUserMore = findViewById(R.id.ll_user_more);
        mLlUserAccount = findViewById(R.id.ll_user_account);

        mTvNameInfo = findViewById(R.id.tv_user_name);
        mTvAutographInfo = findViewById(R.id.tv_user_autograph);
        mTvNickNameInfo = findViewById(R.id.tv_user_nickname);
        mTvSexInfo = findViewById(R.id.tv_user_sex);
        mTvAddressInfo = findViewById(R.id.tv_user_address);
        mTvPhoneInfo = findViewById(R.id.tv_user_Phone);
        mTvHobbyInfo = findViewById(R.id.tv_user_hobby);
        mTvProfileInfo = findViewById(R.id.tv_user_profile);

        mViewModel = ViewModelProviders.of(this).get(UserActivityViewModel.class);
        mViewModel.initialized();
        mUserIconStatusObserver = new UserIconStatusObserver();
        mViewModel.getUserIconUpdateStatus().observe(this, mUserIconStatusObserver);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart: ");
        updateView();
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy: ");
        mTvBack = null;
        mTvTitle = null;
        mLlUserHead = null;
        mLlAutograph = null;
        mLlUserNickName = null;
        mLlUserSex = null;
        mLlUserAddress = null;
        mLlUserPhone = null;
        mLlUserHobby = null;
        mLlUserProfile = null;
        mLlUserMore = null;
        mLlUserAccount = null;
        mTvNameInfo = null;
        mTvAutographInfo = null;
        mTvNickNameInfo = null;
        mTvSexInfo = null;
        mTvAddressInfo = null;
        mTvPhoneInfo = null;
        mTvHobbyInfo = null;
        mTvProfileInfo = null;
        mUserIconStatusObserver = null;
        mViewModel = null;
        mGlideOptions = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LogUtils.d(TAG, "onActivityResult: requestCode = " + requestCode
                + ", resultCode = " + resultCode
                + ", data = " + data);
        if (requestCode == REQUEST_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            if (null != data) {
                Uri dataUri = data.getData();
                String imageUrl = RealPathFromUriUtils.getRealPathFromUri(this, dataUri);
                LogUtils.d(TAG, "onActivityResult: imageUrl = " + imageUrl);
                if (!TextUtils.isEmpty(imageUrl) && null != mViewModel) {
                    mViewModel.setUserIconImage(imageUrl);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        LogUtils.d(TAG, "onClick: userID = " + Common.getInstance().getUser());
        if (v.getId() != R.id.tv_head_back && v.getId() != R.id.ll_user_header && !Common.getInstance().hasUser()) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setContentText(getString(R.string.dialog_no_login))
                    .setConfirmText(getString(R.string.dialog_login))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            jumpLoginActivity();
                        }
                    })
                    .setCancelText(getString(R.string.dialog_cancel))
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .show();
            return;
        }
        switch (v.getId()) {
            case R.id.tv_head_back:
                this.finish();
                break;
            case R.id.miv_user_icon:
                selectIconImage();
                break;
            case R.id.ll_user_header:
                if (Common.getInstance().hasUser()) {
                    showEditDialog(R.string.dialog_user_name_title, R.string.dialog_user_name_hint, UserActivityViewModel.UserInfo.USER_NAME);
                } else {
                    jumpLoginActivity();
                }
                break;
            case R.id.ll_user_autograph:
                showEditDialog(R.string.dialog_user_autograph_title, R.string.dialog_user_autograph_hint, UserActivityViewModel.UserInfo.USER_AUTOGRAPH);
                break;
            case R.id.ll_user_nickname:
                showEditDialog(R.string.dialog_user_nickname_title, R.string.dialog_user_nickname_hint, UserActivityViewModel.UserInfo.USER_NICKNAME);
                break;
            case R.id.ll_user_sex:
                showSexChoiceDialog(R.string.dialog_user_sex_title, UserActivityViewModel.UserInfo.USER_SEX);
                break;
            case R.id.ll_user_address:
                showEditDialog(R.string.dialog_user_address_title, R.string.dialog_user_address_hint, UserActivityViewModel.UserInfo.USER_ADDRESS);
                break;
            case R.id.ll_user_phone:
                showEditDialog(R.string.dialog_user_phone_title, R.string.dialog_user_phone_hint, UserActivityViewModel.UserInfo.USER_PHONE);
                break;
            case R.id.ll_user_hobby:
                showHobbyChoiceDialog(R.string.dialog_user_hobby_title, UserActivityViewModel.UserInfo.USER_HOBBY);
                break;
            case R.id.ll_user_profile:
                showEditDialog(R.string.dialog_user_profile_title, R.string.dialog_user_profile_hint, UserActivityViewModel.UserInfo.USER_PROFILE);
                break;
            case R.id.ll_user_account:
                showAccountManagerDialog();
                break;
            case R.id.ll_user_more:
            default:
                CommonUtils.showFunctionNotOpenDialog(UserActivity.this);
                break;
        }
    }


    /************************************** observer function ****************************************************/
    private class UserIconStatusObserver implements Observer<Boolean> {

        @Override
        public void onChanged(@Nullable Boolean isSuccess) {
            LogUtils.d(TAG, "onChanged: isSuccess = " + isSuccess);
            if (isSuccess) {
                updateView();
            } else {
                CommonUtils.getInstance().showToastView(R.string.toast_setting_local_set_failed);
            }
        }
    }

    /************************************** private function ****************************************************/
    private void initView() {
        LogUtils.d(TAG, "initView: ");
        mTvBack.setOnClickListener(this);
        mCivUserIcon.setOnClickListener(this);
        mLlUserHead.setOnClickListener(this);
        mLlAutograph.setOnClickListener(this);
        mLlUserNickName.setOnClickListener(this);
        mLlUserSex.setOnClickListener(this);
        mLlUserAddress.setOnClickListener(this);
        mLlUserPhone.setOnClickListener(this);
        mLlUserHobby.setOnClickListener(this);
        mLlUserProfile.setOnClickListener(this);
        mLlUserMore.setOnClickListener(this);
        mLlUserAccount.setOnClickListener(this);
        mTvTitle.setText(getString(R.string.app_toolbar_title_user));
    }

    private void updateView() {
        LogUtils.d(TAG, "updateView: ");
        if (null != mViewModel) {
            if (Common.getInstance().hasUser()) {
                String userName = mViewModel.getAccountName();
                if (UserActivityViewModel.DEFAULT_SHOW_TEXT.equals(userName)) {
                    mTvNameInfo.setText(R.string.user_no_name);
                } else {
                    mTvNameInfo.setText(userName);
                }
            } else {
                mTvNameInfo.setText(R.string.user_no_user);
            }
            mTvAutographInfo.setText(mViewModel.getPreferenceString(UserActivityViewModel.UserInfo.USER_AUTOGRAPH));
            mTvNickNameInfo.setText(mViewModel.getPreferenceString(UserActivityViewModel.UserInfo.USER_NICKNAME));
            mTvAddressInfo.setText(mViewModel.getPreferenceString(UserActivityViewModel.UserInfo.USER_ADDRESS));
            mTvPhoneInfo.setText(mViewModel.getPreferenceString(UserActivityViewModel.UserInfo.USER_PHONE));
            mTvHobbyInfo.setText(mViewModel.getPreferenceString(UserActivityViewModel.UserInfo.USER_HOBBY));
            mTvProfileInfo.setText(mViewModel.getPreferenceString(UserActivityViewModel.UserInfo.USER_PROFILE));
            mTvSexInfo.setText(mViewModel.getSexPreferenceString(UserActivityViewModel.UserInfo.USER_SEX));
            mTvHobbyInfo.setText(mViewModel.getHobbyPreferenceString(UserActivityViewModel.UserInfo.USER_HOBBY));
            String userIconImageUrl = mViewModel.getPreferenceString(UserActivityViewModel.UserInfo.USER_ICON);
            Glide.with(mCivUserIcon).load(userIconImageUrl).apply(mGlideOptions).into(mCivUserIcon);
        }
    }

    private void showEditDialog(@NonNull int title, int hint, final int infoType) {
        LogUtils.d(TAG, "showEditDialog: title = " + title + ", hint = " + hint + ", infoType = " + infoType);
        if (null == mViewModel) {
            return;
        }
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setCanceledOnTouchOutside(false);    // 设置为点击对话框之外的区域对话框是否关闭
        Window window = dialog.getWindow();
        dialog.show();

        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setContentView(R.layout.layout_dialog_editor);
        window.setWindowAnimations(R.style.diag_in_out_style);
        // 设置AlertDialog中可以弹出输入法键盘
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        TextView tvTitle = window.findViewById(R.id.tv_dialog_title);
        final EditText etContent = window.findViewById(R.id.et_dialog_editor);
        Button btnConfirm = window.findViewById(R.id.bt_dialog_confirm);
        Button btnCancel = window.findViewById(R.id.bt_dialog_cancel);

        tvTitle.setText(title);
        switch (infoType) {
            case UserActivityViewModel.UserInfo.USER_NAME:
            case UserActivityViewModel.UserInfo.USER_NICKNAME:
                etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(USER_NAME_LENGTH)});
                break;
            case UserActivityViewModel.UserInfo.USER_AUTOGRAPH:
                etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(USER_AUTOGRAPH_LENGTH)});
                etContent.setMaxLines(DIALOG_EDIT_MAX_LINE);
                break;
            case UserActivityViewModel.UserInfo.USER_ADDRESS:
                etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(USER_ADDRESS_LENGTH)});
                etContent.setMaxLines(DIALOG_EDIT_MAX_LINE);
                break;
            case UserActivityViewModel.UserInfo.USER_PHONE:
                etContent.setInputType(InputType.TYPE_CLASS_PHONE);
                etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(USER_PHONE_LENGTH)});
                break;
            case UserActivityViewModel.UserInfo.USER_PROFILE:
                etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(USER_PROFILE_LENGTH)});
                etContent.setMaxLines(DIALOG_EDIT_MAX_LINE);
                break;
            default:
                break;
        }

        String initText = "";
        if (infoType == UserActivityViewModel.UserInfo.USER_NAME) {
            initText = mViewModel.getAccountName();
        } else {
            initText = mViewModel.getPreferenceString(infoType);
        }

        if (TextUtils.isEmpty(initText) || UserActivityViewModel.DEFAULT_SHOW_TEXT.equals(initText)) {
            etContent.setHint(hint);
        } else {
            etContent.setText(initText);
            etContent.setSelection(0, initText.length());
        }

        etContent.requestFocus();
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String value = etContent.getText().toString().trim();
                LogUtils.d(TAG, "onClick: infoType = " + infoType + ", editor.getText() = " + value);
                if (TextUtils.isEmpty(value)) {
                    CommonUtils.getInstance().showToastView(R.string.toast_dialog_user_empty_content);
                    dialog.dismiss();
                    return;
                }
                mViewModel.putPreferenceString(infoType, value);
                dialog.dismiss();
                updateView();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showSexChoiceDialog(@NonNull int title, final int infoType) {
        if (null == mViewModel) {
            return;
        }
        TextView mTitle = new TextView(this);
        mTitle.setBackgroundColor(Color.WHITE);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, DIALOG_TITLE_SIZE);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setText(title);
        mTitle.setPadding(DIALOG_TITLE_PADDING_LEFT, DIALOG_TITLE_PADDING_TOP, DIALOG_TITLE_PADDING_RIGHT, DIALOG_TITLE_PADDING_BOTTOM);
        mTitle.setTextColor(Color.BLACK);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setCustomTitle(mTitle);

        builder.setSingleChoiceItems(mViewModel.getSexItems(), mViewModel.getSexCheckedItemIndex(), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtils.d(TAG, "onClick: which = " + which + ", infoType = " + infoType);
                mViewModel.setSexChoiceItemIndex(which);
            }
        });

        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewModel.putPreferenceString(infoType);
                updateView();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.diag_in_out_style);
        dialog.show();
    }

    private void showHobbyChoiceDialog(@NonNull int title, final int infoType) {
        if (null == mViewModel) {
            return;
        }

        TextView mTitle = new TextView(this);
        mTitle.setBackgroundColor(Color.WHITE);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, DIALOG_TITLE_SIZE);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setText(title);
        mTitle.setPadding(DIALOG_TITLE_PADDING_LEFT, DIALOG_TITLE_PADDING_TOP, DIALOG_TITLE_PADDING_RIGHT, DIALOG_TITLE_PADDING_BOTTOM);
        mTitle.setTextColor(Color.BLACK);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setCustomTitle(mTitle);
        builder.setMultiChoiceItems(mViewModel.getHobbyItems(), mViewModel.getHobbyCheckedItemsFlag(), new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                LogUtils.d(TAG, "onClick: which = " + which + ", isChecked = " + isChecked);
                mViewModel.setHobbyChoiceItemsFlag(which, isChecked);
            }
        });

        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtils.d(TAG, "onClick: infoType = " + infoType);
                mViewModel.putPreferenceString(infoType);
                updateView();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.diag_in_out_style);
        dialog.show();
    }

    private void showAccountManagerDialog() {
        // 加载popupwindow视图
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_account_manager, null);
        View parentView = findViewById(R.id.ll_user_layout);
        View accountChange = dialogView.findViewById(R.id.tv_popMenu_account_manager_change);
        View accountLogout = dialogView.findViewById(R.id.tv_popMenu_account_manager_logout);
        View accountCancel = dialogView.findViewById(R.id.tv_popMenu_account_manager_cancel);

        // popupWindow 初始化
        final PopupWindow popupWindow = new PopupWindow(dialogView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        popupWindow.setAnimationStyle(R.style.diag_in_out_style);
        popupWindow.setFocusable(true);
        // popupWindow 设置大小及位置
        popupWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        // 事件监听
        AccountManagerClickListener clickListener = new AccountManagerClickListener(popupWindow);
        accountChange.setOnClickListener(clickListener);
        accountLogout.setOnClickListener(clickListener);
        accountCancel.setOnClickListener(clickListener);

        // 当弹出Popupwindow时，背景变半透明
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.7f;
        getWindow().setAttributes(layoutParams);

        // 当popupwindow关闭时，背景恢复正常
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.alpha = 1f;
                getWindow().setAttributes(layoutParams);
            }
        });

        // 触摸popupwindow以外的地方，自动关闭popupwindow
        dialogView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = dialogView.findViewById(R.id.ll_dialog_account_manager_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        popupWindow.dismiss();
                    }
                }
                return true;

            }
        });

    }

    private void showAccountManagerConfirmDialog(final int viewID) {
        final Common common = Common.getInstance();
        String contentText = viewID == R.id.tv_popMenu_account_manager_change ?
                getString(R.string.dialog_account_change_content) :
                getString(R.string.dialog_account_logout_content);

        new SweetAlertDialog(UserActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.dialog_waring_tips))
                .setContentText(contentText)
                .setConfirmText(getString(R.string.dialog_confirm))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        if (viewID == R.id.tv_popMenu_account_manager_change) {
                            common.changeAccount();
                        } else if (viewID == R.id.tv_popMenu_account_manager_logout) {
                            if (!common.logoutUser()) {
                                CommonUtils.getInstance().showToastView(R.string.toast_account_manager_logout_failed);
                                return;
                            }
                        } else {
                            // nothing to do
                        }
                        jumpLoginActivity();
                    }
                })
                .setCancelText(getString(R.string.dialog_cancel))
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    private void jumpLoginActivity() {
        JumpUtils.jumpActivity(UserActivity.this, LoginActivity.class);
    }

    private void selectIconImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_IMAGE_CODE);
    }

    /***************************** inner class *************************************/
    private class AccountManagerClickListener implements View.OnClickListener {
        private PopupWindow mPopupWindow;

        public AccountManagerClickListener(PopupWindow popupWindow) {
            this.mPopupWindow = popupWindow;
        }

        @Override
        public void onClick(View v) {
            if (null != mPopupWindow) {
                final Common common = Common.getInstance();
                switch (v.getId()) {
                    case R.id.tv_popMenu_account_manager_change:
                    case R.id.tv_popMenu_account_manager_logout:
                        showAccountManagerConfirmDialog(v.getId());
                        break;
                    case R.id.tv_popMenu_account_manager_cancel:
                        break;
                    default:
                        break;
                }
                mPopupWindow.dismiss();
            }
        }
    }
}
