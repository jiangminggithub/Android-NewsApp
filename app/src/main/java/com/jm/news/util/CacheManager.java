package com.jm.news.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by jiangmimg on 2019-02-02
 */
public class CacheManager {

    private static final String TAG = "CacheManager";
    public static final int CLEAR_CACHE_SUCCESS = 0;
    public static final int CLEAR_CACHE_DIR_SUCCESS = 1;
    public static final int CLEAR_EXTERNAL_CACHE_DIR_SUCCESS = 2;
    public static final int CLEAR_CACHE_FAILED = 3;

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSizeString(Context context) throws Exception {
        Log.d(TAG, "getTotalCacheSizeString: context.getCacheDir() = " + context.getCacheDir());
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
            Log.d(TAG, "getTotalCacheSizeString: context.getExternalCacheDir() = " + context.getExternalCacheDir());
        }
        return getFormatSizeString(cacheSize);
    }

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static double getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    /**
     * 清除缓存
     *
     * @param context
     */
    public static int clearAllCache(Context context) {
        boolean externaDirlStatus = false;
        boolean dirStatus = deleteDir(context.getCacheDir());
        Log.d(TAG, "clearAllCache: Environment.getExternalStorageState() = " + Environment.getExternalStorageState());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            externaDirlStatus = deleteDir(context.getExternalCacheDir());
        }
        Log.d(TAG, "clearAllCache: dirStatus = " + dirStatus + ", externaDirlStatus = " + externaDirlStatus);
        if (dirStatus && externaDirlStatus) {
            return CLEAR_CACHE_SUCCESS;
        } else if (!dirStatus && externaDirlStatus) {
            return CLEAR_EXTERNAL_CACHE_DIR_SUCCESS;
        } else if (dirStatus && !externaDirlStatus) {
            return CLEAR_CACHE_DIR_SUCCESS;
        }

        return CLEAR_CACHE_FAILED;
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    // 获取文件大小
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位字符串
     *
     * @param size
     * @return
     */
    public static String getFormatSizeString(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0 KB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + " TB";
    }

    /**
     * 格式化数字
     *
     * @param size
     * @return
     */
    public static double getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return 0;
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}