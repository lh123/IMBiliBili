package com.lh.imbilibili.model.partion;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuhui on 2016/9/29.
 */

public class PartionHome {

    private Banner banner;
    private List<PartionVideo> recommend;
    @SerializedName("new")
    private List<PartionVideo> newVideo;
    private List<PartionVideo> dynamic;

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public List<PartionVideo> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<PartionVideo> recommend) {
        this.recommend = recommend;
    }

    public List<PartionVideo> getNewVideo() {
        return newVideo;
    }

    public void setNewVideo(List<PartionVideo> newVideo) {
        this.newVideo = newVideo;
    }

    public List<PartionVideo> getDynamic() {
        return dynamic;
    }

    public void setDynamic(List<PartionVideo> dynamic) {
        this.dynamic = dynamic;
    }

    public static class Banner {
        private List<PartionBanner> top;

        public List<PartionBanner> getTop() {
            return top;
        }

        public void setTop(List<PartionBanner> top) {
            this.top = top;
        }
    }
}
