package com.lh.imbilibili.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/9/29.
 */

public class PartionVideo {
    private String title;
    private String cover;
    private String uri;
    private String param;
    @SerializedName("goto")
    private String go_to;
    private String name;
    private int play;
    private int danmaku;
    private int favourite;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getGo_to() {
        return go_to;
    }

    public void setGo_to(String go_to) {
        this.go_to = go_to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlay() {
        return play;
    }

    public void setPlay(int play) {
        this.play = play;
    }

    public int getDanmaku() {
        return danmaku;
    }

    public void setDanmaku(int danmaku) {
        this.danmaku = danmaku;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }
}
