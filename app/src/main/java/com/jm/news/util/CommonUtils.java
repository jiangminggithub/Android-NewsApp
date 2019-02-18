package com.jm.news.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.jm.news.R;
import com.jm.news.activity.MainActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CommonUtils {

    private static final String TAG = "CommonUtils";
    private static CommonUtils mInstance = null;
    public static final int RESTART_TYPE_ALL_ACTIVITYS = 0;
    public static final int RESTART_TYPE_APP = 1;
    public static final int APP_VERSION_FAILED_GET = -1;


    private Context mContext = null;

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
     * @return PopupMenu    PopupMenu 对象
     */
    public PopupMenu showPopupMenu(@NonNull Context context, @NonNull View view, @NonNull int resMenuID) {
        if (null == context || null == view) {
            return null;
        }
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(resMenuID);

        return popupMenu;
    }

    /**
     * 创建新闻选项popupMenu
     *
     * @param context 目标的Context
     * @param view    需要显示popupMenu的对象
     * @return PopupMenu 对象
     */
    public PopupMenu showNewsItemPopupMenu(@NonNull Context context, @NonNull View view) {
        if (null == context || null == view) {
            return null;
        }

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_popup_news_item);

        return popupMenu;
    }

    /**
     * 创建一个可以带icon的新闻选项popupMenu
     *
     * @param context Context 对象
     * @param view    需要显示popupMenu的对象
     * @return PopupMenu
     */
    public PopupMenu showNewsItemPopupMenuIcon(@NonNull Context context, @NonNull View view) {
        if (null == context || null == view) {
            return null;
        }

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_popup_news_item);

        // used reflex show menu icon
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object helper = field.get(popupMenu);
            Method mSetForceShowIcon = helper.getClass().getDeclaredMethod("setForceShowIcon", boolean.class);
            mSetForceShowIcon.invoke(helper, true);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "showNewsItemPopupMenuIcon: class reflex failed ");
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
     * @param type 重启操作类型 RESTART_TYPE_ALL_ACTIVITYS，RESTART_TYPE_APP
     */
    public void restartApp(int type) {
        if (null != mContext) {
            Intent intent = null;
            if (type == RESTART_TYPE_ALL_ACTIVITYS) {
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

}
