package com.jm.news.define;

/**
 * DataManager some define
 */
public class DataDef {

    public static final class AppInfo {
        public static final String APP_SHARE_LINK = "https://github.com/jiangminggithub/Android-NewsApp";
        public static final String APP_GITHUB_LINK = "https://github.com/jiangminggithub/Android-NewsApp";
        public static final String APP_DOWNLOAD_LINK = "https://github.com/jiangminggithub/Android-NewsApp";
    }

    public static final class ApiInfo {
        public static final String API_ID = "q}~{q";
        public static final String API_SECRET = "+**}*||\u007F~z(~}{,|+/x|x+q-*~}p{+({";
    }

    public static final class RequestStatusType {
        public static final int DATA_STATUS_REQUEST_OK = 0;
        public static final int DATA_STATUS_REQUEST_FAILED = 1;
        public static final int DATA_STATUS_NETWORK_DISCONNECTED = 2;
        public static final int DATA_STATUS_NO_MORE_DATA = 3;
    }

    public static final class NewsChanelIDs {
        public static final String[] CHANEL_IDS = {
                "5572a108b3cdc86cf39001cd",
                "5572a108b3cdc86cf39001ce",
                "5572a109b3cdc86cf39001da",
                "5572a108b3cdc86cf39001d0",
                "5572a108b3cdc86cf39001d9",
                "5572a108b3cdc86cf39001d1",
                "5572a10bb3cdc86cf39001f5",
                "5572a108b3cdc86cf39001d2",
                "5572a108b3cdc86cf39001d3",
                "5572a108b3cdc86cf39001cf",
                "5572a108b3cdc86cf39001d4",
                "5572a10bb3cdc86cf39001f7",
        };
        public static final String BANNER_ID = "5572a10bb3cdc86cf39001f8";
    }

    public static final class WebViewKey {
        public static final String KEY_URL = "newsLink";
        public static final String KEY_OPEN_JAVASCRIPT = "openJavaScript";
    }

}
