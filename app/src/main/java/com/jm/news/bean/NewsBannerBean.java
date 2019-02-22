package com.jm.news.bean;

import java.util.List;

public class NewsBannerBean {

    private List<String> bannerTitles;
    private List<String> bannerImages;
    private List<String> bannerUrls;

    public List<String> getBannerTitles() {
        return bannerTitles;
    }

    public void setBannerTitles(List<String> bannerTitles) {
        this.bannerTitles = bannerTitles;
    }

    public List<String> getBannerImages() {
        return bannerImages;
    }

    public void setBannerImages(List<String> bannerImages) {
        this.bannerImages = bannerImages;
    }

    public List<String> getBannerUrls() {
        return bannerUrls;
    }

    public void setBannerUrls(List<String> bannerUrls) {
        this.bannerUrls = bannerUrls;
    }
}
