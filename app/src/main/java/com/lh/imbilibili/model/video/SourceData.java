package com.lh.imbilibili.model.video;

import com.google.gson.annotations.SerializedName;

/**
 * Created by home on 2016/8/3.
 */
public class SourceData {

    @SerializedName("av_id")
    private String avId;
    @SerializedName("bangumi_id")
    private String bangumiId;
    private String cid;
    @SerializedName("episode_id")
    private String episodeId;
    @SerializedName("is_default_source")
    private String isDefaultSource;
    @SerializedName("season_id")
    private String seasonId;
    @SerializedName("source_id")
    private String sourceId;
    private String website;
    @SerializedName("webvideo_id")
    private String webvideoId;

    public String getAvId() {
        return avId;
    }

    public void setAvId(String avId) {
        this.avId = avId;
    }

    public String getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(String bangumiId) {
        this.bangumiId = bangumiId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public String getIsDefaultSource() {
        return isDefaultSource;
    }

    public void setIsDefaultSource(String isDefaultSource) {
        this.isDefaultSource = isDefaultSource;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebvideoId() {
        return webvideoId;
    }

    public void setWebvideoId(String webvideoId) {
        this.webvideoId = webvideoId;
    }
}
