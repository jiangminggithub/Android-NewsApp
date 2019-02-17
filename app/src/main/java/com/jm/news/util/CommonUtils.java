package com.jm.news.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.jm.news.R;
import com.jm.news.activity.MainActivity;
import com.jm.news.activity.WebviewActivity;
import com.jm.news.define.DataDef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CommonUtils {

    private static final String TAG = "CommonUtils";
    private static CommonUtils mInstance = null;
    public static final int RESTART_TYPE_ALL_ACTIVITYS = 0;
    public static final int RESTART_TYPE_APP = 1;


    private Context mContext = null;

    private CommonUtils() {

    }

    public static final synchronized CommonUtils getInstance() {
        if (null == mInstance) {
            mInstance = new CommonUtils();
        }
        return mInstance;
    }

    public void initialize(Context context) {
        this.mContext = context;
    }

    /**
     * Expand the Touch and Click Response Range of View to a maximum not exceeding its parent View Range
     *
     * @param view   target view
     * @param top    expand top range
     * @param bottom expand bottom range
     * @param left   expand left range
     * @param right  expand right range
     */
    public void expandViewTouchDelegate(final View view, final int top, final int bottom, final int left, final int right) {
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


    /**
     * Restore View's touch and click response range, minimum not less than View's own range
     *
     * @param view target view
     */
    public void restoreViewTouchDelegate(final View view) {

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

    /**
     * show toast text with Toast.LENGTH_SHORT
     *
     * @param text showed string
     */
    public void showToastView(String text) {
        if (null != mContext) {
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * show toast text with Toast.LENGTH_SHORT
     *
     * @param resID showed resID
     */
    public void showToastView(int resID) {
        if (null != mContext) {
            Toast.makeText(mContext, resID, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * show toast text with time
     *
     * @param text showed string
     * @param time showed time
     */
    public void showToastView(String text, int time) {
        if (null != mContext) {
            Toast.makeText(mContext, text, time).show();
        }
    }

    /**
     * Create PopupMenu
     *
     * @param context   target context
     * @param view      PopMenu View Object
     * @param resMenuID target menu resource ID
     * @return PopupMenu    PopupMenu Object
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
     *  create NewsItem PopupMenu
     * @param context   target context
     * @param view  PopMenu View Object
     * @return   PopupMenu Object
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
     * Create NewsItem PopupMenu with icon
     *
     * @param context Context Object
     * @param view    PopMenu View Object
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
            Log.d(TAG, "showNewsItemPopupMenuIcon: class reflex failed ");
        }

        return popupMenu;
    }


    /**
     * Solution to part of the use of virtual keys mobile phones do not display Menu keys
     *
     * @param activity target activity
     */
    public void setNeedsMenuKey(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
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
     * Restart App with type form CommonUtils restart type
     *
     * @param type opreation type form CommonUtils restart type
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

    public void jumpWebView(Context context, String newsLink, boolean isJavaScript) {
        Log.d(TAG, "jumpWebView: newsLink = " + newsLink);
        if (null != context) {
            Intent intent = new Intent(context, WebviewActivity.class);
            intent.putExtra(DataDef.WebViewKey.KEY_URL, newsLink);
            intent.putExtra(DataDef.WebViewKey.KEY_OPEN_JAVASCRIPT, isJavaScript);
            context.startActivity(intent);
        }
    }

    public void jumpOtherApp(Context context, String url) {
        Log.d(TAG, "jumpOtherApp: url = " + url);
        if (null != context && !TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }
    }


//    // Restart App Activity
//    public void restartAppActivitys() {
//        if (null != mContext) {
//            Intent intent = new Intent(mContext, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            mContext.startActivity(intent);
//            System.exit(0);
//        }
//    }
//
//    // Restart App
//    public void restartApp() {
//        if (null != mContext) {
//            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            mContext.startActivity(intent);
//            System.exit(0);
//        }
//    }


    ////获取屏幕宽高
//        WindowManager wm = (WindowManager) getApplication()
//                .getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        int width =display.getWidth();
//        int height=display.getHeight();
}
