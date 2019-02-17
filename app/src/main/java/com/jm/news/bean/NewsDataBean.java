package com.jm.news.bean;

import java.util.List;

public class NewsDataBean {

    /**
     * showapi_res_error :
     * showapi_res_id : 728ff91d2bfb4713a9c683906ca8f983
     * showapi_res_code : 0
     * showapi_res_body : {"ret_code":0,"pagebean":{"allPages":1734,"contentlist":[{"pubDate":"2019-01-19 09:14:00","channelName":"国内焦点","channelId":"5572a108b3cdc86cf39001cd","link":"https://news.163.com/19/0119/09/E5SF5JDF0001875N.html","img":"http://cms-bucket.ws.126.net/2019/01/19/148c6d3c96394d63b97330cc091a2e48.jpg","id":"821ae01b0d2fb0c42bc40549feca4022","havePic":true,"title":"外交部发言人陆慷的另一个身份:＂边学边做＂的新人","imageurls":[{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/148c6d3c96394d63b97330cc091a2e48.jpg"},{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/8dadc1b65c44406895734b1bae13d75d.jpg"},{"height":0,"width":0,"url":"http://static.ws.126.net/cnews/css13/img/end_news.png"}],"source":"网易新闻"}],"currentPage":1,"allNum":34665,"maxResult":20}}
     */

    private String showapi_res_error;
    private String showapi_res_id;
    private int showapi_res_code;
    private ShowapiResBodyBean showapi_res_body;

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public String getShowapi_res_id() {
        return showapi_res_id;
    }

    public void setShowapi_res_id(String showapi_res_id) {
        this.showapi_res_id = showapi_res_id;
    }

    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public ShowapiResBodyBean getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(ShowapiResBodyBean showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }

    public static class ShowapiResBodyBean {
        /**
         * ret_code : 0
         * pagebean : {"allPages":1734,"contentlist":[{"pubDate":"2019-01-19 09:14:00","channelName":"国内焦点","channelId":"5572a108b3cdc86cf39001cd","link":"https://news.163.com/19/0119/09/E5SF5JDF0001875N.html","img":"http://cms-bucket.ws.126.net/2019/01/19/148c6d3c96394d63b97330cc091a2e48.jpg","id":"821ae01b0d2fb0c42bc40549feca4022","havePic":true,"title":"外交部发言人陆慷的另一个身份:＂边学边做＂的新人","imageurls":[{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/148c6d3c96394d63b97330cc091a2e48.jpg"},{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/8dadc1b65c44406895734b1bae13d75d.jpg"},{"height":0,"width":0,"url":"http://static.ws.126.net/cnews/css13/img/end_news.png"}],"source":"网易新闻"}],"currentPage":1,"allNum":34665,"maxResult":20}
         */

        private int ret_code;
        private PagebeanBean pagebean;

        public int getRet_code() {
            return ret_code;
        }

        public void setRet_code(int ret_code) {
            this.ret_code = ret_code;
        }

        public PagebeanBean getPagebean() {
            return pagebean;
        }

        public void setPagebean(PagebeanBean pagebean) {
            this.pagebean = pagebean;
        }

        public static class PagebeanBean {
            /**
             * allPages : 1734
             * contentlist : [{"pubDate":"2019-01-19 09:14:00","channelName":"国内焦点","channelId":"5572a108b3cdc86cf39001cd","link":"https://news.163.com/19/0119/09/E5SF5JDF0001875N.html","img":"http://cms-bucket.ws.126.net/2019/01/19/148c6d3c96394d63b97330cc091a2e48.jpg","id":"821ae01b0d2fb0c42bc40549feca4022","havePic":true,"title":"外交部发言人陆慷的另一个身份:＂边学边做＂的新人","imageurls":[{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/148c6d3c96394d63b97330cc091a2e48.jpg"},{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/8dadc1b65c44406895734b1bae13d75d.jpg"},{"height":0,"width":0,"url":"http://static.ws.126.net/cnews/css13/img/end_news.png"}],"source":"网易新闻"},{"pubDate":"2019-01-19 04:45:00","channelName":"国内焦点","channelId":"5572a108b3cdc86cf39001cd","link":"https://news.163.com/19/0119/04/E5RVP9CG0001899N.html","img":"http://cms-bucket.ws.126.net/2019/01/19/f2323522d37f4917be2bf8f1135973b6.jpeg?imageView&thumbnail=550x0","id":"e82a60a8e1341c796b11268f79982d54","havePic":true,"title":"中美＂休战期＂美国得利？外媒：中国才是最大赢家","imageurls":[{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/f2323522d37f4917be2bf8f1135973b6.jpeg?imageView&thumbnail=550x0"},{"height":0,"width":0,"url":"http://static.ws.126.net/cnews/css13/img/end_news.png"}],"source":"网易新闻"},{"pubDate":"2019-01-19 02:44:00","channelName":"国内焦点","channelId":"5572a108b3cdc86cf39001cd","link":"https://news.163.com/19/0119/02/E5RORE8C0001899N.html","img":"http://static.ws.126.net/cnews/css13/img/end_news.png","id":"da91ab69bbde4e803f11efa0993e1071","havePic":true,"title":"央行1周净投放万亿 媒体:非大水漫灌而是市场缺钱","imageurls":[{"height":0,"width":0,"url":"http://static.ws.126.net/cnews/css13/img/end_news.png"}],"source":"网易新闻"},{"pubDate":"2019-01-19 02:03:00","channelName":"国内焦点","channelId":"5572a108b3cdc86cf39001cd","link":"https://news.163.com/19/0119/02/E5RMFB420001899N.html","img":"http://static.ws.126.net/cnews/css13/img/end_news.png","id":"74bede8e7a47efb29cec67497f9c2276","havePic":true,"title":"政法委是什么、干什么、管什么?有了最权威的答案","imageurls":[{"height":0,"width":0,"url":"http://static.ws.126.net/cnews/css13/img/end_news.png"}],"source":"网易新闻"},{"pubDate":"2019-01-19 01:54:00","channelName":"国内焦点","channelId":"5572a108b3cdc86cf39001cd","link":"https://news.163.com/19/0119/01/E5RLV69F0001899N.html","img":"http://cms-bucket.ws.126.net/2019/01/19/3e3bbb186e614e3fa33a761519e45fa8.jpg","id":"ba2d1237b769be7d9fbe474d9a6a7f3c","havePic":true,"title":"美不排除派航母通过台湾海峡 台军这样回应","imageurls":[{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/3e3bbb186e614e3fa33a761519e45fa8.jpg"},{"height":0,"width":0,"url":"http://static.ws.126.net/cnews/css13/img/end_news.png"}],"source":"网易新闻"}],"source":"网易新闻"}]
             * currentPage : 1
             * allNum : 34665
             * maxResult : 20
             */

            private int allPages;
            private int currentPage;
            private int allNum;
            private int maxResult;
            private List<ContentlistBean> contentlist;

            public int getAllPages() {
                return allPages;
            }

            public void setAllPages(int allPages) {
                this.allPages = allPages;
            }

            public int getCurrentPage() {
                return currentPage;
            }

            public void setCurrentPage(int currentPage) {
                this.currentPage = currentPage;
            }

            public int getAllNum() {
                return allNum;
            }

            public void setAllNum(int allNum) {
                this.allNum = allNum;
            }

            public int getMaxResult() {
                return maxResult;
            }

            public void setMaxResult(int maxResult) {
                this.maxResult = maxResult;
            }

            public List<ContentlistBean> getContentlist() {
                return contentlist;
            }

            public void setContentlist(List<ContentlistBean> contentlist) {
                this.contentlist = contentlist;
            }

            public static class ContentlistBean {
                /**
                 * pubDate : 2019-01-19 09:14:00
                 * channelName : 国内焦点
                 * channelId : 5572a108b3cdc86cf39001cd
                 * link : https://news.163.com/19/0119/09/E5SF5JDF0001875N.html
                 * img : http://cms-bucket.ws.126.net/2019/01/19/148c6d3c96394d63b97330cc091a2e48.jpg
                 * id : 821ae01b0d2fb0c42bc40549feca4022
                 * havePic : true
                 * title : 外交部发言人陆慷的另一个身份:＂边学边做＂的新人
                 * imageurls : [{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/148c6d3c96394d63b97330cc091a2e48.jpg"},{"height":0,"width":0,"url":"http://cms-bucket.ws.126.net/2019/01/19/8dadc1b65c44406895734b1bae13d75d.jpg"},{"height":0,"width":0,"url":"http://static.ws.126.net/cnews/css13/img/end_news.png"}]
                 * source : 网易新闻
                 */

                private String pubDate;
                private String channelName;
                private String channelId;
                private String link;
                private String id;
                private String title;
                private String source;
                private List<ImageurlsBean> imageurls;

                public String getPubDate() {
                    return pubDate;
                }

                public void setPubDate(String pubDate) {
                    this.pubDate = pubDate;
                }

                public String getChannelName() {
                    return channelName;
                }

                public void setChannelName(String channelName) {
                    this.channelName = channelName;
                }

                public String getChannelId() {
                    return channelId;
                }

                public void setChannelId(String channelId) {
                    this.channelId = channelId;
                }

                public String getLink() {
                    return link;
                }

                public void setLink(String link) {
                    this.link = link;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getSource() {
                    return source;
                }

                public void setSource(String source) {
                    this.source = source;
                }

                public List<ImageurlsBean> getImageurls() {
                    return imageurls;
                }

                public void setImageurls(List<ImageurlsBean> imageurls) {
                    this.imageurls = imageurls;
                }

                public static class ImageurlsBean {
                    /**
                     * height : 0
                     * width : 0
                     * url : http://cms-bucket.ws.126.net/2019/01/19/148c6d3c96394d63b97330cc091a2e48.jpg
                     */

                    private int height;
                    private int width;
                    private String url;

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }
                }
            }
        }
    }
}
