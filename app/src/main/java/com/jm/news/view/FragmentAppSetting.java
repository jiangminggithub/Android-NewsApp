package com.jm.news.view;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jm.news.BuildConfig;
import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.define.DataDef;
import com.jm.news.util.CacheManager;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.JumpUtils;
import com.jm.news.util.LogUtils;
import com.jm.news.viewmodel.FragmentAppSettingViewModel;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentAppSetting extends PreferenceFragment {

    // static field
    private static final String TAG = "FragmentAppSetting";
    private static final int LOCALE_CHOICE_TITLE_LEFT = 10;
    private static final int LOCALE_CHOICE_TITLE_TOP = 20;
    private static final int LOCALE_CHOICE_TITLE_RIGHT = 10;
    private static final int LOCALE_CHOICE_TITLE_BOTTOM = 10;
    private static final int LOCALE_CHOICE_TITLE_SIZE = 20;
    private static final int FEEDBACK_EDIT_MAX_LINES = 5;
    private static final int FEEDBACK_EDIT_MAX_LENGTH = 120;
    private static final String INSTALL_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileProvider";
    private static final String INSTALL_DATA_TYPE = "application/vnd.android.package-archive";
    // function related filed
    private SweetAlertDialog mCheckUpdateDialog;
    private Handler mHandler;
    // viewmodel related field
    private FragmentAppSettingViewModel mViewModel;


    @SuppressLint("HandlerLeak")
    public FragmentAppSetting() {
        super();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                LogUtils.d(TAG, "handleMessage: msg.what = " + msg.what);
                if (null == mCheckUpdateDialog || !mCheckUpdateDialog.isShowing()) {
                    LogUtils.d(TAG, "handleMessage: --- operation is cancel! ---");
                    return;
                }
                String result = null;
                if (null != msg.obj) {
                    result = msg.obj.toString();
                }
                switch (msg.what) {
                    case FragmentAppSettingViewModel.IS_LATEST:
                        if (null != msg.obj) {
                            boolean isLatest = (boolean) msg.obj;
                            changeDownloadDialog(isLatest);
                        } else {
                            mCheckUpdateDialog.dismiss();
                        }
                        break;
                    case FragmentAppSettingViewModel.DOWNLOAD_PROGRESS_NO:
                        LogUtils.d(TAG, "handleMessage: downloadSize = " + result);
                        mCheckUpdateDialog.setContentText(getString(R.string.dialog_setting_downloading) + result);
                        break;
                    case FragmentAppSettingViewModel.DOWNLOAD_PROGRESS_VALUE:
                        LogUtils.d(TAG, "handleMessage: downloadProgress = " + result);
                        mCheckUpdateDialog.setContentText(getString(R.string.dialog_setting_downloading) + result);
                        break;
                    case FragmentAppSettingViewModel.DOWNLOAD_PROGRESS_SUCCESS:
                        LogUtils.d(TAG, "handleMessage: success file path = " + result);
                        final String filePath = result;
                        mCheckUpdateDialog.setTitleText(getString(R.string.dialog_setting_download_success_title))
                                .setContentText(getString(R.string.dialog_setting_download_success_content))
                                .setCancelButton(R.string.dialog_cancel, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .setConfirmButton(R.string.dialog_setting_download_install, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                        installAPK(filePath);

                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        break;
                    case FragmentAppSettingViewModel.DOWNLOAD_PROGRESS_FAILED:
                        mCheckUpdateDialog.setTitleText(getString(R.string.dialog_setting_download_failed_title))
                                .setContentText(getString(R.string.dialog_setting_download_failed_content))
                                .setConfirmButton(R.string.dialog_confirm, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case FragmentAppSettingViewModel.CHECK_UPDATE_FAILED:
                        mCheckUpdateDialog.setTitleText("")
                                .setContentText(getString(R.string.dialog_setting_download_failed_content))
                                .setConfirmButton(R.string.dialog_confirm, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_fragment_setting);
        mViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(FragmentAppSettingViewModel.class);
        mViewModel.initialized();
    }

    @Override
    public void onStart() {
        LogUtils.d(TAG, "onStart: ");
        super.onStart();
        updateView();
    }

    @Override
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy: ");
        mViewModel = null;
        mCheckUpdateDialog = null;
        mHandler = null;
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        LogUtils.d(TAG, "onPreferenceTreeClick: key=" + key + ", preference=" + preference);
        if (null != key && null != mViewModel) {
            if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_CLEAR_CACHE))) {
                clearCache();
            }
            if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_OUT_CLEAR_CACHE))) {
                CheckBoxPreference pre = (CheckBoxPreference) preference;
                mViewModel.putBoolean(key, pre.isChecked());
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_OPEN_NOTIFY))) {
                CheckBoxPreference pre = (CheckBoxPreference) preference;
                mViewModel.putBoolean(key, pre.isChecked());
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_HIDE_BENEFITS))) {
                CheckBoxPreference pre = (CheckBoxPreference) preference;
                mViewModel.putBoolean(key, pre.isChecked());
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_AUTO_CHECK_UPDATE))) {
                CheckBoxPreference pre = (CheckBoxPreference) preference;
                mViewModel.putBoolean(key, pre.isChecked());
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_APP_WALL))) {
                JumpUtils.jumpWebView(getActivity(), DataDef.AppInfo.APP_APP_WALL, true);
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_LOCALE))) {
                showLocaleChoiceDialog(key);
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_REPUTATION))) {
                showRatingDialog();
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_FEEDBACK))) {
                showFeedbackDialog();
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_SHARE))) {
                sharedApp();
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_VERSION))) {
                versionUpdate();
            } else {
                // nothing to do
            }
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    /******************************** private function ************************************/
    private void updateView() {
        LogUtils.d(TAG, "updateView: ");
        if (null == mViewModel) {
            return;
        }
        try {
            String keyClearCache = mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_CLEAR_CACHE);
            String keyAppOutClear = mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_OUT_CLEAR_CACHE);
            String keyOpenNotify = mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_OPEN_NOTIFY);
            String keyAutoRun = mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_AUTO_RUN);
            String keyLocal = mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_LOCALE);
            String keyHideBenefits = mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_HIDE_BENEFITS);
            String keyCheckUpdate = mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_AUTO_CHECK_UPDATE);
            String keyVersion = mViewModel.getPreferenceKey(FragmentAppSettingViewModel.KEY_VERSION);

            Preference preCache = findPreference(keyClearCache);
            CheckBoxPreference preOutClear = (CheckBoxPreference) findPreference(keyAppOutClear);
            CheckBoxPreference preOpenNotify = (CheckBoxPreference) findPreference(keyOpenNotify);
            SwitchPreference preAutoRun = (SwitchPreference) findPreference(keyAutoRun);
            Preference preLocale = findPreference(keyLocal);
            CheckBoxPreference preHideBenefits = (CheckBoxPreference) findPreference(keyHideBenefits);
            CheckBoxPreference preAutoCheckUpdate = (CheckBoxPreference) findPreference(keyCheckUpdate);
            Preference preAppVersion = findPreference(keyVersion);

            preAutoRun.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    LogUtils.d(TAG, "onPreferenceChange: newsValue=" + newValue + ", key=" + preference.getKey());
                    mViewModel.putBoolean(preference.getKey(), (Boolean) newValue);
                    return true;
                }
            });

            boolean isOutClear = mViewModel.getBoolean(keyAppOutClear, false);
            boolean isOpenNotify = mViewModel.getBoolean(keyOpenNotify, true);
            boolean isAutoRun = mViewModel.getBoolean(keyAutoRun, true);
            boolean isHideBenefits = mViewModel.getBoolean(keyHideBenefits, false);
            boolean isCheckUpdate = mViewModel.getBoolean(keyCheckUpdate, true);
            String appVersion = CommonUtils.getInstance().getVersionName();

            preCache.setSummary(mViewModel.getTotalCacheFormatString());
            if (mViewModel.getTotalCacheSize() <= 0) {
                preCache.setEnabled(false);
            } else {
                preCache.setEnabled(true);
            }
            preOutClear.setChecked(isOutClear);
            preOpenNotify.setChecked(isOpenNotify);
            preAutoRun.setChecked(isAutoRun);
            preHideBenefits.setChecked(isHideBenefits);
            preAutoCheckUpdate.setChecked(isCheckUpdate);
            preAppVersion.setSummary(appVersion);
            preLocale.setSummary(mViewModel.getLocaleString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "onStart: error init");
        }
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        LogUtils.d(TAG, "clearCache: ");
        if (null == mViewModel) {
            return;
        }
        final double totalCacheSize = mViewModel.getTotalCacheSize();
        if (totalCacheSize > 0) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.dialog_clear_cache_title))
                    .setContentText(getString(R.string.dialog_clear_cache_content))
                    .setConfirmText(getString(R.string.dialog_clear_cache_confirm))
                    .setCancelText(getString(R.string.dialog_cancel))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sDialog) {
                            if (CacheManager.CLEAR_CACHE_FAILED != mViewModel.clearAllCache()) {
                                sDialog.setTitleText(getString(R.string.dialog_clear_cache_success_title))
                                        .setContentText(getString(R.string.dialog_clear_cache_success_title) + " " + totalCacheSize + getString(R.string.dialog_clear_cache_success_content))
                                        .setConfirmText(getString(R.string.dialog_confirm))
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                updateView();
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            } else {
                                sDialog.setTitleText(getString(R.string.dialog_clear_cache_failed_title))
                                        .setContentText(getString(R.string.dialog_clear_cache_failed_content))
                                        .setConfirmText(getString(R.string.dialog_confirm))
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }
                            sDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setVisibility(View.GONE);
                        }
                    }).show();
        }
    }

    /**
     * 显示语言选择框
     *
     * @param key 操作key
     */
    private void showLocaleChoiceDialog(final String key) {
        TextView mTitle = new TextView(getActivity());
        mTitle.setBackgroundColor(Color.WHITE);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, LOCALE_CHOICE_TITLE_SIZE);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setText(R.string.setting_local_title);
        mTitle.setPadding(LOCALE_CHOICE_TITLE_LEFT, LOCALE_CHOICE_TITLE_TOP, LOCALE_CHOICE_TITLE_RIGHT, LOCALE_CHOICE_TITLE_BOTTOM);
        mTitle.setTextColor(Color.BLACK);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(mTitle);

        builder.setSingleChoiceItems(mViewModel.getLocaleItems(), mViewModel.getLocaleIndex(), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtils.d(TAG, "onClick: which = " + which);
                mViewModel.setLocaleIndex(which);
            }
        });

        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mViewModel.putLocalSelected(key)) {
                    int localeIndex = mViewModel.getLocaleIndex();
                    Common.getInstance().setAppLocale(localeIndex);
                    CommonUtils.getInstance().restartApp(CommonUtils.RESTART_TYPE_ALL_ACTIVITY);
                } else {
                    CommonUtils.getInstance().showToastView(R.string.toast_setting_local_set_failed);
                }
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

    /**
     * 显示评价框
     */
    private void showRatingDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCanceledOnTouchOutside(false);    // 设置为点击对话框之外的区域对话框是否关闭
        Window window = dialog.getWindow();
        dialog.show();

        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setContentView(R.layout.layout_dialog_rating);
        window.setWindowAnimations(R.style.diag_in_out_style);

        final RatingBar rtb_rating = window.findViewById(R.id.rtb_rating);
        Button btnConfirm = window.findViewById(R.id.bt_dialog_confirm);
        Button btnCancel = window.findViewById(R.id.bt_dialog_cancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = rtb_rating.getRating();
                LogUtils.d(TAG, "onClick: rating = " + rating);
                CommonUtils.getInstance().showToastView(R.string.toast_setting_rating_tips);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /***
     * 显示意见反馈框
     */
    private void showFeedbackDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCanceledOnTouchOutside(false);    // 设置为点击对话框之外的区域对话框是否关闭
        Window window = dialog.getWindow();
        dialog.show();

        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setContentView(R.layout.layout_dialog_editor);
        window.setWindowAnimations(R.style.diag_in_out_style);
        // 设置AlertDialog中可以弹出输入法键盘
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        final EditText etContent = window.findViewById(R.id.et_dialog_editor);
        Button btnConfirm = window.findViewById(R.id.bt_dialog_confirm);
        Button btnCancel = window.findViewById(R.id.bt_dialog_cancel);
        TextView tvTitle = window.findViewById(R.id.tv_dialog_title);

        etContent.setHint(R.string.dialog_setting_feedback_edit_hint);
        etContent.setMaxLines(FEEDBACK_EDIT_MAX_LINES);
        etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(FEEDBACK_EDIT_MAX_LENGTH)});
        etContent.requestFocus();
        tvTitle.setText(R.string.feedback);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable feedbackText = etContent.getText();
                LogUtils.d(TAG, "onClick: feedback = " + feedbackText);
                if (!TextUtils.isEmpty(feedbackText)) {
                    CommonUtils.getInstance().showToastView(R.string.toast_setting_feedback_tips);
                    dialog.dismiss();
                } else {
                    CommonUtils.getInstance().showToastView(R.string.toast_setting_feedback_empty);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 分享应用
     */
    private void sharedApp() {
        String content = getString(R.string.share_app_content_one) + DataDef.AppInfo.APP_DOWNLOAD_LINK + getString(R.string.share_app_content_two);
        CommonUtils.shareDialog(getActivity(), content);
    }

    /**
     * 版本更新
     */
    private void versionUpdate() {
        if (CommonUtils.getInstance().isNetworkAvailable()) {
            if (null != mHandler) {
                mCheckUpdateDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                mCheckUpdateDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mCheckUpdateDialog.setContentText(getString(R.string.dialog_setting_version_update_content));
                mCheckUpdateDialog.setCancelable(true);
                mCheckUpdateDialog.show();
                mViewModel.checkUpdate(mHandler);
            }
        } else {
            CommonUtils.getInstance().showNetInvisibleDialog(getActivity());
        }
    }

    /**
     * 下载提示框切换
     */
    private void changeDownloadDialog(boolean isLatest) {
        if (null != mCheckUpdateDialog) {
            if (isLatest) {
                mCheckUpdateDialog
                        .setTitleText(getString(R.string.dialog_waring_tips))
                        .setContentText(getString(R.string.dialog_setting_version_update_success))
                        .setConfirmText(getString(R.string.dialog_confirm))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            } else {
                mCheckUpdateDialog.setCancelable(false);
                mCheckUpdateDialog.setTitleText(getString(R.string.dialog_waring_tips))
                        .setContentText(getString(R.string.dialog_setting_download_is))
                        .setCancelButton(R.string.dialog_cancel, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setConfirmButton(R.string.dialog_setting_download_update, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mCheckUpdateDialog
                                        .setTitleText("")
                                        .setContentText(getString(R.string.dialog_setting_downloading_prepare))
                                        .showCancelButton(false)
                                        .setCancelButton(R.string.dialog_cancel, new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                mViewModel.setCancelDownload(true);
                                                sweetAlertDialog.dismiss();
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                mViewModel.setCancelDownload(false);
                                mViewModel.downloadApp(mHandler);
                            }
                        })
                        .changeAlertType(SweetAlertDialog.WARNING_TYPE);
            }
        }
    }

    /**
     * 启动应用安装
     *
     * @param filePath 安装路径
     */
    private void installAPK(String filePath) {
        LogUtils.d(TAG, "installAPK: filePath = " + filePath);
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File apkFile = new File(filePath);
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 判断是否是 Android N 以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(getContext(), INSTALL_AUTHORITY, apkFile);
                intent.setDataAndType(contentUri, INSTALL_DATA_TYPE);
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), INSTALL_DATA_TYPE);
            }
            startActivity(intent);
        }
    }

}
