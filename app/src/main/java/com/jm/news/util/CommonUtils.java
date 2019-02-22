package com.jm.news.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.jm.news.R;
import com.jm.news.activity.MainActivity;
import com.jm.news.activity.WelcomeActivity;
import com.jm.news.common.Common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CommonUtils {

    // static field
    private static final String TAG = "CommonUtils";
    public static final int RESTART_TYPE_ALL_ACTIVITY = 0;
    public static final int RESTART_TYPE_APP = 1;
    public static final int APP_VERSION_FAILED_GET = -1;
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

    /**
     * 增加View触摸区域大小，最小区域为View的默认大小
     *
     * @param view   target 目标View
     * @param top    增加上部区域大小
     * @param bottom 增加下部区域大小
     * @param left   增加左部区域大小
     * @param right  增加右部区域大小
     */
    public void expandViewTouchDelegate(@NonNull final View view, final int top, final int bottom, final int left, final int right) {
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
    public void restoreViewTouchDelegate(@NonNull final View view) {

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
     * 为指定View创建popupMenu
     *
     * @param context   目标的Context
     * @param view      需要显示popupMenu的对象
     * @param resMenuID 需要显示Menu的ResourceID
     * @param hasIcon   标记可以带icon的选项，注意在不同系统中显示不可控，谨慎使用
     * @return PopupMenu    PopupMenu 对象
     */
    public PopupMenu getPopupMenu(@NonNull Context context, @NonNull View view, int resMenuID, boolean hasIcon) {
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
    public void setNeedsMenuKey(@NonNull Activity activity) {
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
     * 获取当前程序的版本名
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
            LogUtils.d("TAG", "版本名" + packInfo.versionName);
            return packInfo.versionName;
        }
        return null;
    }

    /*
     * 获取当前程序的版本名
     */
    public long getVersionCode() {
        if (null != mContext) {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packInfo = null;
            try {
                packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    LogUtils.e("TAG", "版本号" + packInfo.getLongVersionCode());
                    return packInfo.getLongVersionCode();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "getVersionCode: --------- failed -----------");
            }
            LogUtils.d(TAG, "版本号" + packInfo.versionCode);
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
     * @param context 目标的Activity context对象
     */
    public void showNetInvisibleDialog(Context context) {
        if (null == context) {
            return;
        }
        if (null == mNetInvisibleDialog) {
            Common common = Common.getInstance();
            mNetInvisibleDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
            mNetInvisibleDialog.setTitleText(common.getResourcesString(R.string.dialog_waring_tips))
                    .setContentText(common.getResourcesString(R.string.dialog_net_invisible_content))
                    .setConfirmText(common.getResourcesString(R.string.dialog_confirm))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            mNetInvisibleDialog = null;
                        }
                    });
            mNetInvisibleDialog.setCancelable(false);
            mNetInvisibleDialog.show();
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
        }
    }

    /**
     * 显示暂时未实现功能提示框
     *
     * @param context context对象
     */
    public static void showFunctionNotOpenDialog(Context context) {
        if (null != context) {
            Common common = Common.getInstance();
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
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

    public void checkPermissions(Activity activity) {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET};

        int writeStorageState = ContextCompat.checkSelfPermission(activity, permissions[0]);
        int readStorageState = ContextCompat.checkSelfPermission(activity, permissions[1]);
        int networkState = ContextCompat.checkSelfPermission(activity, permissions[2]);
        int internetState = ContextCompat.checkSelfPermission(activity, permissions[3]);

        Log.d(TAG, "checkPresion: readStorageState = " + readStorageState + " , writeStorageState = " + writeStorageState + ", networkState = " + networkState + ", internetState = " + internetState);

//        if (readStorageState != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        }
//        if (writeStorageState != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, permissions, 1);
//        }
//        if (networkState != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);
//        }
//        if (internetState != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
//        }

    }
}
