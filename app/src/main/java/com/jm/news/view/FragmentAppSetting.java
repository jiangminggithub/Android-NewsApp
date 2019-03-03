package com.jm.news.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.FragmentActivity;
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

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.define.DataDef;
import com.jm.news.util.CacheManager;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.JumpUtils;
import com.jm.news.util.LogUtils;
import com.jm.news.viewmodel.FragmentSettingViewModel;

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
    // viewmodel related field
    private FragmentSettingViewModel mViewModel;


    public FragmentAppSetting() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_fragment_setting);
        mViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(FragmentSettingViewModel.class);
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
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        LogUtils.d(TAG, "onPreferenceTreeClick: key=" + key + ", preference=" + preference);
        if (null != key && null != mViewModel) {
            if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_CLEAR_CACHE))) {
                clearCache();
            }
            if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_OUT_CLEAR_CACHE))) {
                CheckBoxPreference pre = (CheckBoxPreference) preference;
                mViewModel.putBoolean(key, pre.isChecked());
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_OPEN_NOTIFY))) {
                CheckBoxPreference pre = (CheckBoxPreference) preference;
                mViewModel.putBoolean(key, pre.isChecked());
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_HIDE_BENEFITS))) {
                CheckBoxPreference pre = (CheckBoxPreference) preference;
                mViewModel.putBoolean(key, pre.isChecked());
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_AUTO_CHECK_UPDATE))) {
                CheckBoxPreference pre = (CheckBoxPreference) preference;
                mViewModel.putBoolean(key, pre.isChecked());
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_APP_WALL))) {
                JumpUtils.jumpWebView(getActivity(), DataDef.AppInfo.APP_APP_WALL, true);
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_LOCALE))) {
                showLocaleChoiceDialog(key);
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_REPUTATION))) {
                showRatingDialog();
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_FEEDBACK))) {
                showFeedbackDialog();
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_SHARE))) {
                sharedApp();
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_VERSION))) {
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
            String keyClearCache = mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_CLEAR_CACHE);
            String keyAppOutClear = mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_OUT_CLEAR_CACHE);
            String keyOpenNotify = mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_OPEN_NOTIFY);
            String keyAutoRun = mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_AUTO_RUN);
            String keyLocal = mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_LOCALE);
            String keyHideBenefits = mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_HIDE_BENEFITS);
            String keyCheckUpdate = mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_AUTO_CHECK_UPDATE);
            String keyVersion = mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_VERSION);

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
            final Common common = Common.getInstance();
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE).setTitleText(common.getResourcesString(R.string.dialog_clear_cache_title)).setContentText(common.getResourcesString(R.string.dialog_clear_cache_content)).setConfirmText(common.getResourcesString(R.string.dialog_clear_cache_confirm)).setCancelText(common.getResourcesString(R.string.dialog_cancel)).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(final SweetAlertDialog sDialog) {
                    if (CacheManager.CLEAR_CACHE_FAILED != mViewModel.clearAllCache()) {
                        sDialog.setTitleText(common.getResourcesString(R.string.dialog_clear_cache_success_title)).setContentText(common.getResourcesString(R.string.dialog_clear_cache_success_title) + " " + totalCacheSize + common.getResourcesString(R.string.dialog_clear_cache_success_content)).setConfirmText(common.getResourcesString(R.string.dialog_confirm)).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                updateView();
                            }
                        }).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    } else {
                        sDialog.setTitleText(common.getResourcesString(R.string.dialog_clear_cache_failed_title)).setContentText(common.getResourcesString(R.string.dialog_clear_cache_failed_content)).setConfirmText(common.getResourcesString(R.string.dialog_confirm)).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        }).changeAlertType(SweetAlertDialog.ERROR_TYPE);
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
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setContentText(Common.getInstance().getResourcesString(R.string.dialog_setting_version_update_content));
        pDialog.setCancelable(false);
        pDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != pDialog) {
                    pDialog.setTitleText(getActivity().getString(R.string.dialog_waring_tips)).setContentText(getActivity().getString(R.string.dialog_setting_version_update_success)).setConfirmText(getActivity().getString(R.string.dialog_confirm)).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    });
                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                }
            }
        }, 1200);
    }

}
