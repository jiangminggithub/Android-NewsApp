package com.jm.news.bean;

public class AppVersionBean {

    private ApkInfoBean apkInfo;

    public ApkInfoBean getApkInfo() {
        return apkInfo;
    }

    public void setApkInfo(ApkInfoBean apkInfo) {
        this.apkInfo = apkInfo;
    }

    public static class ApkInfoBean {
        /**
         * versionCode : 1
         * versionName : 1.0.0
         * appName : news.apk
         */

        private int versionCode;
        private String versionName;
        private String appName;

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }
    }
}
