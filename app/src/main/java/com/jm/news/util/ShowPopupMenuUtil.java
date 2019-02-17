package com.jm.news.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;

import com.jm.news.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ShowPopupMenuUtil {

    private static final String TAG = "ShowPopupMenuUtil";
    private static ShowPopupMenuUtil mInstance = null;

    private ShowPopupMenuUtil() {

    }

    public static synchronized ShowPopupMenuUtil getInstance() {
        if (null == mInstance) {
            mInstance = new ShowPopupMenuUtil();
        }
        return mInstance;
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

    public PopupMenu showNewsItemPopupMenu(@NonNull Context context, @NonNull View view) {
        if (null == context || null == view) {
            return null;
        }

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_popup_news_item);

        return popupMenu;
    }
}
