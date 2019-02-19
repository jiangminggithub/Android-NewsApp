package com.jm.news.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jm.news.R;
import com.jm.news.common.Common;
import com.jm.news.util.CacheManager;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.LogUtils;
import com.jm.news.viewmodel.FragmentSettingViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentAppSetting extends PreferenceFragment {
    private static final String TAG = "FragmentAppSetting";
    private static final int LOCALE_CHOICE_TITLE_LEFT = 10;
    private static final int LOCALE_CHOICE_TITLE_TOP = 20;
    private static final int LOCALE_CHOICE_TITLE_RIGHT = 10;
    private static final int LOCALE_CHOICE_TITLE_BOTTON = 10;
    private static final int LOCALE_CHOICE_TITLE_SIZE = 20;

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
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_CHECK_UPDATE))) {
                CheckBoxPreference pre = (CheckBoxPreference) preference;
                mViewModel.putBoolean(key, pre.isChecked());
            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_VERSION))) {

            } else if (key.equals(mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_LOCALE))) {
                showLocaleChoiceDialog(key);
            } else {

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
            String keyCheckUpdate = mViewModel.getPreferenceKey(FragmentSettingViewModel.KEY_CHECK_UPDATE);
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
            LogUtils.d(TAG, "onStart: error init");
        }
    }

    private void clearCache() {
        LogUtils.d(TAG, "clearCache: ");
        if (null == mViewModel) {
            return;
        }

        final double totalCacheSize = mViewModel.getTotalCacheSize();
        if (totalCacheSize > 0) {
            final Common common = Common.getInstance();
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(common.getResourcesString(R.string.dialog_clear_cache_title))
                    .setContentText(common.getResourcesString(R.string.dialog_clear_cache_content))
                    .setConfirmText(common.getResourcesString(R.string.dialog_clear_cache_confirm))
                    .setCancelText(common.getResourcesString(R.string.dialog_cancel))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sDialog) {
                            if (CacheManager.CLEAR_CACHE_FAILED != mViewModel.clearAllCache()) {
                                sDialog.setTitleText(common.getResourcesString(R.string.dialog_clear_cache_success_title))
                                        .setContentText(common.getResourcesString(R.string.dialog_clear_cache_success_title) + " " + totalCacheSize + common.getResourcesString(R.string.dialog_clear_cache_success_content))
                                        .setConfirmText(common.getResourcesString(R.string.dialog_confirm))
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                updateView();
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            } else {
                                sDialog.setTitleText(common.getResourcesString(R.string.dialog_clear_cache_failed_title))
                                        .setContentText(common.getResourcesString(R.string.dialog_clear_cache_failed_content))
                                        .setConfirmText(common.getResourcesString(R.string.dialog_confirm))
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
                    })
                    .show();
        }
    }

    private void showLocaleChoiceDialog(final String key) {
        TextView mTitle = new TextView(getActivity());
        mTitle.setBackgroundColor(Color.WHITE);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, LOCALE_CHOICE_TITLE_SIZE);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setText(R.string.setting_local_title);
        mTitle.setPadding(LOCALE_CHOICE_TITLE_LEFT, LOCALE_CHOICE_TITLE_TOP, LOCALE_CHOICE_TITLE_RIGHT, LOCALE_CHOICE_TITLE_BOTTON);
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
                    CommonUtils.getInstance().restartApp(CommonUtils.RESTART_TYPE_ALL_ACTIVITYS);
                } else {
                    CommonUtils.getInstance().showToastView(R.string.setting_local_set_failed);
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

}
