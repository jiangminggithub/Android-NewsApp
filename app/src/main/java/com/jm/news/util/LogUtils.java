package com.jm.news.util;


import android.annotation.SuppressLint;
import android.util.Log;

/**
 * 自定义Log工具，可以自由控制输出等级
 */
public class LogUtils {

    // static field
    private static final String TAG = "LogUtils";
    public static final int LOG_TYPE_NO = 0;
    public static final int LOG_TYPE_VERBOSE = 1;
    public static final int LOG_TYPE_DEBUG = 2;
    public static final int LOG_TYPE_INFO = 3;
    public static final int LOG_TYPE_WARMING = 4;
    public static final int LOG_TYPE_ERROR = 5;
    public static final int LOG_TYPE_ALL = 6;

    // Log输出的等级, 0：不输出; >=5: 全输出
    private static int LOG_LEVEL = LOG_TYPE_ALL;

    /**
     * 设置Log输出的等级
     *
     * @param logLevel Log输出的等级, 0：不输出; >=5: 全输出
     */
    public static synchronized void setLogPrintFlag(int logLevel) {
        Log.d(TAG, "setLogPrintFlag: logLevel = " + logLevel);
        LOG_LEVEL = logLevel;
    }

    @SuppressLint("LongLogTag")
    public static void v(String tag, String msg) {
        if (LOG_LEVEL >= LOG_TYPE_VERBOSE) {
            Log.v(tag, msg);
        }
    }

    @SuppressLint("LongLogTag")
    public static void d(String tag, String msg) {
        if (LOG_LEVEL >= LOG_TYPE_DEBUG) {
            Log.d(tag, msg);
        }
    }

    @SuppressLint("LongLogTag")
    public static void i(String tag, String msg) {
        if (LOG_LEVEL >= LOG_TYPE_INFO) {
            Log.i(tag, msg);
        }
    }

    @SuppressLint("LongLogTag")
    public static void w(String tag, String msg) {
        if (LOG_LEVEL >= LOG_TYPE_WARMING) {
            Log.w(tag, msg);
        }
    }

    @SuppressLint("LongLogTag")
    public static void e(String tag, String msg) {
        if (LOG_LEVEL >= LOG_TYPE_ERROR) {
            Log.e(tag, msg);
        }
    }
}
