package com.lh.imbilibili.model.bangumi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by home on 2016/8/1.
 */
public class SeasonRecommend {
    private List<Bangumi> list;
    @SerializedName("season_id")
    private String seasonId;
    private String title;

    public List<Bangumi> getList() {
        return list;
    }

    public void setList(List<Bangumi> list) {
        this.list = list;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
