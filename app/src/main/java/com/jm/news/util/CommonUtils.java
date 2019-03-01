package com.jm.news.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.jm.news.R;
import com.jm.news.activity.MainActivity;
import com.jm.news.common.Common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CommonUtils {

    // static field
    private static final String TAG = "CommonUtils";
    public static final int RESTART_TYPE_ALL_ACTIVITY = 0;
    public static final int RESTART_TYPE_APP = 1;
    public static final int APP_VERSION_FAILED_GET = -1;
    private static final int SHARE_CHOOSE_COPY_LINK = 0;
    private static final String REFLEX_DECLARED_FIELD = "mPopup";
    private static final String REFLEX_DECLARED_METHOD = "setForceShowIcon";
    private static CommonUtils mInstance = null;
    // function field
    private Context mContext = null;
    private SweetAlertDialog mNetInvisibleDialog;

    private CommonUtils() {

    }

    public static final synchronized CommonUtils getInstance() {
        if (null == mInstance) {
            mInstance = new CommonUtils();
        }
        return mInstance;
    }

    public void initialize(@NonNull Context context) {
        this.mContext = context;
    }

    /************************************** public  method *************************************/
    /**
     * 通过text显示Toast
     *
     * @param text 显示的文本
     */
    public void showToastView(@NonNull String text) {
        if (null != mContext) {
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 通过ResourceID显示Toast
     *
     * @param resID 显示的ResourceID
     */
    public void showToastView(int resID) {
        if (null != mContext) {
            Toast.makeText(mContext, resID, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示自定义显示时间和文本的Toast
     *
     * @param text 显示的字符
     * @param time 显示的时间
     */
    public void showToastView(String text, int time) {
        if (null != mContext) {
            Toast.makeText(mContext, text, time).show();
        }
    }

    /**
     * 重启App,两种方式，重启APP或者所有Activity
     *
     * @param type 重启操作类型 RESTART_TYPE_ALL_ACTIVITY，RESTART_TYPE_APP
     */
    public void restartApp(int type) {
        if (null != mContext) {
            Intent intent = null;
            if (type == RESTART_TYPE_ALL_ACTIVITY) {
                intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else if (type == RESTART_TYPE_APP) {
                intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                return;
            }
            mContext.startActivity(intent);
            System.exit(0);
        }
    }

    /*
     * 获取当前程序的版本号名
     */
    public String getVersionName() {
        if (null != mContext) {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packInfo = null;
            try {
                packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "getVersionName: --------- failed -----------");
            }
            LogUtils.d("TAG", "getVersionName = " + packInfo.versionName);
            return packInfo.versionName;
        }
        return null;
    }

    /*
     * 获取当前程序的版本号
     */
    public long getVersionCode() {
        if (null != mContext) {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packInfo = null;
            try {
                packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    LogUtils.e("TAG", "getVersionCode versionCode = " + packInfo.getLongVersionCode());
                    return packInfo.getLongVersionCode();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "getVersionCode: --------- failed -----------");
            }
            LogUtils.d(TAG, "getVersionCode  versionCode = " + packInfo.versionCode);
            return packInfo.versionCode;
        }
        return APP_VERSION_FAILED_GET;
    }

    /**
     * 检查网络是否可用
     *
     * @return Boolean
     */
    public boolean isNetworkAvailable() {
        if (null != mContext) {
            ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 当前网络是连接的
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        // 当前所连接的网络可用
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 显示网络断开的提示框
     *
     * @param activity 目标的Activity对象
     */
    public void showNetInvisibleDialog(Activity activity) {
        if (null == activity) {
            return;
        }
        if (null == mNetInvisibleDialog) {
            Common common = Common.getInstance();
            mNetInvisibleDialog = new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE);
            mNetInvisibleDialog.setTitleText(common.getResourcesString(R.string.dialog_waring_tips))
                    .setContentText(common.getResourcesString(R.string.dialog_net_invisible_content))
                    .setConfirmText(common.getResourcesString(R.string.dialog_confirm))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            mNetInvisibleDialog = null;
                        }
                    });
            mNetInvisibleDialog.setCancelable(false);
            mNetInvisibleDialog.show();
            LogUtils.d(TAG, "showNetInvisibleDialog: show");
        } else if (!mNetInvisibleDialog.isShowing()) {
            mNetInvisibleDialog.show();
        } else {
            // nothing to do
        }
    }

    /**
     * 关闭网络断开的提示框
     */
    public void dismissNetInvisibleDialog() {
        if (null != mNetInvisibleDialog && mNetInvisibleDialog.isShowing()) {
            mNetInvisibleDialog.dismiss();
            mNetInvisibleDialog = null;
            LogUtils.d(TAG, "dismissNetInvisibleDialog: dismiss");
        }
    }

    /************************************** static method *************************************/
    /**
     * 增加View触摸区域大小，最小区域为View的默认大小
     *
     * @param view   target 目标View
     * @param top    增加上部区域大小
     * @param bottom 增加下部区域大小
     * @param left   增加左部区域大小
     * @param right  增加右部区域大小
     */
    public static void expandViewTouchDelegate(@NonNull final View view, final int top, final int bottom, final int left, final int right) {
        if (null != view) {
            ((View) view.getParent()).post(new Runnable() {
                @Override
                public void run() {
                    Rect bounds = new Rect();
                    view.setEnabled(true);
                    view.getHitRect(bounds);

                    bounds.top -= top;
                    bounds.bottom += bottom;
                    bounds.left -= left;
                    bounds.right += right;

                    TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                    if (View.class.isInstance(view.getParent())) {
                        ((View) view.getParent()).setTouchDelegate(touchDelegate);
                    }
                }
            });
        }
    }

    /**
     * 恢复view触摸区域大小
     *
     * @param view 目标View
     */
    public static void restoreViewTouchDelegate(@NonNull final View view) {

        if (null != view) {
            ((View) view.getParent()).post(new Runnable() {
                @Override
                public void run() {
                    Rect bounds = new Rect();
                    bounds.setEmpty();
                    TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                    if (View.class.isInstance(view.getParent())) {
                        ((View) view.getParent()).setTouchDelegate(touchDelegate);
                    }
                }
            });
        }
    }

    /**
     * 为指定View创建popupMenu
     *
     * @param context   目标的Context
     * @param view      需要显示popupMenu的对象
     * @param resMenuID 需要显示Menu的ResourceID
     * @param hasIcon   标记可以带icon的选项，注意在不同系统中显示不可控，谨慎使用
     * @return PopupMenu    PopupMenu 对象
     */
    public static PopupMenu getPopupMenu(@NonNull Context context, @NonNull View view, int resMenuID, boolean hasIcon) {
        if (null == context || null == view) {
            return null;
        }

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(resMenuID);

        if (hasIcon) {
            // used reflex show menu icon
            try {
                Field field = popupMenu.getClass().getDeclaredField(REFLEX_DECLARED_FIELD);
                field.setAccessible(true);
                Object helper = field.get(popupMenu);
                Method mSetForceShowIcon = helper.getClass().getDeclaredMethod(REFLEX_DECLARED_METHOD, boolean.class);
                mSetForceShowIcon.invoke(helper, true);
                field.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "getPopupMenuIcon: class reflex failed ");
            }
        }

        return popupMenu;
    }

    /**
     * 解决部分虚拟键手机Menu键显示
     *
     * @param activity 目标activity
     */
    public static void setNeedsMenuKey(@NonNull Activity activity) {
        if (null == activity || Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return;
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                int flags = WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null);
                activity.getWindow().addFlags(flags);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Method setNeedsMenuKey = Window.class.getDeclaredMethod("setNeedsMenuKey", int.class);
                setNeedsMenuKey.setAccessible(true);
                int value = WindowManager.LayoutParams.class.getField("NEEDS_MENU_SET_TRUE").getInt(null);
                setNeedsMenuKey.invoke(activity.getWindow(), value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 判断某个界面是否在前台,返回true，为显示,否则不是
     */
    public static boolean isForeground(Activity activity) {

        if (activity == null || TextUtils.isEmpty(activity.getClass().getName()))
            return false;
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (activity.getClass().getName().equals(cpn.getClassName()))
                return true;
        }
        return false;
    }

    /**
     * 显示暂时未实现功能提示框
     *
     * @param activity context对象
     */
    public static void showFunctionNotOpenDialog(Activity activity) {
        if (null != activity) {
            Common common = Common.getInstance();
            new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(common.getResourcesString(R.string.dialog_waring_tips))
                    .setContentText(common.getResourcesString(R.string.dialog_waring_content))
                    .setConfirmText(common.getResourcesString(R.string.dialog_confirm))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    /**
     * 检查应用权限
     *
     * @param activity activity对象
     * @return 是否有权限true：具备,false:没有权限
     */
    public static boolean checkPermissions(Activity activity) {
        if (null != activity) {
            int writeStorageState = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            LogUtils.d(TAG, "checkPermissions:  writeStorageState = " + writeStorageState);
            if (writeStorageState != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 显示没有权限的提示框
     *
     * @param activity activity对象
     */
    public static void showNoPermission(final Activity activity) {
        if (null != activity) {
            Common common = Common.getInstance();
            new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(common.getResourcesString(R.string.dialog_permission_title))
                    .setContentText(common.getResourcesString(R.string.dialog_permission_content))
                    .setCancelText(common.getResourcesString(R.string.dialog_permission_cancel))
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            activity.finish();
                        }
                    })
                    .setConfirmText(common.getResourcesString(R.string.dialog_permission_setting))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            activity.startActivity(intent);
                            sDialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    /**
     * 打开分享选择框，分享的文本信息
     *
     * @param activity 目标activity对象
     * @param content  分享的文本信息
     */
    public static void shareDialog(final Activity activity, final String content) {
        LogUtils.d(TAG, "shareDialog: content = " + content);
        if (null != activity && !TextUtils.isEmpty(content)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.share_choose);
            builder.setItems(Common.getInstance().getResourcesStringArray(R.array.share_choose), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LogUtils.d(TAG, "shareDialog onClick: which = " + which);
                    // 选择复制分享链接
                    if (which == SHARE_CHOOSE_COPY_LINK) {
                        ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content));
                        CommonUtils.getInstance().showToastView(R.string.toast_setting_share_success);
                    } else {
                        // 系统选择框，分享到支持的应用
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, content);
                        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_title)));
                    }
                }
            });
            builder.setCancelable(true).create().show();
        } else {
            CommonUtils.getInstance().showToastView(R.string.toast_setting_share_failed);
        }
    }

}
